package pl.rmachnik.rest

import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.plugin.json.JavalinJackson
import io.javalin.testtools.HttpClient
import io.javalin.testtools.TestUtil
import okhttp3.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import pl.rmachnik.Application
import pl.rmachnik.domain.UserDto

//basic functional end-to-end tests, do not have time for junit tests or other types of tests
internal class UsersControllerTest {
    private val app = Application().app

    @Test
    fun `Check user creation flow`() = TestUtil.test(app) { _, client ->

        val (newUser, addingNewUserResp, addingDuplicatedUserResp) = createUser(client)

        assertThat(addingNewUserResp.code).isEqualTo(201)
        assertThat(addingDuplicatedUserResp.code).isEqualTo(400)
        assertThat(addingDuplicatedUserResp.body!!.string()).isEqualTo("{\"message\":\"User with login zenek123 already exists.\"}")
        assertThat(client.get("/user-offers/api/users/").code).isEqualTo(200)
        val users = JavalinJackson.defaultMapper()
            .readValue<List<UsersController.UserInfo>>(client.get("/user-offers/api/users/").body!!.string())
        assertThat(users[0].login).isEqualTo(newUser.login)
    }

    @Test
    fun `Check that user is deleted`() = TestUtil.test(app) { _, client ->
        val (_, newUserResp, _) = createUser(client)
        val createdUser =
            JavalinJackson.defaultMapper().readValue<UsersController.CreatedUser>(newUserResp.body!!.string())

        assertThat(client.delete("/user-offers/api/users/${createdUser.id}").code).isEqualTo(200)
        assertThat(client.get("/user-offers/api/users/").body!!.string()).isEqualTo("[]")
    }

    @Test
    fun `Check that user is updated`() = TestUtil.test(app) { _, client ->
        val (_, newUserResp, _) = createUser(client)
        val createdUser =
            JavalinJackson.defaultMapper().readValue<UsersController.CreatedUser>(newUserResp.body!!.string())

        val updated = UserDto("updatedZenek", "123", "zenek")
        val updatedResp = client.patch(
            "/user-offers/api/users/${createdUser.id}", JavalinJackson.defaultMapper().writeValueAsString(updated)
        )
        assertThat(updatedResp.code).isEqualTo(200)
        assertThat(client.get("/user-offers/api/users/${createdUser.id}").code).isEqualTo(200)
        assertThat(client.get("/user-offers/api/users/").body!!.string()).contains("updatedZenek")
    }
}

fun createUser(client: HttpClient): Triple<UserDto, Response, Response> {
    val newUser = UserDto("zenek123", "123", "zenek")
    val addingNewUserResp =
        client.post("/user-offers/api/users", JavalinJackson.defaultMapper().writeValueAsString(newUser))
    val addingDuplicatedUserResp =
        client.post("/user-offers/api/users", JavalinJackson.defaultMapper().writeValueAsString(newUser))
    return Triple(newUser, addingNewUserResp, addingDuplicatedUserResp)
}
