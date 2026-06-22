package com.zhixun.portal

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhixun.portal.databinding.ActivityMainBinding
import com.zhixun.portal.util.PrefsManager

class MainActivity : AppCompatActivity() {

    companion object {
        private const val DOUBLE_BACK_EXIT_INTERVAL = 2000L
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: PrefsManager

    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())
    private var loadFailed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            prefsManager = PrefsManager(this)

            setupBackPressHandler()
            setupWebView()
            setupBottomNav()
            setupErrorOverlay()

            // 加载远程前端（用户在"我的"里改的地址）
            loadFrontend()
        } catch (e: Throwable) {
            showCriticalError("启动失败: ${e.message}")
        }
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else if (doubleBackToExitPressedOnce) {
                        finish()
                    } else {
                        doubleBackToExitPressedOnce = true
                        Toast.makeText(this@MainActivity, "再按一次退出", Toast.LENGTH_SHORT).show()
                        handler.postDelayed({ doubleBackToExitPressedOnce = false }, DOUBLE_BACK_EXIT_INTERVAL)
                    }
                } catch (_: Throwable) {
                    finish()
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webView = binding.webView
        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.mediaPlaybackRequiresUserGesture = false
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // Local file access (for fallback)
        try {
            @Suppress("DEPRECATION")
            settings.allowFileAccessFromFileURLs = true
            @Suppress("DEPRECATION")
            settings.allowUniversalAccessFromFileURLs = true
        } catch (_: Throwable) { /* ignore */ }

        try {
            val defaultUA = settings.userAgentString
            settings.userAgentString = "$defaultUA ZhixunApp/${BuildConfig.VERSION_NAME}"
        } catch (_: Throwable) { /* ignore */ }

        try {
            WebView.setWebContentsDebuggingEnabled(prefsManager.isDebugMode())
        } catch (_: Throwable) { /* ignore */ }

        try {
            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
            }
        } catch (_: Throwable) { /* ignore */ }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                loadFailed = false
                binding.errorOverlay.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                if (request?.isForMainFrame == true) {
                    loadFailed = true
                    binding.errorOverlay.post {
                        binding.errorOverlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                // Allow http/https within the WebView
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false
                }
                // Open other schemes (tel, mailto, etc.) in external apps
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (_: Throwable) { /* ignore */ }
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                try {
                    if (newProgress < 100) {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar.progress = newProgress
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                } catch (_: Throwable) { /* ignore */ }
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (loadFailed) {
                        loadFrontend()
                    } else {
                        navigateInWebView("#/")
                    }
                    true
                }
                R.id.nav_channel -> {
                    navigateInWebView("#/channel/AI")
                    true
                }
                R.id.nav_search -> {
                    navigateInWebView("#/search")
                    true
                }
                R.id.nav_dashboard -> {
                    val dashboardUrl = prefsManager.getDashboardUrl()
                    if (!dashboardUrl.isNullOrEmpty() && dashboardUrl.startsWith("http")) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dashboardUrl))
                            startActivity(intent)
                        } catch (_: Throwable) {
                            Toast.makeText(this, "无法打开大屏地址", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "请先在设置中配置数据大屏地址", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupErrorOverlay() {
        try {
            binding.btnRetry.setOnClickListener {
                binding.errorOverlay.visibility = View.GONE
                loadFrontend()
            }
            binding.btnSettings.setOnClickListener {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        } catch (_: Throwable) { /* ignore */ }
    }

    private fun navigateInWebView(hashPath: String) {
        try {
            val script = """
                (function() {
                    try {
                        window.location.hash = '$hashPath';
                    } catch (e) { /* ignore */ }
                })();
            """.trimIndent()
            binding.webView.evaluateJavascript(script, null)
        } catch (_: Throwable) {
            loadFrontend()
        }
    }

    private fun loadFrontend() {
        try {
            val frontendUrl = prefsManager.getFrontendUrl()
            binding.webView.loadUrl(frontendUrl)
        } catch (e: Throwable) {
            showCriticalError("页面加载失败: ${e.message}")
        }
    }

    private fun showCriticalError(message: String) {
        try {
            binding.errorOverlay.visibility = View.VISIBLE
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        } catch (_: Throwable) { /* ignore */ }
    }

    override fun onResume() {
        super.onResume()
        try {
            binding.webView.onResume()
            // If the previous load failed, try again (user may have changed URL in settings)
            if (loadFailed) {
                loadFrontend()
            }
        } catch (_: Throwable) { /* ignore */ }
    }

    override fun onPause() {
        super.onPause()
        try {
            binding.webView.onPause()
            CookieManager.getInstance().flush()
        } catch (_: Throwable) { /* ignore */ }
    }

    override fun onDestroy() {
        try {
            binding.webView.apply {
                stopLoading()
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                removeAllViews()
                destroy()
            }
        } catch (_: Throwable) { /* ignore */ }
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
