package ru.maeasoftworks.normativecontrol.api.app.web.utlis

import io.ktor.http.parseAndSortHeader
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.util.KtorDsl

@KtorDsl
fun Route.header(name: String, build: Route.() -> Unit): Route {
    val selector = HttpHeaderNameRouteSelector(name)
    return createChild(selector).apply(build)
}

data class HttpHeaderNameRouteSelector(val name: String) : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        val headers = context.call.request.headers[name]
        val parsedHeaders = parseAndSortHeader(headers)
        val header = parsedHeaders.firstOrNull { it.value.isNotBlank() } ?: return RouteSelectorEvaluation.FailedParameter
        return RouteSelectorEvaluation.Success(header.quality)
    }
}