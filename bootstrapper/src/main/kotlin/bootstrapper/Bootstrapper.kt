package bootstrapper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Bootstrapper

fun main(args: Array<String>) {
    runApplication<Bootstrapper>(*args)
}
