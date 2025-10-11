package io.permisso.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService

/**
 * Handles navigation to external links with different strategies
 */
class LinkNavigationHandler(
    private val context: Context,
    private val mode: LinkHandlingMode
) {
    /**
     * Handle an external link navigation
     */
    fun handleExternalLink(url: String, callback: (Boolean) -> Unit) {
        try {
            when (mode) {
                LinkHandlingMode.CUSTOM_TAB -> openWithCustomTab(url, callback)
                LinkHandlingMode.EXTERNAL_BROWSER -> openWithExternalBrowser(url, callback)
                LinkHandlingMode.CUSTOM -> {
                    // For custom mode, just return success and let the implementer handle it
                    callback(true)
                }
            }
        } catch (e: Exception) {
            callback(false)
        }
    }

    private fun openWithCustomTab(url: String, callback: (Boolean) -> Unit) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setUrlBarHidingEnabled(false)
                .build()

            customTabsIntent.launchUrl(context, Uri.parse(url))
            callback(true)
        } catch (e: Exception) {
            // Fallback to external browser if Custom Tabs not available
            openWithExternalBrowser(url, callback)
        }
    }

    private fun openWithExternalBrowser(url: String, callback: (Boolean) -> Unit) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            callback(true)
        } catch (e: Exception) {
            callback(false)
        }
    }

    /**
     * Check if Custom Tabs is available on the device
     */
    fun isCustomTabsSupported(): Boolean {
        val intent = Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION)
        val resolveInfos = context.packageManager.queryIntentServices(intent, 0)
        return resolveInfos.isNotEmpty()
    }
}
