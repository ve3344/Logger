package me.lwb.logger

import android.util.Log
import java.io.PrintStream


open class EmptyLogPrinter : Logger.LogPrinter {
    override fun log(level: Logger.LogLevel, tag: String, messageAny: Any?, throwable: Throwable?) =
        Unit
}

class ConsoleLogPrinter(lineBreak: Boolean = true) : Logger.LogPrinter {

    val appendLog: (PrintStream, String) -> Unit =
        if (lineBreak) PrintStream::println else PrintStream::print

    override fun log(
        level: Logger.LogLevel,
        tag: String,
        messageAny: Any?,
        throwable: Throwable?
    ) {
        val message = messageAny ?: return
        val target = if (level <= Logger.LogLevel.INFO) System.out else System.err
        appendLog(target, message.toString())
    }

}

/**
 * Android日志记录器
 */
class AndroidLogPrinter() : Logger.LogPrinter {
    override fun log(
        level: Logger.LogLevel,
        tag: String,
        messageAny: Any?,
        throwable: Throwable?
    ) {
        val message = messageAny.toString()
        val androidLogLevel = level.ordinal + 2

        val messageWithError = if (throwable != null) {
            message + "\n" + Log.getStackTraceString(throwable)
        } else message

        Log.println(androidLogLevel, tag, messageWithError)

    }

}

private val isAndroidPlatform by lazy {
    runCatching {
        Class.forName("android.util.Log", false, AndroidLogPrinter::class.java.classLoader)
    }.isSuccess
}

@PublishedApi
internal fun createPlatformDefaultLogPrinter(): Logger.LogPrinter {
    return if (isAndroidPlatform) AndroidLogPrinter() else ConsoleLogPrinter(true)
}