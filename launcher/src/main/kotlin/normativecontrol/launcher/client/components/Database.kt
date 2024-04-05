package normativecontrol.launcher.client.components

import normativecontrol.launcher.cli.environment.environment
import normativecontrol.launcher.cli.environment.variable
import normativecontrol.launcher.client.entities.Result
import normativecontrol.shared.debug
import normativecontrol.shared.warn
import org.slf4j.LoggerFactory
import java.sql.DriverManager

object Database {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val url: String = environment.variable("nc_db_url")
    private val user: String = environment.variable("nc_db_user")
    private val password: String = environment.variable("nc_db_password")
    private val connection = DriverManager.getConnection(url, user, password)

    init {
        logger.debug { "Database connection to '$url' successfully initialized." }
    }

    fun updateResult(result: Result) {
        val statement = connection.prepareStatement("update results set (status, description) = (?, ?) where id=?")
        statement.setString(1, result.status.name)
        statement.setString(2, result.description)
        statement.setLong(3, result.id)
        val updated = statement.executeUpdate()
        if (updated != 1) {
            logger.warn { "Updated $updated lines" }
        }
    }
}