package com.maeasoftworks.tellurium.documentation

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.polonium.enums.MistakeType
import com.maeasoftworks.polonium.utils.doUntilCatch
import org.reflections.Reflections
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter


@Component
class DocumentationCreator(private val messageSource: MessageSource) {
    final val controllers: List<Mapping>
    final val entities: List<Entity>

    init {
        val reflections = Reflections("com.maeasoftworks.tellurium")
        val set = reflections.getTypesAnnotatedWith(Documentation::class.java).toMutableList()

        val c = set.filter {
            it.annotations.any { annotation ->
                annotation is RestController
            }
        }

        val e = set.filter {
            it.annotations.all { annotation ->
                annotation !is RestController
            }
        }

        controllers =
            c.map { createControllerDocs(it.kotlin) }.flatten().sortedWith(compareBy({ it.root }, { it.path }))
        entities = e.map { createObjectDocs(it.kotlin) }
    }

    fun enumToList(clazz: KClass<*>, translatable: Boolean = false): List<String> {
        val enum = ((clazz.members.first { it.name == "values" }.call()) as Array<*>)
            .map { if (!translatable) (it as Enum<*>).toString() else "${(it as Enum<*>)} — ${(it as MistakeType).ru}" }
            .toMutableList()
        enum.sortBy { it }
        return enum
    }

    private final fun createControllerDocs(clazz: KClass<*>): List<Mapping> {
        val classname = clazz.simpleName!!
        val functions = clazz.declaredMemberFunctions.toList()
        val infos = ArrayList<Mapping>()
        val root = (clazz.annotations.first { it is RequestMapping } as RequestMapping).value[0]
        for (f in functions.indices) {
            val funcName = functions[f].name
            val info = Mapping()
            val mappings = listOf(
                functions[f].annotations.firstOrNull { it is GetMapping },
                functions[f].annotations.firstOrNull { it is PostMapping },
                functions[f].annotations.firstOrNull { it is PatchMapping },
                functions[f].annotations.firstOrNull { it is PutMapping },
                functions[f].annotations.firstOrNull { it is DeleteMapping },
                functions[f].annotations.firstOrNull { it is RequestMapping }
            )
            for (mapping in mappings) {
                if (mapping != null) {
                    when (mapping) {
                        is GetMapping -> {
                            info.type = "GET"
                            info.path = mapping.value[0]
                        }
                        is PostMapping -> {
                            info.type = "POST"
                            info.path = mapping.value[0]
                        }
                        is PatchMapping -> {
                            info.type = "PATCH"
                            info.path = mapping.value[0]
                        }
                        is PutMapping -> {
                            info.type = "PUT"
                            info.path = mapping.value[0]
                        }
                        is DeleteMapping -> {
                            info.type = "DELETE"
                            info.path = mapping.value[0]
                        }
                        is RequestMapping -> {
                            info.type = mapping.method[0].name
                            info.path = mapping.value[0]
                        }
                    }
                    info.path = root + "/" + info.path
                    info.path = info.path.replace("{", "\$")
                    info.path = info.path.replace("}", "")
                    break
                }
            }
            info.description = ("docs.$classname.$funcName.description")
            info.root = root
            val queryParams = ArrayList<Parameter>()
            val bodyParams = ArrayList<Parameter>()
            val pathParams = ArrayList<Parameter>()
            for (p in functions[f].parameters.indices) {
                val fParam = functions[f].parameters[p]
                if (fParam.kind == KParameter.Kind.INSTANCE) {
                    continue
                } else {
                    val param = Parameter()
                    param.name = fParam.name
                    param.description = "docs.$classname.$funcName.${fParam.name}"
                    param.type = getFullType(fParam.type.classifier as KClass<*>)
                    if (fParam.annotations.any { it is RequestBody }) {
                        bodyParams += param
                        param.name =
                            (fParam.annotations.firstOrNull { it is RequestParam } as RequestParam?)?.value.let { if (it != null && it != "") it else param.name }
                    } else if (fParam.annotations.any { it is PathVariable }) {
                        pathParams += param
                        param.name =
                            (fParam.annotations.firstOrNull { it is PathVariable } as PathVariable?)?.value.let { if (it != null && it != "") it else param.name }
                    } else {
                        queryParams += param
                        param.name =
                            (fParam.annotations.firstOrNull { it is RequestParam } as RequestParam?)?.value.let { if (it != null && it != "") it else param.name }
                    }
                }
            }
            info.bodyParams = bodyParams
            info.queryParams = queryParams
            info.pathParams = pathParams

            val responses = ArrayList<Response>()
            var index = 0

            doUntilCatch<NoSuchMessageException> {
                while (true) {
                    messageSource.getMessage(
                        "docs.$classname.$funcName.response$index.description",
                        arrayOf<Any>(),
                        LocaleContextHolder.getLocale()
                    )
                    index++
                }
            }

            for (i in 0 until index) {
                val response = Response(
                    "docs.$classname.$funcName.response$i.status",
                    "docs.$classname.$funcName.response$i.type",
                    "docs.$classname.$funcName.response$i.description"
                )
                responses.add(response)
            }
            responses.sortBy { it.httpStatus }
            info.responses = responses
            infos.add(info)
        }
        return infos.sortedWith(compareBy({ it.type }, { it.root }, { it.path }))
    }

    private final fun createObjectDocs(clazz: KClass<*>, translatable: Boolean = false): Entity {
        val info = Entity()
        val className = clazz.simpleName!!
        info.name = className
        info.description = "docs.$className.description"
        val properties = ArrayList<Property>()
        for (property in clazz.memberProperties) {
            if (property.javaGetter!!.annotations.any { it is JsonIgnore }) {
                continue
            }
            val prop = Property()
            val type = property.returnType.classifier as KClass<*>
            prop.name = property.javaGetter!!.annotations.firstOrNull { it is JsonProperty }
                .let { if (it != null) (it as JsonProperty).value else property.name }
            prop.type = getFullType(type)
            prop.description = "docs.$className.${property.name}"
            if (type.isSubclassOf(Enum::class)) {
                prop.enum = enumToList(type, translatable)
                prop.isEnumTranslated = translatable
            }
            properties.add(prop)
        }
        info.properties = properties
        return info
    }

    private fun getFullType(clazz: KClass<*>): String {
        return if (clazz.typeParameters.isEmpty()) {
            clazz.simpleName!!
        } else {
            "${clazz.simpleName}<${clazz.typeParameters.joinToString { it.name }}>"
        }
    }
}
