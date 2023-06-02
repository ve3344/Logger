package me.lwb.logger


/**
 * 日志工具类
 * 1.Logger 日志门面，主要提供便捷的方法
 * 2.LogPrinter 日志输出，主要完成各个输出接口的适配
 * 3.LoggerFactory 日志工厂，主要用于生成子Logger
 */

/**
 * 基础日志记录器
 */

open class Logger(
    var tag: String = "Logger",

    var level: LogLevel = LogLevel.VERBOSE,

    var logPrinter: LogPrinter = createPlatformDefaultLogPrinter(),

    ) {
    inline fun v(block: () -> Any?) =
        log(LogLevel.VERBOSE, null, block)

    inline fun d(block: () -> Any?) =
        log(LogLevel.DEBUG, null, block)

    inline fun i(block: () -> Any?) =
        log(LogLevel.INFO, null, block)

    inline fun w(throwable: Throwable? = null, block: () -> Any?) =
        log(LogLevel.WARNING, throwable, block)

    inline fun e(throwable: Throwable? = null, block: () -> Any?) =
        log(LogLevel.ERROR, throwable, block)

    inline fun wtf(throwable: Throwable? = null, block: () -> Any?) =
        log(LogLevel.WTF, throwable, block)


    fun v(message: Any?) =
        log(LogLevel.VERBOSE, message, null)

    fun d(message: Any?) =
        log(LogLevel.DEBUG, message, null)

    fun i(message: Any?) =
        log(LogLevel.INFO, message, null)

    @JvmOverloads
    fun w(message: Any?, throwable: Throwable? = null) =
        log(LogLevel.WARNING, message, throwable)

    @JvmOverloads
    fun e(message: Any?, throwable: Throwable? = null) =
        log(LogLevel.ERROR, message, throwable)

    @JvmOverloads
    fun wtf(message: Any?, throwable: Throwable? = null) =
        log(LogLevel.WTF, message, throwable)


    fun log(
        level: LogLevel,
        message: Any?,
        throwable: Throwable? = null
    ) {
        if (this.level <= level) {
            logPrinter.log(level, this.tag, message, throwable)
        }
    }

    inline fun log(
        level: LogLevel,
        throwable: Throwable? = null,
        block: () -> Any?
    ) {
        if (this.level <= level) {
            log(level, block(), throwable)
        }
    }

    /**
     * 生产子Logger的方法，必要时可以重新设置
     */
    var loggerFactory: (childTag: String) -> Logger = ::defaultLoggerFactory

    /**
     * 创建子Logger
     * @param subTag 次级tag，一般为模块名
     */
    operator fun get(subTag: String) = loggerFactory(subTag)


    companion object INSTANCE : Logger(tag = "Logger")


    /**
     * 日志等级
     */
    enum class LogLevel(val shortName: String) {
        VERBOSE("V"),
        DEBUG("D"),
        INFO("I"),
        WARNING("W"),
        ERROR("E"),
        WTF("WTF")
    }

    /**
     * 日志输出
     */
    fun interface LogPrinter {
        /**
         * 输出日志
         */
        fun log(
            level: LogLevel,
            tag: String,
            messageAny: Any?,
            throwable: Throwable?
        )
    }

}

internal fun Logger.defaultLoggerFactory(subTag: String) = Logger("$tag-$subTag", level, logPrinter)

/**
 * 拷贝
 */
fun Logger.copy(
    tag: String = this.tag,
    level: Logger.LogLevel = this.level,
    logPrinter: Logger.LogPrinter = this.logPrinter,
    loggerFactory: (childTag: String) -> Logger = ::defaultLoggerFactory,
) = Logger(tag, level, logPrinter).also { it.loggerFactory = loggerFactory }

fun Any.loggerForClass() = Logger[javaClass.simpleName]

