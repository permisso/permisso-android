package io.permisso.android

import org.junit.Test
import org.junit.Assert.*

class PermissoConfigTest {

    @Test
    fun `config creation with default values`() {
        val config = PermissoConfig()

        assertEquals(LinkHandlingMode.CUSTOM_TAB, config.linkHandlingMode)
    }

    @Test
    fun `config creation with custom linkHandlingMode`() {
        val config = PermissoConfig(
            linkHandlingMode = LinkHandlingMode.EXTERNAL_BROWSER
        )

        assertEquals(LinkHandlingMode.EXTERNAL_BROWSER, config.linkHandlingMode)
    }

    @Test
    fun `config with all link handling modes`() {
        val customTabConfig = PermissoConfig(linkHandlingMode = LinkHandlingMode.CUSTOM_TAB)
        val externalBrowserConfig = PermissoConfig(linkHandlingMode = LinkHandlingMode.EXTERNAL_BROWSER)
        val customConfig = PermissoConfig(linkHandlingMode = LinkHandlingMode.CUSTOM)

        assertEquals(LinkHandlingMode.CUSTOM_TAB, customTabConfig.linkHandlingMode)
        assertEquals(LinkHandlingMode.EXTERNAL_BROWSER, externalBrowserConfig.linkHandlingMode)
        assertEquals(LinkHandlingMode.CUSTOM, customConfig.linkHandlingMode)
    }

    @Test
    fun `defaultConfig factory method creates config with default values`() {
        val config = PermissoConfig.defaultConfig()

        assertEquals(LinkHandlingMode.CUSTOM_TAB, config.linkHandlingMode)
    }

    @Test
    fun `config is a data class with proper equality`() {
        val config1 = PermissoConfig(LinkHandlingMode.CUSTOM_TAB)
        val config2 = PermissoConfig(LinkHandlingMode.CUSTOM_TAB)
        val config3 = PermissoConfig(LinkHandlingMode.EXTERNAL_BROWSER)

        assertEquals(config1, config2)
        assertNotEquals(config1, config3)
    }

    @Test
    fun `config has proper toString implementation`() {
        val config = PermissoConfig(LinkHandlingMode.CUSTOM_TAB)
        val toString = config.toString()

        assertTrue("toString should contain class name", toString.contains("PermissoConfig"))
        assertTrue("toString should contain linkHandlingMode", toString.contains("CUSTOM_TAB"))
    }
}
