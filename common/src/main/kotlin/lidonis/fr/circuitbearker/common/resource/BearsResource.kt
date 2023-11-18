package lidonis.fr.circuitbearker.common.resource

@kotlinx.serialization.Serializable
@io.ktor.resources.Resource("/bears")
class BearsResource {
  @kotlinx.serialization.Serializable
  data class BearRequest(val name: String)

  @kotlinx.serialization.Serializable
  data class BearResponse(val id: String, val name: String, val state: String)

  @kotlinx.serialization.Serializable
  @io.ktor.resources.Resource("{id}")
  class Id(val parent: BearsResource = BearsResource(), val id: String) {

    @kotlinx.serialization.Serializable
    data class StatusRequest(val status: String)

    @kotlinx.serialization.Serializable
    @io.ktor.resources.Resource("status")
    class Status(val parent: Id)
  }
}
