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
        private const val SETTINGS_REQUEST_CODE = 100
        private const val DOUBLE_BACK_EXIT_INTERVAL = 2000L
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: PrefsManager
    private lateinit var networkUtil: NetworkUtil
    private lateinit var webViewBridge: WebViewBridge
    private lateinit var webChromeClient: AppWebChromeClient

    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)
        networkUtil = NetworkUtil(this)

        // Toolbar hidden — web page has its own header
        // setupToolbar()
        setupWebView()
        setupSwipeRefresh()
        setupErrorOverlay()
        handleDeepLink(intent)

        // Always load frontend - show framework even if URL fails
        // User can change settings via "我的" tab in the web UI
        loadFrontend()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_refresh -> {
                    binding.webView.reload()
                    true
                }
                R.id.action_settings -> {
                    openSettings()
                    true
                }
                R.id.action_about -> {
                    showAboutDialog()
                    true
                }
                else -> false
            }
        }
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

        // Performance
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

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

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java).apply {
            putExtra(SettingsActivity.EXTRA_FIRST_LAUNCH, false)
        }
        @Suppress("DEPRECATION")
        startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        webChromeClient.handleFileChooserResult(requestCode, resultCode, data)

        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            webViewBridge.injectApiServerUrl(prefsManager.getServerUrl())
            loadFrontend()
        }
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data ?: return
        binding.webView.loadUrl(uri.toString())
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.about_title)
            .setMessage(getString(R.string.about_desc) + "\n\n版本: ${BuildConfig.VERSION_NAME}")
            .setPositiveButton("确定", null)
            .show()
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
        } else {
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show()
            handler.postDelayed({ doubleBackToExitPressedOnce = false }, DOUBLE_BACK_EXIT_INTERVAL)
        }
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
            webChromeClient = null
            webViewClient = object : android.webkit.WebViewClient() {}
            removeJavascriptInterface("AndroidBridge")
            destroy()
        }
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
