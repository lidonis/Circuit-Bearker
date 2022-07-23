package lidonis.fr.circuitbearker.domain.model

import java.util.*
import kotlin.time.Duration

interface Bear {
    val id: BearId
    val name: String

    @JvmInline
    value class BearId(val value: UUID = UUID.randomUUID())

    fun hibernate(duration: Duration): Bear = HibernateBear(id, name, duration)

    fun wakeUp(): Bear = AwakeBear(id, name)

    private data class AwakeBear(override val id: BearId, override val name: String) : Bear

    private data class HibernateBear(
        override val id: BearId,
        override val name: String,
        val duration: Duration
    ) : Bear

    companion object {
        fun create(name: String): Bear = AwakeBear(BearId(), name)
    }
}