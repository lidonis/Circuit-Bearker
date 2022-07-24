package lidonis.fr.circuitbearker.domain

import lidonis.fr.circuitbearker.domain.model.Bear
import lidonis.fr.circuitbearker.persitence.BearRepository
import kotlin.time.Duration.Companion.seconds


interface BearsUseCases {
    data class CreateCommand(val name: String)

    fun create(command: CreateCommand): Bear
    fun retrieve(id: Bear.BearId): Bear

    fun hibernate(id: Bear.BearId)
}

class BearsServices(private val bearRepository: BearRepository) : BearsUseCases {

    override fun create(command: BearsUseCases.CreateCommand) =
        Bear.create(command.name)
            .also(bearRepository::save)

    override fun retrieve(id: Bear.BearId) =
        bearRepository.getById(id) ?: error("Nor bear")

    override fun hibernate(id: Bear.BearId) =
        retrieve(id)
            .hibernate(1.seconds)
            .let(bearRepository::save)

}
