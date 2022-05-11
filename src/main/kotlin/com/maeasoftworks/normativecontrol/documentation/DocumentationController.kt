package com.maeasoftworks.normativecontrol.documentation

import com.maeasoftworks.normativecontrol.documentation.annotations.*
import com.maeasoftworks.normativecontrol.documentation.objects.*
import com.maeasoftworks.normativecontrol.entities.DocumentError
import org.apache.commons.lang3.reflect.MethodUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.net.InetAddress
import javax.annotation.PostConstruct
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.kotlinFunction

@Controller
@CrossOrigin
@RequestMapping("docs")
@ConditionalOnExpression("\${controllers.documentation}")
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

    fun enumToString(clazz: KClass<*>): String {
        val enum =
            ((clazz.members.first { it.name == "values" }.call()) as Array<*>).map { it.toString() }.toMutableList()
        enum.sort()
        return "<ul>" + enum.joinToString("") { "<li>\"$it\"</li>" } + "</ul>"
    }

    private final fun createControllerDocs(clazz: KClass<*>): List<MethodInfo> {
        val functions = MethodUtils.getMethodsListWithAnnotation(clazz.java, Documentation::class.java)
        val infos = ArrayList<MethodInfo>()
        val root = (clazz.annotations.first { it is RequestMapping } as RequestMapping).value[0]
        for (function in functions) {
            val info = MethodInfo()

            val mappings = listOf(
                function.getAnnotation(GetMapping::class.java),
                function.getAnnotation(PostMapping::class.java),
                function.getAnnotation(PatchMapping::class.java),
                function.getAnnotation(PutMapping::class.java),
                function.getAnnotation(DeleteMapping::class.java),
                function.getAnnotation(RequestMapping::class.java)
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
                    break
                }
            }

            info.description = function.getAnnotation(Documentation::class.java).description
            info.root = root

            val queryParams = ArrayList<Parameter>()
            val bodyParams = ArrayList<Parameter>()

            for (parameter in function.kotlinFunction!!.parameters) {
                if (parameter.kind == KParameter.Kind.INSTANCE) {
                    continue
                } else {
                    val param = Parameter()
                    param.name = parameter.name
                    param.description =
                        (parameter.annotations.first { it is Documentation } as Documentation).description
                    param.type = (parameter.type.classifier as KClass<*>).simpleName
                    if (parameter.annotations.any { it is BodyParam }) {
                        bodyParams.add(param)
                    } else {
                        queryParams.add(param)
                    }
                }
            }
            info.bodyParams = bodyParams
            info.queryParams = queryParams

            val responses = ArrayList<Response>()

            for (annotation in function.kotlinFunction!!.annotations.filterIsInstance<PossibleResponse>()) {
                val response = Response()
                response.httpStatus = annotation.httpStatus
                response.description = annotation.description
                response.body = annotation.body
                response.type = annotation.type.simpleName!!
                responses.add(response)
            }

            for (annotation in function.kotlinFunction!!.annotations.filterIsInstance<PossibleResponseWithEnum>()) {
                val response = Response()
                response.httpStatus = annotation.httpStatus
                response.description = annotation.description
                response.body = annotation.body
                response.type = annotation.type.simpleName!!
                if (annotation.enum.java.isEnum) {
                    response.body += enumToString(annotation.enum)
                }
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
        info.description = classAnnotation.description
        info.name = clazz.simpleName!!
        val properties = ArrayList<PropertyInfo>()
        for (property in clazz.memberProperties) {
            val prop = PropertyInfo()
            if (property.annotations.any { it is PropertyDocumentation }) {
                val propertyAnnotation =
                    property.annotations.first { it is PropertyDocumentation } as PropertyDocumentation
                prop.name = property.name
                prop.type = (property.returnType.classifier as KClass<*>).simpleName!!
                prop.description = propertyAnnotation.description
                if (propertyAnnotation.enum !== Unit::class) {
                    prop.body += enumToString(propertyAnnotation.enum)
                }
                properties.add(prop)
            }
        }
        info.properties = properties
        return info
    }

    val methods = createControllerDocs(DocumentProcessingDocumentation::class)
    val objects = arrayOf(createObjectDocs(DocumentError::class))

    @GetMapping
    fun mainPage(@RequestParam("section") section: String?, model: Model): String {
        model.addAttribute("methods", methods)
        model.addAttribute("objects", objects)
        model.addAttribute("address", address)
        model.addAttribute("sandboxEnabled", false)
        if (section != null && '/' in section && methods.any { it.root + "/" + it.path == section }) {
            model.addAttribute("isMethod", true)
            model.addAttribute("current", methods.first { it.root + "/" + it.path == section })
        } else if (section in objects.map { it.name }) {
            model.addAttribute("isMethod", false)
            model.addAttribute("current", objects.first { it.name == section })
        } else {
            model.addAttribute("current", null)
        }
        return "main"
    }
}