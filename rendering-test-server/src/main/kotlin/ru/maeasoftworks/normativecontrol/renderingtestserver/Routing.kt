package ru.maeasoftworks.normativecontrol.renderingtestserver

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.html.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    style {
                        unsafe {
                            //language=CSS
                            +"""
                                * {
                                    box-sizing: border-box;
                                    margin: 0;
                                    padding: 0;
                                }
                                
                                body {
                                    width: 100vw;
                                    height: 100vh;
                                    display: grid;
                                }
                                
                                iframe {
                                    border: none;
                                    height: inherit;
                                    width: inherit;
                                }
                            """.trimIndent()
                        }
                    }
                    script {
                        unsafe {
                            //language=JavaScript
                            +"""
                                const socket = new WebSocket("ws://localhost:8081");
                                socket.addEventListener("message", (event) => {
                                    if (event.data === "RELOAD") {
                                        document.getElementById("render").contentWindow.location.reload();
                                    }
                                });
                            """.trimIndent()
                        }
                    }
                }
                body {
                    iframe {
                        id = "render"
                        src = "/render"
                    }
                }
            }
        }

        webSocket("/") {
            RenderHolder.channel.receiveAsFlow().collect {
                outgoing.send(Frame.Text(it.name))
            }
        }

        post("/set") {
            RenderHolder.changeRender(call.receiveText())
            call.respond(HttpStatusCode.OK)
        }

        get("/render") {
            call.respondText(RenderHolder.render, ContentType.Text.Html)
        }
    }
}
