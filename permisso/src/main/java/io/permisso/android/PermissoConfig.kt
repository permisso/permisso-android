package io.permisso.android

/**
 * Configuration class for PermissoWebView
 */
data class PermissoConfig(
    /**
     * How external links should be handled
     */
    val linkHandlingMode: LinkHandlingMode = LinkHandlingMode.CUSTOM_TAB,
) {
    companion object {
        /**
         * Create a default configuration
         */
        fun defaultConfig() = PermissoConfig()
    }
}

/**
 * Defines how external links should be handled
 */
enum class LinkHandlingMode {
    /**
     * Open links in Chrome Custom Tabs (recommended)
     */
    CUSTOM_TAB,

    /**
     * Open links in the default browser
     */
    EXTERNAL_BROWSER,

    /**
     * Use custom handling logic
     */
    CUSTOM
}
