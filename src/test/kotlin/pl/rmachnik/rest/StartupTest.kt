package pl.rmachnik.rest

import io.javalin.plugin.json.JavalinJackson
import io.javalin.testtools.TestUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import pl.rmachnik.Application


internal class StartupTest {
    private val app = Application.app

    @Test
    fun `Check basic endpoints`() = TestUtility.test(app) { _, client ->
        assertThat(client.get("/user-offers/api/").code).isEqualTo(200)
        assertThat(client.get("/user-offers/api/").body?.string()).isEqualTo(
            JavalinJackson.defaultMapper().writeValueAsString(Application.Hello("Hi"))
        )
        assertThat(client.get("/not-found/").code).isEqualTo(404)
        val exceptionResp = JavalinJackson.defaultMapper().readValue(
            client.get("/user-offers/api/exception").body?.string(),
            Application.AppException::class.java
        )
        assertThat(exceptionResp.code).isEqualTo(500)
        assertThat(exceptionResp.message).contains("RuntimeException")
    }

    @Test
    fun `Check test endpoints`() = TestUtility.test(app) { _, client ->
        assertThat(client.get("/user-offers/api/").code).isEqualTo(200)
        assertThat(client.get("/user-offers/api/").body?.string()).isEqualTo(
            JavalinJackson.defaultMapper().writeValueAsString(Application.Hello("Hi"))
        )
        assertThat(client.get("/not-found/").code).isEqualTo(404)
        val exceptionResp = JavalinJackson.defaultMapper().readValue(
            client.get("/user-offers/api/exception").body?.string(),
            Application.AppException::class.java
        )
        assertThat(exceptionResp.code).isEqualTo(500)
        assertThat(exceptionResp.message).contains("RuntimeException")
    }
}
