package me.lwb.logger.demo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.os.Looper
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.lwb.logger.Logger
import me.lwb.logger.logAlso
import me.lwb.logger.loggerForClass

class MainActivity : AppCompatActivity() {
    private val logger = loggerForClass()


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        logger.logPrinter = logger.logPrinter.logAlso(TextViewLogPrinter(findViewById(R.id.log_view)))

        logger.d("Debug")
        logger.i("Info")
        logger.v{"Ig v"}
        logger.w{"Ig w"}
        logger.e{"Ig e"}

        val text=findViewById<EditText>(R.id.text)
        val level=findViewById<Spinner>(R.id.level)
        val log=findViewById<Button>(R.id.log)

        log.setOnClickListener {

            logger.log(Logger.LogLevel.values()[level.selectedItemPosition],text.text.toString())
        }
    }


    class TextViewLogPrinter(private val textView: TextView) : Logger.LogPrinter {
        override fun log(level: Logger.LogLevel, tag: String, messageAny: Any?, throwable: Throwable?) {
            val color = when (level) {
                Logger.LogLevel.VERBOSE -> Color.LTGRAY
                Logger.LogLevel.DEBUG -> Color.BLUE
                Logger.LogLevel.INFO -> Color.GREEN
                Logger.LogLevel.WARNING -> Color.YELLOW
                Logger.LogLevel.ERROR -> Color.RED
                Logger.LogLevel.WTF -> Color.BLACK
            }
            val message = messageAny.toString()
            val messageWithError = if (throwable != null) {
                message + "\n" + Log.getStackTraceString(throwable)
            } else message

            val text: CharSequence = SpannableStringBuilder().append(
                "$tag|$messageWithError\n",
                ForegroundColorSpan(color),
                SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
            )
            doOnMain {
                textView.append(text)
            }
        }

        private fun doOnMain(runnable: Runnable) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                runnable.run()
            } else {
                textView.post(runnable)
            }
        }
    }


}