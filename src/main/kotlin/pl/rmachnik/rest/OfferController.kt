package pl.rmachnik.rest

import io.javalin.http.Context
import pl.rmachnik.domain.*
import java.time.format.DateTimeFormatter.ISO_DATE
import java.util.*

class OfferController(private val users: Users, private val jobOfferRepository: JobOfferRepository) {

    fun create(ctx: Context) {
        kotlin.runCatching {
            val userId = ctx.pathParam("user-id")
            val jobOfferDto = ctx.bodyAsClass<JobOfferDto>()
            users.byId(UUID.fromString(userId))?.addJobOffer(jobOfferDto, jobOfferRepository)
        }
            .onSuccess { createdOffer ->
                createdOffer?.let {
                    ctx.json(
                        JobOfferDto(
                            it.category, it.startDate.format(ISO_DATE), it.endDateTime.format(
                                ISO_DATE
                            )
                        )
                    )
                } ?: ctx.status(400)
            }
            .onFailure { respondWithError(ctx, it) }
    }

    fun getAll(ctx: Context) {
        kotlin.runCatching {
            //we potentially here could support multiple names and multiple categories, but for now we do single
//            val name = ctx.queryParams("name")
            val name = ctx.queryParam("name")
            val category = ctx.queryParam("category")?.let { Category.valueOf(it) }

            val validOffersByUserName = byUserName(name).filter { it.isValid() }
            val filteredOffers = category?.let { cat ->
                validOffersByUserName.filter { it.category == cat }
            } ?: validOffersByUserName
            filteredOffers.map {
                JobOfferDto(
                    it.category, it.startDate.format(ISO_DATE), it.endDateTime.format(ISO_DATE)
                )
            }

        }
            .onSuccess { ctx.json(it) }
            .onFailure { respondWithError(ctx, it) }
    }

    private fun byUserName(name: String?): List<JobOffer> {
        return name?.let {
            users.byName(it)?.jobOffers(jobOfferRepository) ?: emptyList()
        } ?: jobOfferRepository.all()
    }
}
