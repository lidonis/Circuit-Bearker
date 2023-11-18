package lidonis.fr.circuitbearker.storage.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.request.receive
import io.ktor.server.resources.Resources
import io.ktor.server.resources.get
import io.ktor.server.resources.href
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import lidonis.fr.circuitbearker.common.resource.BearsResource
import lidonis.fr.circuitbearker.common.resource.BearsResource.BearRequest
import lidonis.fr.circuitbearker.common.resource.BearsResource.BearResponse
import lidonis.fr.circuitbearker.common.resource.BearsResource.Id
import lidonis.fr.circuitbearker.storage.domain.BearsUseCases
import lidonis.fr.circuitbearker.storage.domain.BearsUseCases.CreateCommand
import lidonis.fr.circuitbearker.storage.domain.model.Bear

fun Application.configureRouting(bearsUseCases: BearsUseCases) {
  install(Resources)

  routing {
    post<BearsResource> {
      val bearRequest = call.receive<BearRequest>()
      val bear = bearsUseCases.create(CreateCommand(bearRequest.name))
      call.response.headers.append(HttpHeaders.Location, this@routing.application.path(bear.id.toResource()))
      call.respond(
        status = HttpStatusCode.Created,
        message = bear.toBearBody()
      )
    }

    get<Id> { bearId ->
      val bear = bearsUseCases.retrieve(Bear.BearId(bearId.id))
      when (bear.state) {
        "Hibernate" -> call.respond(HttpStatusCode.ServiceUnavailable)
        else -> call.respond(bear.toBearBody())
      }
    }

    put<Id.Status> { status ->
      val statusRequest = call.receive<Id.StatusRequest>()
      bearsUseCases.hibernate(Bear.BearId(status.parent.id))
      call.respondText("hibernate bear ${statusRequest.status}")
    }
  }
}

private fun Bear.toBearBody() = BearResponse(
  id = id.toString(),
  name = name,
  state = state
)

private inline fun <reified T : Any> Application.path(resource: T) =
  URLBuilder().also { href(resource, it) }.build().encodedPath

private fun Bear.BearId.toResource() = Id(id = this.toString())
