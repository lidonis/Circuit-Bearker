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
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockkStatic
import lidonis.fr.circuitbearker.plugins.BearsResource
import java.util.*

class ApplicationKtTest : ShouldSpec({

    should("create a bear") {
        testApplication {
            mockkStatic(UUID::randomUUID)
            val fixedUUID = "2f87befc-6899-4791-bb92-3a5e037bcfa4"
            every { UUID.randomUUID() } returns UUID.fromString(fixedUUID)

            val response = httpClient().post(BearsResource()) {
                contentType(ContentType.Application.Json)
                setBody(BearsResource.BearRequest("Baloo"))
            }

            assertSoftly {
                response shouldHaveStatus HttpStatusCode.Created
                response.shouldHaveHeader(HttpHeaders.Location, value = "/bears/$fixedUUID")
                val body = response.bodyAsText()
                body.shouldContainJsonKeyValue("$.id", fixedUUID)
                body.shouldContainJsonKeyValue("$.name", "Baloo")
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
