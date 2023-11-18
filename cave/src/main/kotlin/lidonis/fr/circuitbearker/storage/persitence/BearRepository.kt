package lidonis.fr.circuitbearker.storage.persitence

import lidonis.fr.circuitbearker.storage.domain.model.Bear

interface BearRepository {
  fun save(bear: Bear)
  fun getById(id: Bear.BearId): Bear?
}

class InMemoryBearRepository : BearRepository {

  private val bearsMap: MutableMap<Bear.BearId, Bear> = mutableMapOf()

  override fun save(bear: Bear) {
    bearsMap[bear.id] = bear
  }

  override fun getById(id: Bear.BearId) = bearsMap[id]
}
