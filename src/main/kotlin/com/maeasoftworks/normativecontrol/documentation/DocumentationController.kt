package com.maeasoftworks.normativecontrol.documentation

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.documentation.annotations.BodyParam
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PossibleResponse
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import com.maeasoftworks.normativecontrol.documentation.objects.*
import com.maeasoftworks.normativecontrol.entities.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.net.InetAddress
import javax.annotation.PostConstruct
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaGetter

@Controller
@CrossOrigin
@RequestMapping("docs")
@ConditionalOnExpression("\${controllers.docs}")
class DocumentationController {
    private lateinit var address: String

    @Value("\${server.port}")
    private lateinit var host: String

    @PostConstruct
    fun init() {
        val loopbackAddress = InetAddress.getLoopbackAddress().hostAddress
        val localAddress = InetAddress.getLocalHost().hostAddress

        address = if (localAddress.startsWith("192.168")) {
            "$loopbackAddress:$host"
        } else {
            loopbackAddress
        }
    }

    fun enumToList(clazz: KClass<*>): List<String> {
        val enum = ((clazz.members.first { it.name == "values" }.call()) as Array<*>)
            .map {(it as Enum<*>).toString() }
            .toMutableList()
        enum.sortBy { it }
        return enum
    }

    private final fun createControllerDocs(clazz: KClass<*>): List<MethodInfo> {
        val functions = clazz.functions.filter { x -> x.annotations.any { it is Documentation } }
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
                superFunctions[f].annotations.firstOrNull { it is RequestMapping })
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

            info.description = (functions[f].annotations.first { it is Documentation } as Documentation).translationId
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
                    param.description = (fParam.annotations.first { it is Documentation } as Documentation).translationId
                    param.type = (fParam.type.classifier as KClass<*>).simpleName
                    if (fSuperParam.annotations.any { it is BodyParam }) {
                        bodyParams += param
                        param.name =
                            (fSuperParam.annotations.first { it is RequestParam } as RequestParam).value.let { if (it != "") it else param.name }
                    } else if (fSuperParam.annotations.any { it is PathVariable }) {
                        pathParams += param
                        param.name =
                            (fSuperParam.annotations.first { it is PathVariable } as PathVariable).value.let { if (it != "") it else param.name }
                    } else {
                        queryParams += param
                        param.name =
                            (fSuperParam.annotations.first { it is RequestParam } as RequestParam).value.let { if (it != "") it else param.name }
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

    private final fun createObjectDocs(clazz: KClass<*>): ObjectInfo {
        val info = ObjectInfo()
        val classAnnotation = clazz.annotations.first { it is Documentation } as Documentation
        info.description = classAnnotation.translationId
        info.name = clazz.simpleName!!
        val properties = ArrayList<PropertyInfo>()
        for (property in clazz.memberProperties) {
            val prop = PropertyInfo()
            if (property.annotations.any { it is PropertyDocumentation }) {
                val propertyAnnotation =
                    property.annotations.first { it is PropertyDocumentation } as PropertyDocumentation
                prop.name = property.javaGetter!!.annotations.firstOrNull { it is JsonProperty }.let { if (it != null) (it as JsonProperty).value else property.name }
                prop.type = (property.returnType.classifier as KClass<*>).simpleName!!
                prop.description = propertyAnnotation.translationId
                if (propertyAnnotation.enum !== Unit::class) {
                    prop.enum = enumToList(propertyAnnotation.enum)
                }
                properties.add(prop)
            }
        }
        info.properties = properties
        return info
    }

    val methods =
        createControllerDocs(DocumentDocs::class) + createControllerDocs(QueueDocs::class)
    val objects = arrayOf(
        createObjectDocs(FileResponse::class),
        createObjectDocs(Mistake::class),
        createObjectDocs(MistakesResponse::class),
        createObjectDocs(StatusResponse::class),
        createObjectDocs(QueueResponse::class)
    )

    @GetMapping
    fun mainPage(@RequestParam("section") section: String?, model: Model): String {
        model.addAttribute("methods", methods)
        model.addAttribute("entities", objects)
        model.addAttribute("address", address)
        model.addAttribute("sandboxEnabled", false)
        if (section != null && "/" in section && methods.any { it.path == section }) {
            model.addAttribute("isMethod", true)
            model.addAttribute("current", methods.first { it.path == section })
        } else if (section in objects.map { it.name }) {
            model.addAttribute("isMethod", false)
            model.addAttribute("current", objects.first { it.name == section })
        } else {
            model.addAttribute("current", null)
        }
        return "main"
    }
}