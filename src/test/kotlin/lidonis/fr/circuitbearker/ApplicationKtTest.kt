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
import kotlinx.coroutines.delay
import lidonis.fr.circuitbearker.domain.BearsServices
import lidonis.fr.circuitbearker.domain.model.Bear
import lidonis.fr.circuitbearker.persitence.InMemoryBearRepository
import lidonis.fr.circuitbearker.plugins.BearsResource
import lidonis.fr.circuitbearker.plugins.BearsResource.Id
import lidonis.fr.circuitbearker.plugins.configureRouting
import lidonis.fr.circuitbearker.plugins.configureSerialization
import java.util.*
import kotlin.time.Duration.Companion.seconds

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
        testApplication {
            val (bearRepository, bear) = initBearRepo("Baloo")

            initApp(bearRepository)

            val response = httpClient().get(bear.toIdResource())

            assertSoftly {
                response shouldHaveStatus HttpStatusCode.OK
                val body = response.bodyAsText()
                body.shouldContainJsonKeyValue("$.id", bear.id.toString())
                body.shouldContainJsonKeyValue("$.name", bear.name)
                body.shouldContainJsonKeyValue("$.state", "Awake")
            }
        }
    }

    should("hibernate a bear") {
        testApplication {
            val (bearRepository, bear) = initBearRepo("Jojo")

            initApp(bearRepository)

            val idResource = bear.toIdResource()
            var response = httpClient().put(Id.Status(idResource)) {
                contentType(ContentType.Application.Json)
                setBody(Id.StatusRequest("hibernate"))
            }
            response shouldHaveStatus HttpStatusCode.OK

            response = httpClient().get(idResource)
            response shouldHaveStatus HttpStatusCode.ServiceUnavailable


            delay(2.seconds)
            response = httpClient().get(idResource)
            response shouldHaveStatus HttpStatusCode.OK
        }
    }


})

private fun Bear.toIdResource() = Id(id = id.toString())

private fun initBearRepo(bearName: String): Pair<InMemoryBearRepository, Bear> {
    val bearRepository = InMemoryBearRepository()
    val bear = Bear.create(bearName)
    bearRepository.save(bear)
    return Pair(bearRepository, bear)
}

private fun ApplicationTestBuilder.initApp(bearRepository: InMemoryBearRepository) {
    environment {
        config = ApplicationConfig("application-custom.conf")
    }
    application {
        configureRouting(BearsServices(bearRepository))
        configureSerialization()
    }
}

private fun ApplicationTestBuilder.httpClient() = createClient {
    install(Resources)
    install(ContentNegotiation) {
        json()
    }
}
