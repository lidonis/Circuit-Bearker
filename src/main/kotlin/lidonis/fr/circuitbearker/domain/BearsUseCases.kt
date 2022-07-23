package lidonis.fr.circuitbearker.domain

import lidonis.fr.circuitbearker.domain.model.Bear


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
