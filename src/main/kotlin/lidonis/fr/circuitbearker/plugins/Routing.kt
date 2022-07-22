package lidonis.fr.circuitbearker.plugins

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import kotlinx.serialization.Serializable
import lidonis.fr.circuitbearker.domain.Bear
import lidonis.fr.circuitbearker.domain.Bears
import lidonis.fr.circuitbearker.domain.BearsUseCases
import java.util.*

fun Application.configureRouting() {
    install(Resources)

    routing {
        post<BearsResource> { bear ->
            call.respond(Bears.create(BearsUseCases.CreateCommand(bear.name)))
        }

        get<BearsResource.Id> { bearId ->
            call.respond(Bears.retrieve(Bear.BearId(UUID.fromString(bearId.id))) ?: error("No bear"))
        }

        put<BearsResource.Id.Hibernate> { status ->
            call.respondText("hibernate bear ${status.id}")
        }
    }
}

@Serializable
@Resource("/bears")
class BearsResource(val name: String) {

    @Serializable
    @Resource("{id}")
    class Id(val id: String) {

        @Serializable
        @Resource("hibernate")
        class Hibernate(val id: String)
    }
}

