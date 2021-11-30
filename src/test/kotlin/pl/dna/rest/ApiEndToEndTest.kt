package pl.dna.rest

import io.javalin.testtools.TestUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.dna.Application


internal class ApiEndToEndTest {
    private val app = Application.app

    @Test
    fun `GET to fetch initial endpoint with hello`() = TestUtil.test(app) { _, client ->
        assertThat(client.get("/").code).isEqualTo(200)
        assertThat(client.get("/").body?.string()).isEqualTo("Hello!")
    }
}
