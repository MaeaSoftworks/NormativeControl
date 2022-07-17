package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.Status

/**
 * Document representation
 * @author prmncr
 */
class DocumentData(
    /**
     * File in <code>ByteArray</code> representation
     * @author prmncr
     */
    var file: ByteArray = ByteArray(0),

    /**
     * Document status (QUEUE by default)
     * @see com.maeasoftworks.docx4nc.enums.Status
     * @author prmncr
     */
    var status: Status = Status.QUEUE,

    /**
     * Failure type (NONE by default)
     * @see com.maeasoftworks.docx4nc.model.FailureType
     * @author prmncr
     */
    var failureType: FailureType = FailureType.NONE
)
