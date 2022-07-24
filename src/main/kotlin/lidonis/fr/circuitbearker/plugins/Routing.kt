package lidonis.fr.circuitbearker.plugins

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import lidonis.fr.circuitbearker.domain.BearsUseCases
import lidonis.fr.circuitbearker.domain.BearsUseCases.CreateCommand
import lidonis.fr.circuitbearker.domain.model.Bear
import lidonis.fr.circuitbearker.plugins.BearsResource.BearRequest
import lidonis.fr.circuitbearker.plugins.BearsResource.BearResponse
import lidonis.fr.circuitbearker.plugins.BearsResource.Id
import java.util.*

@Serializable
@Resource("/bears")
class BearsResource {
    @Serializable
    data class BearRequest(val name: String)

    @Serializable
    data class BearResponse(val id: String, val name: String, val state: String)

    @Serializable
    @Resource("{id}")
    class Id(val parent: BearsResource = BearsResource(), val id: String) {

        @Serializable
        data class StatusRequest(val status: String)

        @Serializable
        @Resource("status")
        class Status(val parent: Id)
    }
}

fun Application.configureRouting(bearsUseCases: BearsUseCases) {
    install(Resources)

    routing {
        post<BearsResource> {
            val bearRequest = call.receive<BearRequest>()
            val bear = bearsUseCases.create(CreateCommand(bearRequest.name))
            call.response.headers.append(HttpHeaders.Location, application.path(bear.id.toResource()))
            call.respond(
                status = HttpStatusCode.Created,
                message = bear.toBearBody()
            )
        }

        get<Id> { bearId ->
            val bear = bearsUseCases.retrieve(Bear.BearId(UUID.fromString(bearId.id)))
            when (bear.state) {
                "Hibernate" -> call.respond(HttpStatusCode.ServiceUnavailable)
                else -> call.respond(bear.toBearBody())
            }
        }

        put<Id.Status> { status ->
            val statusRequest = call.receive<Id.StatusRequest>()
            bearsUseCases.hibernate(Bear.BearId(UUID.fromString(status.parent.id)))
            call.respondText("hibernate bear ${statusRequest.status}")
        }
    }
}

private fun Bear.toBearBody() = BearResponse(
    id = id.value.toString(),
    name = name,
    state = state
)

private inline fun <reified T : Any> Application.path(resource: T) =
    URLBuilder().also { href(resource, it) }.build().encodedPath

private fun Bear.BearId.toResource() = Id(id = this.value.toString())