package com.zhixun.portal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zhixun.portal.databinding.ActivityMainBinding
import com.zhixun.portal.util.NetworkUtil
import com.zhixun.portal.util.PrefsManager
import com.zhixun.portal.util.WebViewBridge
import com.zhixun.portal.web.AppWebChromeClient
import com.zhixun.portal.web.AppWebViewClient

class MainActivity : AppCompatActivity() {

    companion object {
        private const val DOUBLE_BACK_EXIT_INTERVAL = 2000L
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: PrefsManager
    private lateinit var networkUtil: NetworkUtil
    private lateinit var webViewBridge: WebViewBridge
    private lateinit var webChromeClient: AppWebChromeClient

    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())

    // Activity Result API 替代已弃用的 startActivityForResult
    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            webViewBridge.injectApiServerUrl(prefsManager.getServerUrl())
            loadFrontend()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)
        networkUtil = NetworkUtil(this)

        setupWebView()
        setupSwipeRefresh()
        setupErrorOverlay()
        setupBackPressHandler()
        handleDeepLink(intent)

        // 首次启动自动引导用户配置服务器地址
        if (prefsManager.isFirstLaunch()) {
            prefsManager.setFirstLaunchDone()
            openSettings(true)
        } else {
            loadFrontend()
        }
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this@MainActivity, R.string.press_back_again, Toast.LENGTH_SHORT).show()
                    handler.postDelayed({ doubleBackToExitPressedOnce = false }, DOUBLE_BACK_EXIT_INTERVAL)
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
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        settings.mediaPlaybackRequiresUserGesture = true
        settings.allowFileAccess = false
        settings.allowContentAccess = false

        // Image loading optimization
        settings.loadsImagesAutomatically = true
        settings.blockNetworkImage = false

        // Cache - use cache first for faster image loading
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        val defaultUA = settings.userAgentString
        settings.userAgentString = "$defaultUA ZhixunApp/${BuildConfig.VERSION_NAME}"

        WebView.setWebContentsDebuggingEnabled(prefsManager.isDebugMode())

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
            flush()
        }

        webViewBridge = WebViewBridge(webView, prefsManager)
        webView.addJavascriptInterface(webViewBridge, "AndroidBridge")

        val webViewClient = AppWebViewClient(
            bridge = webViewBridge,
            onPageLoadStarted = {
                binding.errorOverlay.visibility = View.GONE
            },
            onPageLoadFinished = {
                binding.swipeRefresh.isRefreshing = false
                binding.errorOverlay.visibility = View.GONE
            },
            onPageLoadError = { errorMsg ->
                showErrorOverlay(errorMsg)
                binding.swipeRefresh.isRefreshing = false
            }
        )
        webView.webViewClient = webViewClient

        webChromeClient = AppWebChromeClient(this, binding.progressBar)
        webView.webChromeClient = webChromeClient
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.navy_900, R.color.accent)
        binding.swipeRefresh.setOnRefreshListener {
            binding.webView.reload()
        }
    }

    private fun setupErrorOverlay() {
        binding.btnRetry.setOnClickListener {
            binding.errorOverlay.visibility = View.GONE
            binding.swipeRefresh.isRefreshing = true
            binding.webView.reload()
        }
        binding.btnSettings.setOnClickListener {
            openSettings()
        }
    }

    private fun showErrorOverlay(errorMessage: String) {
        binding.errorMessage.text = errorMessage
        binding.errorOverlay.visibility = View.VISIBLE
    }

    private fun loadFrontend() {
        if (!networkUtil.isNetworkAvailable()) {
            showErrorOverlay(getString(R.string.error_network))
            return
        }
        val frontendUrl = prefsManager.getFrontendUrl()
        binding.webView.loadUrl(frontendUrl)
    }

    private fun openSettings(firstLaunch: Boolean = false) {
        val intent = Intent(this, SettingsActivity::class.java).apply {
            putExtra(SettingsActivity.EXTRA_FIRST_LAUNCH, firstLaunch)
        }
        settingsLauncher.launch(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data ?: return
        binding.webView.loadUrl(uri.toString())
    }

    // 保留 onActivityResult 仅用于 WebView 文件选择器（AppWebChromeClient 内部仍使用 startActivityForResult）
    @Deprecated("Used for WebView file chooser internally")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        webChromeClient.handleFileChooserResult(requestCode, resultCode, data)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onDestroy() {
        binding.webView.apply {
            stopLoading()
            setDownloadListener(null)
            removeJavascriptInterface("AndroidBridge")
            removeAllViews()
            destroy()
        }
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}