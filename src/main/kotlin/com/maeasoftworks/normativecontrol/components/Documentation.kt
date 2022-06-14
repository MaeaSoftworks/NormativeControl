package com.maeasoftworks.normativecontrol.components

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.docx4nc.enums.MistakeType
import com.maeasoftworks.normativecontrol.dao.Mistake
import com.maeasoftworks.normativecontrol.dto.documentation.AuthDocs
import com.maeasoftworks.normativecontrol.dto.documentation.DocumentDocs
import com.maeasoftworks.normativecontrol.dto.documentation.QueueDocs
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.BodyParam
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.PossibleResponse
import com.maeasoftworks.normativecontrol.dto.documentation.objects.*
import com.maeasoftworks.normativecontrol.dto.request.LoginRequest
import com.maeasoftworks.normativecontrol.dto.request.RegistrationRequest
import com.maeasoftworks.normativecontrol.dto.request.TokenRefreshRequest
import com.maeasoftworks.normativecontrol.dto.response.*
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaGetter

@Component
class Documentation {
    val methods = createControllerDocs(AuthDocs::class) +
        createControllerDocs(DocumentDocs::class) +
        createControllerDocs(QueueDocs::class)

    val entities = arrayOf(
        createObjectDocs(FileResponse::class),
        createObjectDocs(Mistake::class, true),
        createObjectDocs(MistakesResponse::class),
        createObjectDocs(StatusResponse::class),
        createObjectDocs(QueueResponse::class),
        createObjectDocs(RegistrationRequest::class),
        createObjectDocs(LoginRequest::class),
        createObjectDocs(JwtResponse::class),
        createObjectDocs(TokenRefreshRequest::class),
        createObjectDocs(TokenRefreshResponse::class)
    )

    fun enumToList(clazz: KClass<*>, translatable: Boolean = false): List<String> {
        val enum = ((clazz.members.first { it.name == "values" }.call()) as Array<*>)
            .map { if (!translatable) (it as Enum<*>).toString() else "${(it as Enum<*>)} — ${(it as MistakeType).ru}" }
            .toMutableList()
        enum.sortBy { it }
        return enum
    }

    private final fun createControllerDocs(clazz: KClass<*>): List<MethodInfo> {
        val functions = clazz.functions.filter { x -> x.annotations.any { it is Documented } }
        val funNames = functions.map { it.name }
        val superclass = clazz.superclasses[0]
        val superFunctions = superclass.functions.filter { it.name in funNames }
        val infos = ArrayList<MethodInfo>()
        val root = (superclass.annotations.first { it is RequestMapping } as RequestMapping).value[0]
        for (f in functions.indices) {
            val info = MethodInfo()

            val mappings = listOf(
                superFunctions[f].annotations.firstOrNull { it is GetMapping },
                superFunctions[f].annotations.firstOrNull { it is PostMapping },
                superFunctions[f].annotations.firstOrNull { it is PatchMapping },
                superFunctions[f].annotations.firstOrNull { it is PutMapping },
                superFunctions[f].annotations.firstOrNull { it is DeleteMapping },
                superFunctions[f].annotations.firstOrNull { it is RequestMapping }
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

            info.description = (functions[f].annotations.first { it is Documented } as Documented).translationId
            info.root = root

            val queryParams = ArrayList<Parameter>()
            val bodyParams = ArrayList<Parameter>()
            val pathParams = ArrayList<Parameter>()

            for (p in functions[f].parameters.indices) {
                val fParam = functions[f].parameters[p]
                val fSuperParam = superFunctions[f].parameters[p]
                if (fParam.kind == KParameter.Kind.INSTANCE) {
                    continue
                } else {
                    val param = Parameter()
                    param.name = fParam.name
                    param.description =
                        (fParam.annotations.first { it is Documented } as Documented).translationId
                    param.type = (fParam.type.classifier as KClass<*>).simpleName
                    if (fParam.annotations.any { it is BodyParam }) {
                        bodyParams += param
                        param.name =
                            (fParam.annotations.firstOrNull { it is RequestParam } as RequestParam?)?.value.let { if (it != null && it != "") it else param.name }
                    } else if (fSuperParam.annotations.any { it is PathVariable }) {
                        pathParams += param
                        param.name =
                            (fSuperParam.annotations.firstOrNull { it is PathVariable } as PathVariable?)?.value.let { if (it != null && it != "") it else param.name }
                    } else {
                        queryParams += param
                        param.name =
                            (fSuperParam.annotations.firstOrNull { it is RequestParam } as RequestParam?)?.value.let { if (it != null && it != "") it else param.name }
                    }
                }
            }
            info.bodyParams = bodyParams
            info.queryParams = queryParams
            info.pathParams = pathParams

            val responses = ArrayList<Response>()

            for (annotation in functions[f].annotations.filterIsInstance<PossibleResponse>()) {
                val response = Response()
                response.httpStatus = annotation.httpStatus
                response.description = annotation.description
                response.body = annotation.body
                response.type = annotation.type.simpleName!!
                responses.add(response)
            }
            responses.sortBy { it.httpStatus.value() }
            info.responses = responses
            infos.add(info)
        }
        return infos.sortedWith(compareBy({ it.type }, { it.root }, { it.path }))
    }

    private final fun createObjectDocs(clazz: KClass<*>, translatable: Boolean = false): ObjectInfo {
        val info = ObjectInfo()
        val classAnnotation = clazz.annotations.first { it is Documented } as Documented
        info.description = classAnnotation.translationId
        info.name = clazz.simpleName!!
        val properties = ArrayList<PropertyInfo>()
        for (property in clazz.memberProperties) {
            val prop = PropertyInfo()
            if (property.annotations.any { it is DocumentedProperty }) {
                val propertyAnnotation =
                    property.annotations.first { it is DocumentedProperty } as DocumentedProperty
                prop.name = property.javaGetter!!.annotations.firstOrNull { it is JsonProperty }
                    .let { if (it != null) (it as JsonProperty).value else property.name }
                prop.type = (property.returnType.classifier as KClass<*>).simpleName!!
                prop.description = propertyAnnotation.translationId
                if (propertyAnnotation.enum !== Unit::class) {
                    prop.enum = enumToList(propertyAnnotation.enum, translatable)
                    prop.isEnumTranslated = translatable
                }
                properties.add(prop)
            }
        }
        info.properties = properties
        return info
    }
}
