package pl.rmachnik

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import mu.KotlinLogging
import pl.rmachnik.rest.OfferController
import pl.rmachnik.rest.UserCUserController
import java.lang.RuntimeException


object Application {
    private val logger = KotlinLogging.logger {}

    val app: Javalin = Javalin.create().apply {
        exception(Exception::class.java) { e, ctx ->
            logger.info { e }
            ctx.status(500)
            ctx.json(AppException(500, e.stackTraceToString()))
        }
        error(404) {
            it.json(AppException(404, "Not found."))
        }
    }

    init {
        app.routes {
            path("/user-offers/api") {
                get("/") {
                    it.json(Hello("Hi"))
                }
                get("/exception"){
                    throw RuntimeException("Some test exception.")
                }
                crud("/users/{user-id}", UserCUserController())
                path("/offers") {
                    val offerController = OfferController()
                    get("/", offerController::getAll)
                    post("/", offerController::create)
                    delete("/{offerId}", offerController::delete)
                }
            }
        }
    }

    data class Hello(val hello: String)
    data class AppException(val code: Int, val message: String)

}
