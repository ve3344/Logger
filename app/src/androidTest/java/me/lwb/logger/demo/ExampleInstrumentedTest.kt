package me.lwb.logger.demo

import androidx.test.ext.junit.runners.AndroidJUnit4
import me.lwb.logger.AndroidLogPrinter
import me.lwb.logger.Logger
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun paltformLogger() {
        Assert.assertTrue(Logger.logPrinter is AndroidLogPrinter)

    }
}