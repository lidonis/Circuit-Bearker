package lidonis.fr.circuitbearker.domain

import lidonis.fr.circuitbearker.domain.model.Bear
import lidonis.fr.circuitbearker.persitence.InMemoryBearRepository


interface BearsUseCases {
    data class CreateCommand(val name: String)

    fun create(command: CreateCommand): Bear
    fun retrieve(id: Bear.BearId): Bear?
}

class BearsServices(private val bearRepository: InMemoryBearRepository) : BearsUseCases {

    override fun create(command: BearsUseCases.CreateCommand) =
        Bear.create(command.name)
            .also(bearRepository::save)

    override fun retrieve(id: Bear.BearId) = bearRepository.getById(id)

}
