package lidonis.fr.circuitbearker.persitence

import lidonis.fr.circuitbearker.domain.model.Bear

class InMemoryBearRepository {

    private val bearsMap: MutableMap<Bear.BearId, Bear> = mutableMapOf()

    fun save(bear: Bear) {
        bearsMap[bear.id] = bear
    }

    fun getById(id: Bear.BearId) = bearsMap[id]

}