package lidonis.fr.circuitbearker.domain

import java.util.UUID


interface BearsUseCases {
    data class CreateCommand(val name: String)

    fun create(command: CreateCommand): Bear.BearId
    fun retrieve(id: Bear.BearId): Bear?
}

object Bears : BearsUseCases {

    private val bearsMap = mutableMapOf<Bear.BearId, Bear>()

    override fun create(command: BearsUseCases.CreateCommand) =
        Bear.BearId().also {
            bearsMap[it] = Bear.AwakeBear(
                id = it,
                name = command.name,
            )
        }

    override fun retrieve(id: Bear.BearId) = bearsMap[id]

}


interface Bear {
    val id: BearId
    val name: String

    @JvmInline
    value class BearId(val value: UUID = UUID.randomUUID())

    data class AwakeBear(override val id: BearId, override val name: String) : Bear
}