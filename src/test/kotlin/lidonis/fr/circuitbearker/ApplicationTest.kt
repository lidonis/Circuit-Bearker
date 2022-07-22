package lidonis.fr.circuitbearker

import io.ktor.client.plugins.resources.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import lidonis.fr.circuitbearker.plugins.BearsResource
import lidonis.fr.circuitbearker.plugins.configureRouting
import lidonis.fr.circuitbearker.plugins.configureSerialization
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun `create new bear`() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

       val response = client.post(BearsResource(name = "Baloo"))

        response.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}