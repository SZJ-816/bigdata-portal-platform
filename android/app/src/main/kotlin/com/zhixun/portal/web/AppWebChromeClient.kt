package com.zhixun.portal.web

import android.net.Uri
import android.view.View
import android.webkit.*
import android.app.Activity
import android.content.Intent
import android.widget.ProgressBar

class AppWebChromeClient(
    private val activity: Activity,
    private val progressBar: ProgressBar?
) : WebChromeClient() {

    companion object {
        private const val FILE_CHOOSER_REQUEST_CODE = 1001
    }

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        progressBar?.let {
            if (newProgress < 100) {
                it.visibility = View.VISIBLE
                it.progress = newProgress
            } else {
                it.visibility = View.GONE
            }
        }
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        // Update toolbar title with page title if it's not blank
        if (!title.isNullOrBlank() && !title.startsWith("http")) {
            activity.title = title
        }
    }

    // Handle JavaScript alert()
    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        android.app.AlertDialog.Builder(activity)
            .setTitle("提示")
            .setMessage(message)
            .setPositiveButton("确定") { _, _ -> result?.confirm() }
            .setCancelable(false)
            .show()
        return true
    }

    // Handle JavaScript confirm()
    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        android.app.AlertDialog.Builder(activity)
            .setTitle("确认")
            .setMessage(message)
            .setPositiveButton("确定") { _, _ -> result?.confirm() }
            .setNegativeButton("取消") { _, _ -> result?.cancel() }
            .setCancelable(false)
            .show()
        return true
    }

    // Handle JavaScript prompt()
    override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        val input = android.widget.EditText(activity).apply {
            setText(defaultValue)
        }
        android.app.AlertDialog.Builder(activity)
            .setTitle("输入")
            .setMessage(message)
            .setView(input)
            .setPositiveButton("确定") { _, _ -> result?.confirm(input.text.toString()) }
            .setNegativeButton("取消") { _, _ -> result?.cancel() }
            .setCancelable(false)
            .show()
        return true
    }

    // Handle file upload (input type="file")
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        this.filePathCallback?.onReceiveValue(null)
        this.filePathCallback = filePathCallback

        val intent = fileChooserParams?.createIntent() ?: return false
        return try {
            activity.startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE)
            true
        } catch (e: Exception) {
            this.filePathCallback = null
            false
        }
    }

    fun handleFileChooserResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            filePathCallback?.let { callback ->
                val results = if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { arrayOf(it) }
                } else {
                    null
                }
                callback.onReceiveValue(results)
            }
            filePathCallback = null
        }
    }
}
