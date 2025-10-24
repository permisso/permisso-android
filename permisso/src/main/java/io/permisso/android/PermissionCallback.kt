package io.permisso.android

import android.webkit.PermissionRequest

/**
 * Callback interface for handling WebView permission requests.
 * This allows the Activity to handle runtime permission requests
 * and communicate back to the WebView.
 */
interface PermissionCallback {
    /**
     * Called when the WebView needs to request runtime permissions from the user.
     * The implementation should request the required Android permissions and then
     * call the result callback.
     * 
     * @param permissions Array of Android permissions needed (e.g., Manifest.permission.CAMERA)
     * @param webViewRequest The original WebView permission request
     * @param onResult Callback to invoke with the permission result
     */
    fun onPermissionRequired(
        permissions: Array<String>,
        webViewRequest: PermissionRequest,
        onResult: (granted: Boolean) -> Unit
    )
}