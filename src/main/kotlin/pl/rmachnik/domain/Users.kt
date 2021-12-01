package pl.rmachnik.domain

import java.util.*

data class UserDto(
    val login: String,
    val password: String,
    val name: String
)

class Users(private val userRepository: UserRepository) {
    fun add(newUser: UserDto): User {
        userRepository.byLogin(newUser.login)
            ?.run { throw RuntimeException("User with login ${newUser.login} already exists.") }
        userRepository.byName(newUser.name)
            ?.run { throw RuntimeException("User with name ${newUser.login} already exists.") }

        val createdUser = User.create(newUser)
        userRepository.create(createdUser)
        return createdUser
    }

    fun byId(userId: UUID): User? {
        return userRepository.byId(userId)
    }

    fun delete(userId: UUID) {
        userRepository.delete(userId)
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
