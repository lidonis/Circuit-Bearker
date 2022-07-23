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
import lidonis.fr.circuitbearker.domain.Bear
import lidonis.fr.circuitbearker.domain.Bears
import lidonis.fr.circuitbearker.domain.BearsUseCases.CreateCommand
import lidonis.fr.circuitbearker.plugins.BearsResource.*
import java.util.*

@Serializable
@Resource("/bears")
class BearsResource {
    @Serializable
    data class BearRequest(val name: String)

    @Serializable
    data class BearBody(val id: String, val name: String)

    @Serializable
    @Resource("{id}")
    class Id(val parent: BearsResource, val id: String) {

        @Serializable
        @Resource("hibernate")
        class Hibernate(val parent: BearsResource.Id, val id: String)
    }
}

fun Application.configureRouting() {
    install(Resources)

    routing {
        post<BearsResource> {
            val bearRequest = call.receive<BearRequest>()
            val bear = Bears.create(CreateCommand(bearRequest.name))
            call.response.headers.append(HttpHeaders.Location, "/bears/${bear.id.value}")
            call.respond(
                status = HttpStatusCode.Created,
                BearBody(id = bear.id.value.toString(), name = bear.name)
            )
        }

        get<Id> { bearId ->
            val bear = Bears.retrieve(Bear.BearId(UUID.fromString(bearId.id))) ?: error("No bear")
            call.respond(BearBody(id = bear.id.value.toString(), name = bear.name))
        }

        put<Id.Hibernate> { status ->
            call.respondText("hibernate bear ${status.id}")
        }
    }
}
