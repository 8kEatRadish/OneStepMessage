package com.shawn.oneStepMessage

import android.util.Log
import java.lang.StringBuilder

enum class LogType {
    DEBUG,
    ERROR,
    INFO,
    VERBOSE,
    WARN
}

/**
 * 显示log工具类
 * @param tag show tag
 * @param type show log level
 */
internal fun String.showLog(tag: String, type: LogType = LogType.DEBUG) {
    if (!OSM_SHOW_LOG) return

    val trace: Array<StackTraceElement> = Thread.currentThread().stackTrace
    val where = if (trace == null || trace.isEmpty()) {
        null
    } else {

        var currentIndex = -1
        //在堆栈中找到相应方法
        for (i in trace.indices) {
            if (trace[i].methodName.compareTo("showLog") == 0) {
                currentIndex = i + 3
                break
            }
        }

        //防止越界
        if (currentIndex < trace.size) {
            "${trace[currentIndex].className}.${trace[currentIndex].methodName} (${trace[currentIndex].fileName}:${trace[currentIndex].lineNumber})"
        } else {
            ""
        }
    }
    try {
        when (type) {
            LogType.DEBUG -> {
                Log.d(tag, "|| $where")
                Log.d(tag, "|| $this")
                Log.d(tag, "||${this.length.drawLine()}")
            }
            LogType.ERROR -> {
                Log.e(tag, "|| $where")
                Log.e(tag, "|| $this")
                Log.e(tag, "||${this.length.drawLine()}")
            }
            LogType.INFO -> {
                Log.i(tag, "|| $where")
                Log.i(tag, "|| $this")
                Log.i(tag, "||${this.length.drawLine()}")
            }
            LogType.VERBOSE -> {
                Log.v(tag, "|| $where")
                Log.v(tag, "|| $this")
                Log.v(tag, "||${this.length.drawLine()}")
            }
            LogType.WARN -> {
                Log.w(tag, "|| ${where}")
                Log.w(tag, "|| ${this}")
                Log.w(tag, "||${this.length.drawLine()}")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

private fun Int.drawLine(): String {
    var builder = StringBuilder()
    repeat(this + 1) {
        builder.append("||")
    }
    return builder.toString()
}