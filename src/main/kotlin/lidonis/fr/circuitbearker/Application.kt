package lidonis.fr.circuitbearker

import io.ktor.server.application.*
import io.ktor.server.netty.*
import lidonis.fr.circuitbearker.domain.BearsServices
import lidonis.fr.circuitbearker.persitence.InMemoryBearRepository
import lidonis.fr.circuitbearker.plugins.configureRouting
import lidonis.fr.circuitbearker.plugins.configureSerialization

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureRouting(BearsServices(InMemoryBearRepository()))
    configureSerialization()
}
