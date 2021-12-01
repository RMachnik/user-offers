package pl.rmachnik.rest

import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.plugin.json.JavalinJackson
import io.javalin.testtools.TestUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import pl.rmachnik.Application
import pl.rmachnik.domain.Category
import pl.rmachnik.domain.JobOfferDto
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//basic functional end-to-end tests, do not have time for junit tests or other types of tests
internal class OfferControllerTest{
    private val app = Application().app

    @Test
    fun `Check user offer creation flow`() = TestUtil.test(app){ _, client->
        createUser(client)
        val users = JavalinJackson.defaultMapper()
            .readValue<List<UsersController.UserInfo>>(client.get("/user-offers/api/users/").body!!.string())

        val fixedTime = Clock.fixed(
            Instant.parse("2018-04-29T10:15:30.00Z"), ZoneId.of("UTC")
        )
        val newOfferBody = JavalinJackson.defaultMapper().writeValueAsString(
            JobOfferDto(
                Category.COURIER,
                LocalDate.now(fixedTime).minusDays(20).format(DateTimeFormatter.ISO_DATE),
                LocalDate.now(fixedTime).plusYears(LocalDate.now().year + 10L).format(DateTimeFormatter.ISO_DATE)
            )
        )

        val addedNewOfferResp = client.post("/user-offers/api/users/${users[0].id}/offers/", newOfferBody)
        assertThat(addedNewOfferResp.code).isEqualTo(200)

        val getAllOffersResp = client.get("/user-offers/api/offers")
        assertThat(getAllOffersResp.code).isEqualTo(200)
        assertThat(getAllOffersResp.body!!.string()).isEqualTo("[{\"category\":\"COURIER\",\"startDate\":\"2018-04-09Z\",\"endDate\":\"4049-04-29Z\"}]")
    }

    @Test
    fun `Check filtering of offers`() = TestUtil.test(app){ _, client->
        createUser(client)
        val users = JavalinJackson.defaultMapper()
            .readValue<List<UsersController.UserInfo>>(client.get("/user-offers/api/users/").body!!.string())

        val fixedTime = Clock.fixed(
            Instant.parse("2018-04-29T10:15:30.00Z"), ZoneId.of("UTC")
        )
        val newOfferBody = JavalinJackson.defaultMapper().writeValueAsString(
            JobOfferDto(
                Category.COURIER,
                LocalDate.now(fixedTime).minusDays(20).format(DateTimeFormatter.ISO_DATE),
                LocalDate.now(fixedTime).plusYears(LocalDate.now().year + 10L).format(DateTimeFormatter.ISO_DATE)
            )
        )

        val addedNewOfferResp = client.post("/user-offers/api/users/${users[0].id}/offers/", newOfferBody)
        assertThat(addedNewOfferResp.code).isEqualTo(200)

        val getAllWithEmptyResp = client.get("/user-offers/api/offers?name=benek")
        assertThat(getAllWithEmptyResp.code).isEqualTo(200)
        assertThat(getAllWithEmptyResp.body!!.string()).isEqualTo("[]")

        val getAllByZenek = client.get("/user-offers/api/offers?name=zenek")
        assertThat(getAllByZenek.code).isEqualTo(200)
        assertThat(getAllByZenek.body!!.string()).isEqualTo("[{\"category\":\"COURIER\",\"startDate\":\"2018-04-09Z\",\"endDate\":\"4049-04-29Z\"}]")


        val getAllByZenekAndWrongCategory = client.get("/user-offers/api/offers?name=zenek&category=OFFICE")
        assertThat(getAllByZenekAndWrongCategory.code).isEqualTo(200)
        assertThat(getAllByZenekAndWrongCategory.body!!.string()).isEqualTo("[]")

        val getAllByCourierCat = client.get("/user-offers/api/offers?category=COURIER")
        assertThat(getAllByCourierCat.code).isEqualTo(200)
        assertThat(getAllByCourierCat.body!!.string()).isEqualTo("[{\"category\":\"COURIER\",\"startDate\":\"2018-04-09Z\",\"endDate\":\"4049-04-29Z\"}]")

        val getAllByZenekAndCourierCat = client.get("/user-offers/api/offers?name=zenek&category=COURIER")
        assertThat(getAllByZenekAndCourierCat.code).isEqualTo(200)
        assertThat(getAllByZenekAndCourierCat.body!!.string()).isEqualTo("[{\"category\":\"COURIER\",\"startDate\":\"2018-04-09Z\",\"endDate\":\"4049-04-29Z\"}]")
    }

    //there are few missing test cases covering validity of the offer check and other corner cases with filtering
}
