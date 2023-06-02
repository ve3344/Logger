package me.lwb.logger

import java.util.concurrent.Executor



/**
 * LogPrinter拓展
 */
/**
 * 拦截器
 * logPrinter 被拦截对象
 */
typealias LogPrinterInterceptor = (logPrinter: Logger.LogPrinter, level: Logger.LogLevel, tag: String, messageAny: Any?, throwable: Throwable?) -> Unit

inline fun Logger.LogPrinter.intercept(crossinline interceptor: LogPrinterInterceptor) =
    Logger.LogPrinter { level, tag, messageAny, throwable ->
        interceptor(this@intercept, level, tag, messageAny, throwable)
    }

typealias LogFormatter=(level: Logger.LogLevel, tag: String, messageAny: Any?, throwable: Throwable?)-> String
/**
 * 设置日志格式
 */
fun Logger.LogPrinter.format(formatter: LogFormatter) =
    intercept { logPrinter, level, tag, messageAny, throwable ->
        val formattedMessage = formatter(level, tag, messageAny, throwable)
        logPrinter.log(level, tag, formattedMessage, throwable)
    }

/**
 * 设置日志记录线程
 */
fun Logger.LogPrinter.logAt(executor: Executor) =
    intercept { logPrinter, level, tag, messageAny, throwable ->
        executor.execute {
            logPrinter.log(level, tag, messageAny, throwable)
        }
    }

/**
 * 添加额外的日志记录器
 */
fun Logger.LogPrinter.logAlso(other: Logger.LogPrinter) =
    intercept { logPrinter, level, tag, messageAny, throwable ->
        logPrinter.log(level, tag, messageAny, throwable)
        other.log(level, tag, messageAny, throwable)
    }

operator fun Logger.plusAssign(other: Logger.LogPrinter){
    logPrinter=logPrinter.logAlso(other)
}
/**
 * 日志过滤
 */
fun Logger.LogPrinter.filter(
    predicate: (
        level: Logger.LogLevel,
        tag: String,
        messageAny: Any?,
        throwable: Throwable?
    ) -> Boolean
) =
    intercept { logPrinter, level, tag, messageAny, throwable ->
        if (predicate(level, tag, messageAny, throwable)) {
            logPrinter.log(level, tag, messageAny, throwable)
        }
    }

/**
 * 日志过滤
 */
fun Logger.LogPrinter.filterLevel(minLevel: Logger.LogLevel) =
    filter { level, _, _, _ -> level >= minLevel }
