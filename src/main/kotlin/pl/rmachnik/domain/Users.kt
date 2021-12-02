package pl.rmachnik.domain

import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

class Users(private val userRepository: UserRepository) {
    fun add(newUser: UserDto): User {
        logger.info { "Creating new user with login: ${newUser.login}." }
        userRepository.byLogin(newUser.login)
            ?.run { throw RuntimeException("User with login ${newUser.login} already exists.") }
        userRepository.byName(newUser.name)
            ?.run { throw RuntimeException("User with name ${newUser.name} already exists.") }

        val createdUser = User.create(newUser)
        userRepository.create(createdUser)
        logger.info { "New user created ${newUser.login}." }
        return createdUser
    }

    fun byId(userId: UUID): User? {
        return userRepository.byId(userId)
    }

    fun delete(userId: UUID, jobOfferRepository: JobOfferRepository) {
        //this should be single transaction
        val jobOffers = jobOfferRepository.byUser(userId)
        kotlin.runCatching {
            jobOffers.forEach { jobOfferRepository.delete(it) }
            userRepository.delete(userId)
        }
            .onSuccess { logger.info { "User $userId was successfully deleted." } }
            .onFailure {
                logger.error { "Something went wrong with deleting user $userId. Please check db and make sure that state is correct for that user." }
                throw it
            }
    }

    fun all(): List<User> {
        return userRepository.all()
    }

    fun update(update: User) {
        userRepository.update(update)
    }

    fun byName(name: String): User? {
        return userRepository.byName(name)
    }
}
