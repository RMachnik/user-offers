package pl.rmachnik.domain

import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

data class JobOfferDto(
    val category: Category,
    val startDate: String,
    val endDate: String
)

class User private constructor(
    val id: UUID,
    val login: String,
    val password: ByteArray,
    val name: String,
    val creationDate: OffsetDateTime
) {

    companion object {
        fun create(userDto: UserDto): User {
            return User(
                UUID.randomUUID(),
                userDto.login,
                userDto.password.toByteArray(),
                userDto.name,
                OffsetDateTime.now(Clock.systemUTC())
            )
        }

        fun update(userId: UUID, userDto: UserDto): User {
            return User(
                userId,
                userDto.login,
                userDto.password.toByteArray(),
                userDto.name,
                OffsetDateTime.now(Clock.systemUTC())
            )
        }
    }

    fun jobOffers(jobOfferRepository: JobOfferRepository): List<JobOffer> {
        return jobOfferRepository.byUser(id)
    }

    fun addJobOffer(newOffer: JobOfferDto, jobOfferRepository: JobOfferRepository): JobOffer {
        val createdOffer = JobOffer.create(newOffer, id)
        jobOfferRepository.create(createdOffer)
        return createdOffer
    }
}

