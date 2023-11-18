package lidonis.fr.circuitbearker.storage.domain

import lidonis.fr.circuitbearker.storage.domain.model.Bear
import lidonis.fr.circuitbearker.storage.persitence.BearRepository
import kotlin.time.Duration.Companion.seconds

interface BearsUseCases {
    data class CreateCommand(val name: String)

    fun create(command: CreateCommand): Bear
    fun retrieve(id: Bear.BearId): Bear
    fun hibernate(id: Bear.BearId)
}

class BearsServices(
    private val bearRepository: BearRepository,
    private val idGenerator: () -> String
) : BearsUseCases {

    override fun create(command: BearsUseCases.CreateCommand) =
        Bear.MamaBear(idGenerator).create(command.name)
            .also(bearRepository::save)

    override fun retrieve(id: Bear.BearId) =
        bearRepository.getById(id) ?: error("Nor bear")

    override fun hibernate(id: Bear.BearId) =
        retrieve(id)
            .hibernate(1.seconds)
            .let(bearRepository::save)
}
