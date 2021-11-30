package pl.dna.domain

import java.time.OffsetDateTime


enum class Category {
    IT, FOOD_DRINKS, OFFICE, COURIER, SHOP_ASSISTANT
}

data class JobOffer(
    val category: Category,
    val startDate: OffsetDateTime,
    val endDateTime: OffsetDateTime,
    val name: String
)
