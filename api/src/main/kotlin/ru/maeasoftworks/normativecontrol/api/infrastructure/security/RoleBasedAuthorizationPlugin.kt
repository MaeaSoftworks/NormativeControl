package ru.maeasoftworks.normativecontrol.api.infrastructure.security

import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelectorEvaluation
import kotlinx.serialization.json.Json
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.createChild
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.NoAccessException

fun Route.withRoles(vararg roles: Role, build: Route.() -> Unit) {
    val route = createChild { _, _ -> RouteSelectorEvaluation.Transparent }
    route.install(RoleBasedAuthorizationPlugin) {
        requiredRoles.addAll(roles)
    }
    route.build()
}

class RoleBaseAuthorizationConfiguration {
    val requiredRoles = mutableSetOf<Role>()
}

val RoleBasedAuthorizationPlugin = createRouteScopedPlugin("RoleBasedAuthorization", ::RoleBaseAuthorizationConfiguration) {
    on(AuthenticationChecked) { call ->
        val principal = call.principal<JWTPrincipal>() ?: return@on
        val roles = Json.decodeFromString<Array<Role>>(principal.payload.getClaim("role").asString())

        if (pluginConfig.requiredRoles.isNotEmpty() && roles.intersect(pluginConfig.requiredRoles).isEmpty()) {
            throw NoAccessException()
        }
    }
}