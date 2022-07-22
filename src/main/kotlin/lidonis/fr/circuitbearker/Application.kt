package lidonis.fr.circuitbearker

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import lidonis.fr.circuitbearker.plugins.configureRouting
import lidonis.fr.circuitbearker.plugins.configureSerialization

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureRouting()
    configureSerialization()
}
