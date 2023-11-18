package lidonis.fr.circuitbearker.storage

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import lidonis.fr.circuitbearker.storage.domain.BearsServices
import lidonis.fr.circuitbearker.storage.persitence.InMemoryBearRepository
import lidonis.fr.circuitbearker.storage.plugins.configureRouting
import lidonis.fr.circuitbearker.storage.plugins.configureSerialization
import java.util.UUID

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
  configureRouting(BearsServices(InMemoryBearRepository()) { UUID.randomUUID().toString() })
  configureSerialization()
}
