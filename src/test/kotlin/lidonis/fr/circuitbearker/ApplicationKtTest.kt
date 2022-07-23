package lidonis.fr.circuitbearker

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.ktor.client.shouldHaveHeader
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.ShouldSpec
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockkStatic
import lidonis.fr.circuitbearker.domain.BearsServices
import lidonis.fr.circuitbearker.domain.model.Bear
import lidonis.fr.circuitbearker.persitence.InMemoryBearRepository
import lidonis.fr.circuitbearker.plugins.BearsResource
import lidonis.fr.circuitbearker.plugins.configureRouting
import lidonis.fr.circuitbearker.plugins.configureSerialization
import java.util.*

const val FIXED_UUID = "2f87befc-6899-4791-bb92-3a5e037bcfa4"

class ApplicationKtTest : ShouldSpec({

    should("create a bear") {
        testApplication {
            mockkStatic(UUID::randomUUID)
            every { UUID.randomUUID() } returns UUID.fromString(FIXED_UUID)

            val response = httpClient().post(BearsResource()) {
                contentType(ContentType.Application.Json)
                setBody(BearsResource.BearRequest("Baloo"))
            }

            assertSoftly {
                response shouldHaveStatus HttpStatusCode.Created
                response.shouldHaveHeader(HttpHeaders.Location, value = "/bears/$FIXED_UUID")
                val body = response.bodyAsText()
                body.shouldContainJsonKeyValue("$.id", FIXED_UUID)
                body.shouldContainJsonKeyValue("$.name", "Baloo")
                body.shouldContainJsonKeyValue("$.state", "Awake")
            }
        }
    }

    should("get new bear") {
        val bearRepository = InMemoryBearRepository()
        val bear = Bear.create("Baloo")
        bearRepository.save(bear)
        testApplication {
            environment {
                config = ApplicationConfig("application-custom.conf")
            }
            application {
                configureRouting(BearsServices(bearRepository))
                configureSerialization()
            }

            val response = httpClient().get(BearsResource.Id(id = bear.id.value.toString()))

            assertSoftly {
                response shouldHaveStatus HttpStatusCode.OK
                val body = response.bodyAsText()
                body.shouldContainJsonKeyValue("$.id", bear.id.value.toString())
                body.shouldContainJsonKeyValue("$.name", bear.name)
                body.shouldContainJsonKeyValue("$.state", "Awake")
            }
        }
    }
})

private fun ApplicationTestBuilder.httpClient() = createClient {
    install(Resources)
    install(ContentNegotiation) {
        json()
    }
}
