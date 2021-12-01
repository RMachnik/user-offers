package pl.rmachnik.domain.infra

import pl.rmachnik.domain.JobOffer
import pl.rmachnik.domain.JobOfferRepository
import java.util.*

class InMemoryJobOfferRepo : JobOfferRepository {
    private val jobOffers = mutableListOf<JobOffer>()

    override fun create(jobOffer: JobOffer) {
        jobOffers.add(jobOffer)
    }

    override fun byUser(createdBy: UUID): List<JobOffer> {
        return jobOffers.filter { it.createdBy == createdBy }
    }

    override fun all(): List<JobOffer> {
        return jobOffers.toList()
    }

    override fun delete(jobOffer: JobOffer) {
        jobOffers.removeIf { it.id == jobOffer.id }
    }
}
