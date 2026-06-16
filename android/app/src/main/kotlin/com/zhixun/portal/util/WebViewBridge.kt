package com.zhixun.portal.util

import android.os.Build
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.zhixun.portal.BuildConfig

class WebViewBridge(
    private val webView: WebView,
    private val prefsManager: PrefsManager
) {

    @JavascriptInterface
    fun saveServerUrl(url: String) {
        prefsManager.setServerUrl(url)
        webView.post {
            injectApiServerUrl(url)
        }
    }

    @JavascriptInterface
    fun getServerUrl(): String {
        return prefsManager.getServerUrl()
    }

    @JavascriptInterface
    fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    @JavascriptInterface
    fun getPlatform(): String {
        return "android"
    }

    @JavascriptInterface
    fun getOsVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun injectApiServerUrl(serverUrl: String) {
        val escapedUrl = serverUrl
            .replace("\\", "\\\\")
            .replace("'", "\\'")
        val script = """
            (function() {
                try {
                    var url = '${escapedUrl}'.replace(/\/$/, '');
                    localStorage.setItem('apiServerUrl', url);
                    window.__API_BASE_URL__ = url + '/api';
                    window.__SERVER_URL__ = url;
                } catch(e) {
                    console.error('Failed to inject apiServerUrl:', e);
                }
            })();
        """.trimIndent()
        webView.evaluateJavascript(script, null)
    }
}
