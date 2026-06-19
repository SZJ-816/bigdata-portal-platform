package com.zhixun.portal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zhixun.portal.databinding.ActivitySettingsBinding
import com.zhixun.portal.util.NetworkUtil
import com.zhixun.portal.util.PrefsManager
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FIRST_LAUNCH = "extra_first_launch"
    }

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefsManager: PrefsManager
    private lateinit var networkUtil: NetworkUtil
    private var isFirstLaunch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)
        networkUtil = NetworkUtil(this)
        isFirstLaunch = intent.getBooleanExtra(EXTRA_FIRST_LAUNCH, false)

        setupFields()
        setupButtons()
        setupBackPressHandler()

        if (isFirstLaunch) {
            binding.cardFirstLaunch.visibility = View.VISIBLE
        }
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFirstLaunch) {
                    Toast.makeText(this@SettingsActivity, R.string.settings_first_launch_block, Toast.LENGTH_SHORT).show()
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupFields() {
        // Load current values - show server URL as primary field
        val serverUrl = prefsManager.getServerUrl()
        binding.etServerUrl.setText(serverUrl)

        // Pre-fill advanced fields
        binding.etFrontendUrl.setText(prefsManager.getFrontendUrl())
        binding.etApiUrl.setText(serverUrl)

        binding.switchDebugMode.isChecked = prefsManager.isDebugMode()
        binding.tvVersion.text = "${getString(R.string.settings_version)}: ${BuildConfig.VERSION_NAME}"

        // Toggle advanced section
        binding.tvAdvancedToggle.setOnClickListener {
            val isExpanded = binding.layoutAdvanced.visibility == View.VISIBLE
            binding.layoutAdvanced.visibility = if (isExpanded) View.GONE else View.VISIBLE
            binding.tvAdvancedToggle.text = if (isExpanded)
                getString(R.string.settings_advanced_expand)
            else
                getString(R.string.settings_advanced_collapse)
        }
    }

    private fun setupButtons() {
        binding.btnTestConnection.setOnClickListener {
            testConnection()
        }

        binding.btnSave.setOnClickListener {
            saveConfig()
        }
    }

    private fun testConnection() {
        val apiUrl = if (binding.layoutAdvanced.visibility == View.VISIBLE) {
            binding.etApiUrl.text.toString().trim()
        } else {
            binding.etServerUrl.text.toString().trim()
        }

        if (apiUrl.isEmpty()) {
            Toast.makeText(this, R.string.settings_url_empty, Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnTestConnection.isEnabled = false
        binding.btnTestConnection.text = getString(R.string.testing_connection)

        lifecycleScope.launch {
            val result = networkUtil.testServerConnection(apiUrl)
            binding.btnTestConnection.isEnabled = true
            binding.btnTestConnection.text = getString(R.string.settings_test_connection)

            result.fold(
                onSuccess = { msg ->
                    Toast.makeText(this@SettingsActivity, msg, Toast.LENGTH_LONG).show()
                },
                onFailure = { error ->
                    Toast.makeText(
                        this@SettingsActivity,
                        getString(R.string.connection_failed, error.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    private fun saveConfig() {
        val isAdvanced = binding.layoutAdvanced.visibility == View.VISIBLE

        val frontendUrl: String
        val apiUrl: String

        if (isAdvanced) {
            frontendUrl = binding.etFrontendUrl.text.toString().trim()
            apiUrl = binding.etApiUrl.text.toString().trim()
        } else {
            // Single URL mode - use same URL for both
            val url = binding.etServerUrl.text.toString().trim()
            frontendUrl = url
            apiUrl = url
        }

        // Validate
        if (frontendUrl.isEmpty() || apiUrl.isEmpty()) {
            Toast.makeText(this, R.string.settings_url_empty, Toast.LENGTH_SHORT).show()
            return
        }
        if (!isValidUrl(frontendUrl) || !isValidUrl(apiUrl)) {
            Toast.makeText(this, R.string.settings_url_invalid, Toast.LENGTH_SHORT).show()
            return
        }

        // Save
        prefsManager.setFrontendUrl(frontendUrl)
        prefsManager.setServerUrl(apiUrl)
        prefsManager.setDebugMode(binding.switchDebugMode.isChecked)

        if (isFirstLaunch) {
            prefsManager.setFirstLaunchDone()
        }

        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()

        // Return to MainActivity
        setResult(RESULT_OK)
        if (isFirstLaunch) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    private fun isValidUrl(url: String): Boolean {
        if (url.isBlank()) return false
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false
        }
        // 提取主机名
        val host = try {
            java.net.URI(url).host ?: return false
        } catch (e: Exception) {
            return false
        }
        // 白名单检查
        val allowedHosts = listOf(
            "localhost",
            "127.0.0.1",
            "10.0.2.2",
            "45c3c69c.r7.cpolar.cn"
        )
        return allowedHosts.any { allowed ->
            host == allowed || host.endsWith(".cpolar.top") || host.endsWith(".cpolar.cn")
        }
    }

    }
