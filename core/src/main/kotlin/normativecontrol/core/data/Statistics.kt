package normativecontrol.core.data

/**
 * Metrics collected during document verification
 * @property mistakeCount count of mistakes that were found
 */
data class Statistics(
    val mistakeCount: Int
)