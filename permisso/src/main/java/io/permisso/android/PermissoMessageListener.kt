package io.permisso.android

/**
 * Interface for listening to messages from the Permisso widget
 */
interface PermissoMessageListener {
    /**
     * Called when a message is received from the widget via postMessage
     * @param event The JSON data string received from the widget
     */
    fun onMessageReceived(event: String)
}
