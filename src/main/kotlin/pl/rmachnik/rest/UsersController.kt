package pl.rmachnik.rest

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import pl.rmachnik.domain.JobOfferRepository
import pl.rmachnik.domain.User
import pl.rmachnik.domain.UserDto
import pl.rmachnik.domain.Users
import java.time.format.DateTimeFormatter.ISO_DATE
import java.util.*

class UsersController(private val users: Users, private val jobOfferRepository: JobOfferRepository) : CrudHandler {

    data class CreatedUser(val id: UUID)

    override fun create(ctx: Context) {
        kotlin.runCatching {
            val newUser = ctx.bodyAsClass<UserDto>()
            users.add(newUser)
        }
            .onSuccess {
                ctx.status(201)
                ctx.json(CreatedUser(it.id))
            }
            .onFailure { respondWithError(ctx, it) }
    }

    override fun delete(ctx: Context, resourceId: String) {
        kotlin.runCatching { users.delete(UUID.fromString(resourceId), jobOfferRepository) }
            .onSuccess { ctx.status(200) }
            .onFailure { respondWithError(ctx, it) }
    }


    data class UserInfo(val id: UUID, val login: String, val name: String, val createdDate: String)

    //potentially we can implement some pagination here
    override fun getAll(ctx: Context) {
        kotlin.runCatching {
            users.all().map { UserInfo(it.id, it.login, it.name, it.creationDate.format(ISO_DATE)) }
        }
            .onSuccess { ctx.json(it) }
            .onFailure { respondWithError(ctx, it) }

    }

    override fun getOne(ctx: Context, resourceId: String) {
        kotlin.runCatching {
            users.byId(UUID.fromString(resourceId))
                ?.let { UserInfo(it.id, it.login, it.name, it.creationDate.format(ISO_DATE)) }
        }
            .onSuccess {
                it?.run { ctx.json(it) } ?: run { ctx.status(404) }
            }
            .onFailure { respondWithError(ctx, it) }
    }

    override fun update(ctx: Context, resourceId: String) {
        kotlin.runCatching {
            val userToBeUpdated = ctx.bodyAsClass<UserDto>()
            val updatedUser = User.update(UUID.fromString(resourceId), userToBeUpdated)
            users.update(updatedUser)
            UserInfo(
                updatedUser.id, userToBeUpdated.login, updatedUser.name, updatedUser.creationDate.format(ISO_DATE)
            )
        }
            .onSuccess { ctx.json(it) }
            .onFailure { respondWithError(ctx, it) }
    }
}
