package normativecontrol.launcher.client

import com.rabbitmq.client.AMQP
import kotlinx.serialization.json.Json
import normativecontrol.launcher.client.messages.JobConfiguration
import normativecontrol.launcher.utils.exceptionToNullable

data class Job(
    val properties: AMQP.BasicProperties,
    val message: String,
    val jobConfiguration: JobConfiguration
) {
    val source: String
        get() = jobConfiguration.document
    val replyTo: String
        get() = jobConfiguration.replyTo
    val docxName: String
        get() = jobConfiguration.resultDocx
    val htmlName: String
        get() = jobConfiguration.resultHtml

    constructor(properties: AMQP.BasicProperties, body: ByteArray?): this(
        properties,
        body?.let { String(it) } ?: throw NullPointerException("Job ${properties.correlationId} had empty body"),
        exceptionToNullable { Json.decodeFromString<JobConfiguration>(String(body)) }
            ?: throw IllegalArgumentException("Job ${properties.correlationId}: unrecognized message body: ${String(body)}")
    )
}