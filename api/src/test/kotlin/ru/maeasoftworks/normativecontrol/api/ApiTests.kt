package ru.maeasoftworks.normativecontrol.api

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.equals.shouldNotBeEqual
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import ru.maeasoftworks.normativecontrol.api.app.web.dto.CredentialsResponse
import ru.maeasoftworks.normativecontrol.api.app.web.dto.LoginRequest
import ru.maeasoftworks.normativecontrol.api.app.web.dto.RegistrationRequest
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.Database

class ApiTests : ShouldSpec({
    context("registration") {
        should("register new user") {
            applicationTest {
                val response = post("/account/register") {
                    contentType(ContentType.Application.Json)
                    setBody(RegistrationRequest("testEmail@urfu.me", "pwd"))
                }
                response.shouldHaveStatus(200)
            }
        }

        should("not register user with not supported email domain") {
            applicationTest {
                val response = post("/account/register") {
                    contentType(ContentType.Application.Json)
                    setBody(RegistrationRequest("testEmail@unsopported.email", "pwd"))
                }
                response.shouldHaveStatus(HttpStatusCode.BadRequest)
            }
        }

        should("not register user without email or password") {
            applicationTest {
                val response1 = post("/account/register") {
                    contentType(ContentType.Application.Json)
                    setBody(RegistrationRequest("", "pwd"))
                }
                response1.shouldHaveStatus(HttpStatusCode.BadRequest)

                val response2 = post("/account/register") {
                    contentType(ContentType.Application.Json)
                    setBody(RegistrationRequest("testEmail@urfu.me", ""))
                }
                response2.shouldHaveStatus(HttpStatusCode.BadRequest)
            }
        }

        should("not register new user with same email") {
            applicationTest {
                val response1 = post("/account/register") {
                    contentType(ContentType.Application.Json)
                    setBody(RegistrationRequest("testEmail@urfu.me", "pwd"))
                }
                response1.shouldHaveStatus(200)
                val response2 = post("/account/register") {
                    contentType(ContentType.Application.Json)
                    setBody(RegistrationRequest("testEmail@urfu.me", "pwd"))
                }
                response2.shouldHaveStatus(HttpStatusCode.Conflict)
            }
        }
    }

    context("login") {
        should("create new session") {
            applicationTest {
                val user = createUser()
                val response = post("/account/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(user.email, user.password))
                }.body<CredentialsResponse>()
                response.accessToken shouldNotBeEqual user.jwtToken
                response.refreshToken.refreshToken shouldNotBeEqual user.refreshToken
            }
        }

        should("not login user without email or password") {
            applicationTest {
                val response1 = post("/account/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest("", "pwd"))
                }
                response1.shouldHaveStatus(HttpStatusCode.BadRequest)

                val response2 = post("/account/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest("testEmail@urfu.me", ""))
                }
                response2.shouldHaveStatus(HttpStatusCode.BadRequest)
            }
        }
    }
}) {
    class Session {
        lateinit var refreshToken: String
        lateinit var jwtToken: String
        lateinit var email: String
        lateinit var password: String
    }

    companion object {
        private var sessionCounter = 0

        fun applicationTest(fn: suspend HttpClient.() -> Unit) = testApplication {
            application {
                configuredModule(Database) {
                    this {
                        runBlocking {
                            withTransaction {
                                Meta.all().forEach { table ->
                                    runQuery {
                                        QueryDsl.executeScript("""truncate table ${table.tableName()}""")
                                    }
                                }
                            }
                        }
                    }
                }
                applyModules(
                    ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.InMemoryFileStorage,
                    ru.maeasoftworks.normativecontrol.api.infrastructure.web.CORS,
                    ru.maeasoftworks.normativecontrol.api.infrastructure.security.Security,
                    ru.maeasoftworks.normativecontrol.api.infrastructure.web.Serialization,
                    ru.maeasoftworks.normativecontrol.api.infrastructure.web.StatusPages,
                    ru.maeasoftworks.normativecontrol.api.infrastructure.web.Validation,
                    ru.maeasoftworks.normativecontrol.api.infrastructure.web.WebSockets,
                    TestVerificationService,
                    ru.maeasoftworks.normativecontrol.api.app.web.controllers.StudentsController,
                    ru.maeasoftworks.normativecontrol.api.app.web.controllers.AccountController,
                    ru.maeasoftworks.normativecontrol.api.app.web.controllers.InspectorViewController,
                    ru.maeasoftworks.normativecontrol.api.domain.services.AccountService
                )
            }
            environment {
                config = MapApplicationConfig(
                    "ktor.websocket.maxFrameSize" to (5 * 1024 * 1024).toString(),
                    "security.jwt.issuer" to "normative-control.ru",
                    "security.jwt.audience" to "normative-control.ru",
                    "security.jwt.realm" to "normative-control.ru",
                    "security.jwt.secret" to "XRQaA5ABbDbTosUYTW5WRukcQ8+U6mzvNh8mYXFkZ7c=",
                    "security.jwt.jwtTokenExpirationSeconds" to 31536000.toString(),
                    "security.jwt.refreshTokenExpirationSeconds" to 31536000.toString(),
                    "security.verification.expirationSeconds" to 300.toString(),
                    "r2dbc.url" to "r2dbc:h2:mem:///normativecontrol;DB_CLOSE_DELAY=-1"
                )
            }
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
                install(io.ktor.client.plugins.websocket.WebSockets)
            }
            client.fn()
        }

        suspend fun HttpClient.createUser(): Session {
            val session = Session()
            val sessionId = sessionCounter++
            session.email = "user$sessionId@urfu.me"
            session.password = "user$sessionId"
            val response = post("/account/register") {
                contentType(ContentType.Application.Json)
                setBody(RegistrationRequest(session.email, session.password))
            }
            response.body<CredentialsResponse>().apply {
                session.refreshToken = this.refreshToken.refreshToken
                session.jwtToken = this.accessToken
            }
            return session
        }
    }
}