package lidonis.fr.circuitbearker.domain.model

import java.util.*
import kotlin.time.Duration

interface Bear {
    val id: BearId
    val name: String
    val state: String

    @JvmInline
    value class BearId(val value: UUID = UUID.randomUUID()) {
        constructor(uuidString: String) : this(UUID.fromString(uuidString))
    }

    fun hibernate(duration: Duration): Bear = HibernateBear(id, name, duration)

    fun wakeUp(): Bear = AwakeBear(id, name)

    private data class AwakeBear(override val id: BearId, override val name: String) : Bear {
        override val state = "Awake"
    }

    private data class HibernateBear(
        override val id: BearId,
        override val name: String,
        val duration: Duration,
    ) : Bear {
        override val state = "Hibernate"
    }


    companion object {
        fun create(name: String): Bear = AwakeBear(BearId(), name)
    }
}