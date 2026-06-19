package com.zhixun.portal.util

import android.os.Build
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.zhixun.portal.BuildConfig

class WebViewBridge(
    private val webView: WebView,
    private val prefsManager: PrefsManager
) {
    companion object {
        // 白名单：只允许设置为这些域名/IP
        private val ALLOWED_HOSTS = listOf(
            "localhost",
            "127.0.0.1",
            "10.0.2.2",  // Android 模拟器访问宿主机
            "45c3c69c.r7.cpolar.cn",  // 默认 cpolar 域名
            ".cpolar.top",  // 允许所有 cpolar top 子域名
            ".cpolar.cn"    // 允许所有 cpolar cn 子域名
        )
    }

    /**
     * 保存服务器 URL（仅供 Native 代码调用，不暴露给 JavaScript）
     * 用户应通过 SettingsActivity 修改服务器地址
     */
    fun saveServerUrl(url: String): Boolean {
        if (!isUrlAllowed(url)) {
            return false
        }
        prefsManager.setServerUrl(url)
        webView.post {
            injectApiServerUrl(url)
        }
        return true
    }

    private fun isUrlAllowed(url: String): Boolean {
        if (url.isBlank()) return false
        // 检查协议
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false
        }
        // 提取主机名
        val host = try {
            java.net.URI(url).host ?: return false
        } catch (e: Exception) {
            return false
        }
        // 检查是否在白名单中
        return ALLOWED_HOSTS.any { allowed ->
            host == allowed || host.endsWith(allowed)
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
            .replace("<", "\\x3c")
            .replace(">", "\\x3e")
            .replace("/", "\\/")
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
