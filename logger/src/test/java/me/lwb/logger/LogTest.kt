package me.lwb.logger

import com.google.gson.GsonBuilder
import junit.framework.TestCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


open class LogTest : TestCase() {

    override fun setUp() {

        Logger.apply {
            level = Logger.LogLevel.VERBOSE
            tag = "LogTest"
            logPrinter = ConsoleLogPrinter()
        }
    }

    /**
     * CSV格式化
     */
    fun testLogFormat() {
        Logger.logPrinter = ConsoleLogPrinter()
            .format(CsvLogFormatter())
            .format { _, _, messageAny, _ -> "【$messageAny】" }


        Logger.i { "Test info" }
    }

    /**
     * JSON格式化
     */
    fun testLogJsonExt() {

        Logger.logJson {
            mapOf(
                "name" to "tom",
                "age" to 19,
            )
        }


    }

    fun testLogJsonLogger() {


        val logger = Logger.jsonLogger()
        logger.d{
            arrayOf("hello","world")
        }
        logger.i {
            mapOf(
                "name" to "tom",
                "age" to 19,
            )
        }
    }

    fun testLogFormatJson() {
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

        Logger.logPrinter = ConsoleLogPrinter()
            .format { _, _, messageAny, _ -> gson.toJson(messageAny) }


        Logger.i {
            mapOf(
                "name" to "tom",
                "age" to 19,
            )
        }
    }

    /**
     * 子线程Log
     */
    fun testLogAtThread() {
        val executorService = Executors.newSingleThreadExecutor()
        Logger.logPrinter = ConsoleLogPrinter()
            .format { _, _, messageAny, _ -> "【${Thread.currentThread().name}】:$messageAny" }
            .logAt(executorService)

        Logger.i { "Test info" }

        executorService.awaitTermination(1, TimeUnit.SECONDS)
    }

    fun testLogAlso() {
        Logger.logPrinter = ConsoleLogPrinter()
            .format(CsvLogFormatter())
            .logAlso(ConsoleLogPrinter().format { _, _, messageAny, _ -> "【$messageAny】" })
            .logAlso(ConsoleLogPrinter().format { _, _, messageAny, _ -> "《$messageAny》" })

        Logger.i { "Test info" }
    }

    fun testLogFilter() {
        Logger.logPrinter =
            ConsoleLogPrinter().format { _, tag, messageAny, _ -> "$tag : $messageAny\n" }
                .logAlso(ConsoleLogPrinter()
                    .format { _, tag, messageAny, _ -> "$tag :【$messageAny】\n" }
                    .filterLevel(Logger.LogLevel.INFO))//仅记录level在INFO及以上的
                .logAlso(ConsoleLogPrinter()
                    .format { _, tag, messageAny, _ -> "$tag :《$messageAny》\n" }
                    .filter { _, tag, _, _ -> tag.contains("CHILD") })//仅记录tag包含CHILD

        Logger.v { "Test verbose" }
        Logger.d { "Test debug" }
        Logger.i { "Test info" }
        Logger.w { "Test warning" }
        Logger.e { "Test error" }
        Logger.wtf { "Test wtf" }


        val childLogger = Logger["CHILD"]
        //子 logger,默认logPrinter、level 会从父Logger获得 ,tag 会变成 LogTest-CHILD

        childLogger.v { "Test verbose" }
        childLogger.d { "Test debug" }
        childLogger.i { "Test info" }
        childLogger.w { "Test warning" }
        childLogger.e { "Test error" }
        childLogger.wtf { "Test wtf" }
    }


    fun testLogLevel() {
        Logger.level = Logger.LogLevel.VERBOSE
        Logger.v { "Test verbose" }
        Logger.d { "Test debug" }
        Logger.i { "Test info" }
        Logger.w { "Test warning" }
        Logger.e { "Test error" }
        Logger.wtf { "Test wtf" }


        Logger.level = Logger.LogLevel.INFO
        Logger.v { "Test verbose" }
        Logger.d { "Test debug" }
        Logger.i { "Test info" }
        Logger.w { "Test warning" }
        Logger.e { "Test error" }
        Logger.wtf { "Test wtf" }

        Logger.level = Logger.LogLevel.WTF
        Logger.v { "Test verbose" }
        Logger.d { "Test debug" }
        Logger.i { "Test info" }
        Logger.w { "Test warning" }
        Logger.e { "Test error" }
        Logger.wtf { "Test wtf" }


    }

    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    fun Logger.jsonLogger() =
        copy(logPrinter = logPrinter.format { _, _, messageAny, _ -> gson.toJson(messageAny) })

    inline fun Logger.logJson(level: Logger.LogLevel = Logger.LogLevel.INFO, any: () -> Any) {
        log(level, block = { gson.toJson(any()) })
    }

    class CsvLogFormatter(dateFormat: String = "yyyy-MM-dd HH:mm:ss.SSS") : LogFormatter {
        private val format = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        override fun invoke(
            level: Logger.LogLevel,
            tag: String,
            messageAny: Any?,
            throwable: Throwable?
        ): String {
            val message = messageAny.toString() + (throwable?.toString() ?: "")
            val date = Date()
            return buildString {
                append(date.time)
                appendItem(format.format(date))
                appendItem("%3s", level.shortName)
                appendItem("%10s", tag)
                appendItem(message.replace("\n", "<br>"))
                append("\n")
            }
        }

        private fun StringBuilder.appendItem(format: String, vararg args: Any) {
            append(",")
            append(String.format(format, *args))
        }

    }

}
















