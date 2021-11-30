package pl.dna

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import pl.dna.rest.OfferController
import pl.dna.rest.UserCUserController

object Application {

    val app: Javalin = Javalin.create().apply {
        exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
        error(404) { ctx -> ctx.json("not found") }
    }

    init {
        app.routes {
            get("/") {
                it.result("Hello!")
            }
            crud("users/{user-id}", UserCUserController())
            path("/offers") {
                val offerController = OfferController()
                get("/", offerController::getAll)
                post("/", offerController::create)
                delete("/{offerId}", offerController::delete)
            }
        }
    }
}
