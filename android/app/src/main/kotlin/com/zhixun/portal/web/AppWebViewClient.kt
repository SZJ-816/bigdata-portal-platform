package com.zhixun.portal.web

import android.graphics.Bitmap
import android.net.http.SslError
import android.view.View
import android.webkit.*
import com.zhixun.portal.BuildConfig
import com.zhixun.portal.util.WebViewBridge

class AppWebViewClient(
    private val bridge: WebViewBridge,
    private val onPageLoadStarted: () -> Unit,
    private val onPageLoadFinished: () -> Unit,
    private val onPageLoadError: (String) -> Unit
) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onPageLoadStarted()
        // Inject apiServerUrl as early as possible
        view?.let { bridge.injectApiServerUrl(bridge.getServerUrl()) }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onPageLoadFinished()
        // Second injection to ensure it's set after page fully loads
        view?.let { bridge.injectApiServerUrl(bridge.getServerUrl()) }
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        // Only handle errors for the main frame
        if (request?.isForMainFrame == true) {
            val description = error?.description?.toString() ?: "未知错误"
            onPageLoadError(description)
        }
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        if (request?.isForMainFrame == true) {
            val code = errorResponse?.statusCode ?: 0
            onPageLoadError("HTTP $code 错误")
        }
    }

    /**
     * SSL 证书错误处理：
     * - Debug 模式：允许自签名证书（开发环境）
     * - Release 模式：拒绝所有 SSL 错误（生产安全）
     */
    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        if (BuildConfig.DEBUG) {
            // 开发环境允许自签名证书
            handler?.proceed()
        } else {
            // 生产环境拒绝 SSL 错误
            super.onReceivedSslError(view, handler, error)
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        // Let WebView handle all navigations within the same domain
        return false
    }
}
