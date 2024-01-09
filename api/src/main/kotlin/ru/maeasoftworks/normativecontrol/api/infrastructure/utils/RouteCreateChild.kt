package ru.maeasoftworks.normativecontrol.api.infrastructure.utils

import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext

fun Route.createChild(
    evaluation: (context: RoutingResolveContext, segmentIndex: Int) -> RouteSelectorEvaluation
): Route = createChild(
    object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation = evaluation(context, segmentIndex)
    }
)