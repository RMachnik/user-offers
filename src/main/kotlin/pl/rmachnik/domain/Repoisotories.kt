package pl.rmachnik.domain

import java.util.*

interface UserRepository {
    fun create(user: User)
    fun byLogin(login: String): User?
    fun byName(name: String): User?
    fun update(user: User)
    fun delete(id: UUID)
    fun all(): List<User>
    fun byId(userId: UUID): User?
}

interface JobOfferRepository {
    fun create(jobOffer: JobOffer)
    fun byUser(createdBy: UUID): List<JobOffer>
    fun all(): List<JobOffer>
    fun delete(jobOffer: JobOffer)
}
