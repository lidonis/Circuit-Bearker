package lidonis.fr.circuitbearker.domain

import java.util.UUID
import kotlin.time.Duration


interface BearsUseCases {
    data class CreateCommand(val name: String)
    fun create(command: CreateCommand): Bear
    fun retrieve(id: Bear.BearId): Bear?
}

object Bears : BearsUseCases {

    private val bearsMap = mutableMapOf<Bear.BearId, Bear>()

    override fun create(command: BearsUseCases.CreateCommand): Bear {
        val bear = Bear.create(command.name)
        bearsMap[bear.id] = bear
        return bear
    }

    override fun retrieve(id: Bear.BearId) = bearsMap[id]

}


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

