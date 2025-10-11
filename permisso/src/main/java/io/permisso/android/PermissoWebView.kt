package io.permisso.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest

/**
 * PermissoWebView is a specialized WebView that handles Permisso widget integration
 * with proper link navigation and communication via postMessage API.
 */
class PermissoWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    private var permissoConfig: PermissoConfig? = null
    private var linkNavigationHandler: LinkNavigationHandler? = null
    private var messageListener: PermissoMessageListener? = null

    init {
        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            setSupportMultipleWindows(true)
            javaScriptCanOpenWindowsAutomatically = true
            setSupportZoom(false)
        }

        webViewClient = PermissoWebViewClient()
        webChromeClient = PermissoWebChromeClient()

        addJavascriptInterface(PermissoJavaScriptInterface(this), "PermissoAndroid")
    }

    /**
     * Initialize the PermissoWebView with configuration
     */
    fun initialize(config: PermissoConfig, messageListener: PermissoMessageListener? = null) {
        this.permissoConfig = config
        this.messageListener = messageListener
        this.linkNavigationHandler = LinkNavigationHandler(context, config.linkHandlingMode)
    }

    /**
     * Load the Permisso widget using the short link
     */
    fun loadWidget(shortLink: String) {
        loadUrl(shortLink)
    }

    /**
     * Set a custom link navigation handler
     */
    fun setLinkNavigationHandler(handler: LinkNavigationHandler) {
        this.linkNavigationHandler = handler
    }

    internal fun handleMessage(event: String) {
        messageListener?.onMessageReceived(event)
    }

    private inner class PermissoWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()

            if (isPermissoDomain(url)) return false

            // Handle the URL using our link navigation handler
            linkNavigationHandler?.handleExternalLink(url) {}

            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            // Inject JavaScript to capture postMessage events from the widget
            val jsCode = """
                (function() {
                    console.log('PermissoAndroid: Initializing message capture');
                    
                    window.addEventListener('message', function(e) {
                        try {
                            var data = e.data;
                            if (typeof data === 'string') {
                                try { data = JSON.parse(data); } catch(_) {}
                            }
                            if (data && data.name && window.PermissoAndroid) {
                                window.PermissoAndroid.onEvent(data.name, JSON.stringify(data));
                            }
                        } catch (err) {
                            console.log("PermissoAndroid error:", err);
                        }
                    });
                    
                    console.log('PermissoAndroid: Message capture initialized');
                })();
            """.trimIndent()

            evaluateJavascript(jsCode, null)
        }

        fun isPermissoDomain(url: String): Boolean {
            val permissoDomains = listOf("permisso.io", "prms.io", "bankflip.io", "bkfp.io")
            val uri = android.net.Uri.parse(url.lowercase())
            return permissoDomains.any { domain -> uri.host?.endsWith(domain) == true }
        }
    }

    private inner class PermissoWebChromeClient : WebChromeClient() {
        // Handle new window creation for multiple windows support
        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: android.os.Message
        ): Boolean {
            Log.d("PermissoWebView", "onCreateWindow called - isDialog: $isDialog, isUserGesture: $isUserGesture")

            // Create a new WebView to capture the URL that should be opened in a new window
            val newWebView = WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val url = request.url.toString()
                        Log.d("PermissoWebView", "New window requesting URL: $url")

                        // Handle the URL using our link navigation handler
                        linkNavigationHandler?.handleExternalLink(url) {}

                        // Always return true to prevent the temporary WebView from loading the URL
                        return true
                    }
                }
            }

            // Set the new WebView as the result of the window creation
            val transport = resultMsg.obj as WebView.WebViewTransport
            transport.webView = newWebView
            resultMsg.sendToTarget()

            return true
        }
    }
}
