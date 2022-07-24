package lidonis.fr.circuitbearker.domain.model

import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import java.util.*
import kotlin.time.Duration
import kotlin.time.toJavaDuration

sealed interface Bear {
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
        private val hibernateUntil: OffsetDateTime = now() + duration.toJavaDuration()

        override val state: String
            get() = if (now() >= hibernateUntil) {
                "Out of Hibernation"
            } else {
                "Hibernate"
            }
    }


    companion object {
        fun create(name: String): Bear = AwakeBear(BearId(), name)
    }
}