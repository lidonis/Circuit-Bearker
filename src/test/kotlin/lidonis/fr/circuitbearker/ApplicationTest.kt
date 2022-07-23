package lidonis.fr.circuitbearker

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import lidonis.fr.circuitbearker.plugins.BearsResource
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun `create new bear`() = testApplication {
        val response = httpClient().post(BearsResource()) {
            contentType(ContentType.Application.Json)
            setBody(BearsResource.BearRequest("Baloo"))
        }

        response.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertContains(bodyAsText(),"Baloo")
        }
    }

    private fun ApplicationTestBuilder.httpClient() = createClient {
        install(Resources)
        install(ContentNegotiation) {
            json()
        }
    }
}
