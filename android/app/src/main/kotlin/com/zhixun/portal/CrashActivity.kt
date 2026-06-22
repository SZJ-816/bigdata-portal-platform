package com.zhixun.portal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashLog = intent.getStringExtra(EXTRA_CRASH_LOG) ?: "未知错误"
        val logPath = intent.getStringExtra(EXTRA_LOG_PATH) ?: ""

        val scrollView = android.widget.ScrollView(this).apply {
            setPadding(32, 32, 32, 32)
            isFillViewport = true
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
        }

        val title = TextView(this).apply {
            text = "⚠ APP 发生错误"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val path = TextView(this).apply {
            text = "日志文件: $logPath"
            textSize = 11f
            setTextColor(0xFF666666.toInt())
            setPadding(0, 8, 0, 16)
        }

        val logView = TextView(this).apply {
            text = crashLog
            textSize = 11f
            setTextColor(0xFF333333.toInt())
            setPadding(0, 0, 0, 24)
            typeface = android.graphics.Typeface.MONOSPACE
        }

        val restartBtn = Button(this).apply {
            text = "重启应用"
            setOnClickListener {
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                finish()
                if (launchIntent != null) {
                    startActivity(launchIntent)
                }
                Runtime.getRuntime().exit(0)
            }
        }

        val shareBtn = Button(this).apply {
            text = "复制日志"
            setOnClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("crash_log", crashLog)
                clipboard.setPrimaryClip(clip)
                android.widget.Toast.makeText(this@CrashActivity, "已复制到剪贴板", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        val btnLayout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 8)
            }
            layoutParams = params
            addView(restartBtn)
            val params2 = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 0)
            }
            shareBtn.layoutParams = params2
            addView(shareBtn)
        }

        container.addView(title)
        container.addView(path)
        container.addView(logView)
        container.addView(btnLayout)
        scrollView.addView(container)

        setContentView(scrollView)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Runtime.getRuntime().exit(0)
    }

    companion object {
        const val EXTRA_CRASH_LOG = "extra_crash_log"
        const val EXTRA_LOG_PATH = "extra_log_path"
    }
}
