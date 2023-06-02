package me.lwb.logger.demo

import android.app.Application
import me.lwb.logger.Logger

/**
 * Created by luowenbin on 2/6/2023.
 */
open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.tag = "Demo"
        Logger.loggerFactory={
            Logger(it, level = Logger.LogLevel.INFO)
        }
    }
}