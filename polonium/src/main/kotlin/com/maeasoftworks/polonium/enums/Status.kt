package com.maeasoftworks.polonium.enums

/**
 * Document processing status. Any document can have 1 status at a time.
 * @see com.maeasoftworks.polonium.model.DocumentData
 * @author prmncr
 */
enum class Status {
    /**
     * Document ready to be queued
     * @author prmncr
     */
    READY_TO_ENQUEUE,

    /**
     * Document is waiting to be queued
     * @author prmncr
     */
    QUEUE,

    /**
     * Document is processing now
     * @author prmncr
     */
    PROCESSING,

    /**
     * Document processing failed
     * @author prmncr
     */
    ERROR,

    /**
     * Document processing was successful
     * @author prmncr
     */
    READY,

    /**
     * Document rendering failed but processing was successful
     * @author prmncr
     */
    RENDER_ERROR,

    /**
     * Document status is unknown
     * @author prmncr
     */
    UNDEFINED,

    /**
     * Document processed, saved and ready to give result
     * @author prmncr
     */
    SAVED
}
