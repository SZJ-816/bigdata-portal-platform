package com.zhixun.portal.web

import android.graphics.Bitmap
import android.net.http.SslError
import android.view.View
import android.webkit.*
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

    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        // For development: proceed with SSL errors
        // In production, this should be more restrictive
        handler?.proceed()
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        // Let WebView handle all navigations within the same domain
        return false
    }
}
