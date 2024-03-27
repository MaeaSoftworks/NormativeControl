package normativecontrol.launcher.client.components

import normativecontrol.launcher.cli.environment.environment
import normativecontrol.launcher.cli.environment.variable
import normativecontrol.launcher.client.entities.Result
import normativecontrol.launcher.client.entities.results
import normativecontrol.shared.debug
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.jdbc.JdbcDatabase
import org.slf4j.LoggerFactory

object Database {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val url: String = environment.variable("nc_db_url")
    private val user: String = environment.variable("nc_db_user")
    private val password: String = environment.variable("nc_db_password")
    private val database: JdbcDatabase = JdbcDatabase(url, user, password)

    init {
        database.withTransaction {
            Meta.all().forEach {
                database.runQuery {
                    QueryDsl.create(it)
                }
            }
        }
        logger.debug { "Database connection to '$url' successfully initialized." }
    }

    fun updateResult(result: Result) {
        database.withTransaction {
            database.runQuery {
                QueryDsl.update(Meta.results).single(result)
            }
        }
    }
}