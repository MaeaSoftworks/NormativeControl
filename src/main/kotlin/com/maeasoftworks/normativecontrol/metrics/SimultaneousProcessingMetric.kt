package com.maeasoftworks.normativecontrol.metrics

import com.maeasoftworks.normativecontrol.services.DocumentQueue
import org.springframework.stereotype.Component
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean

@Component
@ConditionalOnBean(DocumentQueue::class)
class SimultaneousProcessingMetric(registry: MeterRegistry, queue: DocumentQueue) {
    init { queue.count = registry.gauge("simultaneous.processing.count", queue.count)!! }
}
