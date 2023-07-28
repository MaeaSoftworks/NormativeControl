package com.maeasoftworks.core.enums

/**
 * Document processing status. Any document can have 1 status at a time.
 * @see com.maeasoftworks.core.model.DocumentData
 */
enum class Status {
    /**
     * Document ready to be queued
     */
    READY_TO_ENQUEUE,

    /**
     * Document is waiting to be queued
     */
    QUEUE,

    /**
     * Document is processing now
     */
    PROCESSING,

    /**
     * Document processing failed
     */
    ERROR,

    /**
     * Document processing was successful
     */
    READY,

    /**
     * Document rendering failed but processing was successful
     */
    RENDER_ERROR,

    /**
     * Document status is unknown
     */
    UNDEFINED,

    /**
     * Document processed, saved and ready to give result
     */
    SAVED
}
