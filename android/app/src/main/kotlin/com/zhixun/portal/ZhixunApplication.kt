package com.zhixun.portal

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ZhixunApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupUncaughtExceptionHandler()
    }

    private fun setupUncaughtExceptionHandler() {
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val crashLog = buildCrashLog(throwable)
                val logFile = saveCrashLog(crashLog)

                // Launch an error dialog activity
                val intent = Intent(this, CrashActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    putExtra(CrashActivity.EXTRA_CRASH_LOG, crashLog)
                    putExtra(CrashActivity.EXTRA_LOG_PATH, logFile)
                }
                startActivity(intent)

                // Kill the process after starting the error activity
                Process.killProcess(Process.myPid())
                System.exit(2)
            } catch (_: Throwable) {
                // Last resort: let the original handler deal with it
                originalHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    private fun buildCrashLog(throwable: Throwable): String {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val stackTrace = sw.toString()

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        return buildString {
            appendLine("=== 智讯 APP 崩溃日志 ===")
            appendLine("时间: $timestamp")
            appendLine("版本: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine("设备: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("系统: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("包名: ${BuildConfig.APPLICATION_ID}")
            appendLine("")
            appendLine("--- 堆栈跟踪 ---")
            append(stackTrace)
            appendLine("")
            appendLine("--- 异常链 ---")
            var cause: Throwable? = throwable.cause
            while (cause != null) {
                appendLine("Caused by: $cause")
                cause = cause.cause
            }
        }
    }

    private fun saveCrashLog(log: String): String {
        return try {
            val crashDir = File(filesDir, "crashes")
            if (!crashDir.exists()) crashDir.mkdirs()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(crashDir, "crash_$timestamp.txt")
            file.writeText(log)
            file.absolutePath
        } catch (_: Throwable) {
            "（文件写入失败）"
        }
    }

    companion object {
        fun getLastCrashLog(context: Context): String? {
            return try {
                val crashDir = File(context.filesDir, "crashes")
                if (!crashDir.exists()) return null
                val files = crashDir.listFiles()?.sortedByDescending { it.lastModified() }
                files?.firstOrNull()?.readText()
            } catch (_: Throwable) {
                null
            }
        }
    }
}
