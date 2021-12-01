package pl.rmachnik

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import mu.KotlinLogging
import pl.rmachnik.domain.Users
import pl.rmachnik.domain.infra.InMemoryJobOfferRepo
import pl.rmachnik.domain.infra.InMemoryUserRepo
import pl.rmachnik.rest.OfferController
import pl.rmachnik.rest.UsersController


class Application {
    private val logger = KotlinLogging.logger {}

    val app: Javalin = Javalin.create().apply {
        //for some reason that configuration of jackson doesn't work
        //so that I need to convert dates to string to avoid issues
        jacksonObjectMapper().registerModule(JavaTimeModule()).registerModule(Jdk8Module())
        exception(Exception::class.java) { e, ctx ->
            logger.info { e }
            ctx.status(500)
            ctx.json(SystemError(500, e.stackTraceToString()))
        }
        error(404) {
            it.json(SystemError(404, "Not found."))
        }
    }
    private val users = Users(InMemoryUserRepo())
    private val offerController = OfferController(users, InMemoryJobOfferRepo())

    init {
        app.routes {
            path("/user-offers/api") {
                get("/") {
                    it.json(Hello("Hi"))
                }
                get("/exception") {
                    throw RuntimeException("Some test exception.")
                }
                crud("/users/{user-id}", UsersController(users))
                get("/offers", offerController::getAll)
                path("/users/{user-id}/offers") {
                    post("/", offerController::create)
                    delete("/{offer-id}", { })
                }
            }
        }
    }

    data class Hello(val hello: String)
    data class ApiException(val message: String?)
    data class SystemError(val code: Int, val message: String)

}
