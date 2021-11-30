package pl.rmachnik.rest

import io.javalin.Javalin
import io.javalin.core.util.JavalinLogger
import io.javalin.testtools.HttpClient
import io.javalin.testtools.TestCase
import io.javalin.testtools.ThrowingRunnable
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*

//unfortunately I found bug in the original javalin TestUtil implementation - server is not stopped and it is trying to start it again
object TestUtility {

    @JvmField
    var disableJavalinLogging = true

    @JvmField
    var clearCookies = true

    @JvmStatic
    fun test(app: Javalin, testCase: TestCase) {
        if (disableJavalinLogging) {
            JavalinLogger.enabled = false
        }
        if (app.port() == 0) {
            app.start(0)
            val http = HttpClient(app)
            testCase.accept(app, http) // this is where the user's test happens
            if (clearCookies) {
                val endpointUrl = "/clear-cookies-${UUID.randomUUID()}"
                app.delete(endpointUrl) { it.cookieMap().forEach { (k, _) -> it.removeCookie(k) } }
                http.request(endpointUrl) { it.delete() }
            }
            app.stop()
            if (disableJavalinLogging) {
                JavalinLogger.enabled = true
            }
        }
    }

    @JvmStatic
    fun test(testCase: TestCase) {
        test(Javalin.create(), testCase)
    }

    @JvmStatic
    fun captureStdOut(run: ThrowingRunnable): String {
        val out = ByteArrayOutputStream()
        val printStream = PrintStream(out)
        val oldOut = System.out
        val oldErr = System.err
        System.setOut(printStream)
        System.setErr(printStream)
        try {
            run.run()
        } finally {
            System.out.flush()
            System.setOut(oldOut)
            System.setErr(oldErr)
        }
        return out.toString()
    }

}
