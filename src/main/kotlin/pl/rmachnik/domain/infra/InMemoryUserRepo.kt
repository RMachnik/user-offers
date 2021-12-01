package pl.rmachnik.domain.infra

import pl.rmachnik.domain.User
import pl.rmachnik.domain.UserRepository
import java.util.*

class InMemoryUserRepo : UserRepository {

    private val users = mutableListOf<User>()

    override fun create(user: User) {
        users.add(user)
    }

    override fun byLogin(login: String): User? {
        return users.find { it.login == login }
    }

    override fun byName(name: String): User? {
        return users.find { it.name == name }
    }

    override fun update(user: User) {
        users.removeIf { it.id == user.id }
        users.add(user)
    }

    override fun delete(id: UUID) {
        users.removeIf { it.id == id }
    }

    override fun all(): List<User> {
        return users.toList()
    }

    override fun byId(userId: UUID): User? {
        return users.find { it.id == userId }
    }
}
