package lidonis.fr.circuitbearker.plugins

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.put
import kotlinx.serialization.Serializable
import lidonis.fr.circuitbearker.domain.BearsUseCases
import lidonis.fr.circuitbearker.domain.BearsUseCases.CreateCommand
import lidonis.fr.circuitbearker.domain.model.Bear
import lidonis.fr.circuitbearker.plugins.BearsResource.BearBody
import lidonis.fr.circuitbearker.plugins.BearsResource.BearRequest
import lidonis.fr.circuitbearker.plugins.BearsResource.Id
import java.util.*

@Serializable
@Resource("/bears")
class BearsResource {
    @Serializable
    data class BearRequest(val name: String)

    @Serializable
    data class BearBody(val id: String, val name: String, val state: String)

    @Serializable
    @Resource("{id}")
    class Id(val parent: BearsResource = BearsResource(), val id: String) {

        @Serializable
        @Resource("hibernate")
        class Hibernate(val parent: Id, val id: String)
    }
}

fun Application.configureRouting(bearsUseCases: BearsUseCases) {
    install(Resources)

    routing {
        post<BearsResource> {
            val bearRequest = call.receive<BearRequest>()
            val bear = bearsUseCases.create(CreateCommand(bearRequest.name))
            call.response.headers.append(HttpHeaders.Location, "/bears/${bear.id.value}")
            call.respond(
                status = HttpStatusCode.Created,
                message = bear.toBearBody()
            )
        }

        get<Id> { bearId ->
            val bear = bearsUseCases.retrieve(Bear.BearId(UUID.fromString(bearId.id))) ?: error("No bear")
            call.respond(bear.toBearBody())
        }

        put<Id.Hibernate> { status ->
            call.respondText("hibernate bear ${status.id}")
        }
    }
}

private fun Bear.toBearBody() = BearBody(
    id = id.value.toString(),
    name = name,
    state = state
)
