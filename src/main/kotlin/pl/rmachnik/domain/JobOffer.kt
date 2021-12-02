package pl.rmachnik.domain

import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.util.*

enum class Category {
    IT, FOOD_DRINKS, OFFICE, COURIER, SHOP_ASSISTANT
}

class JobOffer private constructor(
    val id: UUID,
    val category: Category,
    val startDate: OffsetDateTime,
    val endDateTime: OffsetDateTime,
    val createdBy: UUID
) {
    companion object {
        fun create(newOffer: JobOfferDto, createdBy: UUID): JobOffer {
            return JobOffer(
                UUID.randomUUID(),
                newOffer.category,
                //workaround for the jackson config issue... assuming that time is parsable
                OffsetDateTime.of(LocalDate.parse(newOffer.startDate), LocalTime.now(), UTC),
                OffsetDateTime.of(LocalDate.parse(newOffer.endDate), LocalTime.now(), UTC),
                createdBy
            )
        }
    }

    fun isValid(): Boolean {
        return startDate.isBefore(OffsetDateTime.now(Clock.systemUTC())) && endDateTime.isAfter(OffsetDateTime.now(Clock.systemUTC()))
    }
}

data class JobOfferDto(
    val category: Category,
    //in general good practise for that would be to have iso format with timezone, it would make things consistent
    val startDate: String,
    //in general good practise for that would be to have iso format with timezone, it would make things consistent
    //due to serializer config issue with javalin I just left it as string
    val endDate: String
)
