package com.zhixun.portal

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zhixun.portal.databinding.ActivitySettingsBinding
import com.zhixun.portal.util.PrefsManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)

        // 加载当前值
        binding.etServerUrl.setText(prefsManager.getServerUrl())
        binding.tvVersion.text = "v${BuildConfig.VERSION_NAME}"

        // 测试连接
        binding.btnTestConnection.setOnClickListener {
            val url = binding.etServerUrl.text.toString().trim().trimEnd('/')
            if (url.isEmpty()) {
                Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Thread {
                try {
                    val connection = java.net.URL("$url/api/news?page=1&size=1").openConnection()
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    val code = (connection as java.net.HttpURLConnection).responseCode
                    runOnUiThread {
                        if (code in 200..299) {
                            Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "连接失败: HTTP $code", Toast.LENGTH_SHORT).show()
                        }
                    }
                    connection.disconnect()
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "连接失败: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }

        // 保存
        binding.btnSave.setOnClickListener {
            val serverUrl = binding.etServerUrl.text.toString().trim().trimEnd('/')

            if (serverUrl.isEmpty()) {
                Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefsManager.setServerUrl(serverUrl)
            prefsManager.setFrontendUrl(serverUrl)
            prefsManager.setDashboardUrl("$serverUrl/bigscreen/")

            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }
}
