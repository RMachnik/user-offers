package pl.rmachnik.rest

import io.javalin.http.Context
import pl.rmachnik.Application

//error message could be improved if needed
fun respondWithError(ctx: Context, it: Throwable) {
    ctx.status(400)
    ctx.json(Application.ApiException(it.message))
}
