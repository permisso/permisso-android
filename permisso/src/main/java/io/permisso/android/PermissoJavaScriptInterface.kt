package io.permisso.android

import android.webkit.JavascriptInterface
import android.util.Log

/**
 * JavaScript interface for handling postMessage communication between the widget and native Android code
 */
class PermissoJavaScriptInterface(private val webView: PermissoWebView) {

    @JavascriptInterface
    fun onEvent(event: String) {
        Log.d("PermissoWidget", "Received event: $event")
        webView.handleMessage(event)
    }

    @JavascriptInterface
    fun log(message: String) {
        Log.d("PermissoWidget", message)
    }
}
