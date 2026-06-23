package com.zhixun.portal

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.http.SslError
import android.webkit.CookieManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zhixun.portal.databinding.ActivityMainBinding
import com.zhixun.portal.util.PrefsManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: PrefsManager
    private var doubleBackToExitPressedOnce = false
    private var lastServerUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)
        lastServerUrl = prefsManager.getServerUrl()

        setupWebView()

        loadUrl(prefsManager.getFrontendUrl())
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
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.setSupportMultipleWindows(true)

        try {
            @Suppress("DEPRECATION")
            settings.allowFileAccessFromFileURLs = true
            @Suppress("DEPRECATION")
            settings.allowUniversalAccessFromFileURLs = true
        } catch (_: Exception) { }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                // 允许所有SSL证书（cpolar等内网穿透需要）
                handler?.proceed()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                // 前端页面和大屏地址都在 WebView 内加载
                val frontendHost = prefsManager.getFrontendUrl().replace("https://", "").replace("http://", "").trimEnd('/')
                val dashboardUrl = prefsManager.getDashboardUrl().replace("https://", "").replace("http://", "").trimEnd('/')
                val dashboardHost = dashboardUrl.substringBefore('/')
                if (url.contains(frontendHost) || url.contains(dashboardHost) || url.startsWith("file://")) {
                    return false
                }
                // 其他外部链接用浏览器打开
                if (url.startsWith("http")) {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        return true
                    } catch (_: Exception) { }
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // 注入API地址和服务器地址
                val serverUrl = prefsManager.getServerUrl().trimEnd('/')
                val apiUrl = "$serverUrl/api"
                view?.evaluateJavascript("window.__API_BASE_URL__='$apiUrl'", null)
                view?.evaluateJavascript("window.DASHBOARD_URL='${prefsManager.getDashboardUrl()}'", null)
                view?.evaluateJavascript("window.__SERVER_URL__='$serverUrl'", null)
                view?.evaluateJavascript("window.HOME_URL='$serverUrl'", null)
            }
        }

        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")
        webView.webChromeClient = object : WebChromeClient() {
            // 处理 window.open（大屏等页面在 WebView 内加载）
            override fun onCreateWindow(
                view: WebView?, isDialog: Boolean, isUserGesture: Boolean,
                resultMsg: android.os.Message?
            ): Boolean {
                val newWebView = WebView(this@MainActivity)
                newWebView.settings.javaScriptEnabled = true
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(v: WebView?, req: WebResourceRequest?): Boolean {
                        v?.loadUrl(req?.url.toString())
                        return true
                    }
                }
                val transport = resultMsg?.obj as? WebView.WebViewTransport
                transport?.webView = newWebView
                resultMsg?.sendToTarget()
                // 在主 WebView 中加载大屏 URL
                val dashboardUrl = prefsManager.getDashboardUrl()
                if (dashboardUrl.startsWith("http")) {
                    view?.loadUrl(dashboardUrl)
                }
                return true
            }
        }
    }

    private fun loadUrl(url: String) {
        binding.webView.loadUrl(url)
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
        // 检测服务器地址是否变化（从SettingsActivity返回时），自动刷新WebView
        val currentServerUrl = prefsManager.getServerUrl()
        if (currentServerUrl != lastServerUrl) {
            lastServerUrl = currentServerUrl
            binding.webView.reload()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onDestroy() {
        try {
            binding.webView.apply {
                stopLoading()
                destroy()
            }
        } catch (_: Exception) { }
        super.onDestroy()
    }

    @Deprecated("Use onBackPressedDispatcher")
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else if (doubleBackToExitPressedOnce) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        } else {
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show()
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        }
    }

    /**
     * JS Bridge: 供前端网页调用，连接原生功能
     */
    inner class AndroidBridge {
        @JavascriptInterface
        fun openNativeSettings() {
            runOnUiThread {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
        }

        @JavascriptInterface
        fun saveServerUrl(url: String) {
            val trimmed = url.trim().trimEnd('/')
            prefsManager.setServerUrl(trimmed)
            prefsManager.setFrontendUrl(trimmed)
            prefsManager.setDashboardUrl("$trimmed/bigscreen/")
        }

        @JavascriptInterface
        fun getServerUrl(): String {
            return prefsManager.getServerUrl()
        }
    }
}
