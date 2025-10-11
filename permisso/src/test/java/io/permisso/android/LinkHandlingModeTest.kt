package io.permisso.android

import org.junit.Test
import org.junit.Assert.*

class LinkHandlingModeTest {

    @Test
    fun `all link handling modes are available`() {
        val modes = LinkHandlingMode.values()

        assertEquals(3, modes.size)
        assertTrue(modes.contains(LinkHandlingMode.CUSTOM_TAB))
        assertTrue(modes.contains(LinkHandlingMode.EXTERNAL_BROWSER))
        assertTrue(modes.contains(LinkHandlingMode.CUSTOM))
    }

    @Test
    fun `link handling mode string representation`() {
        assertEquals("CUSTOM_TAB", LinkHandlingMode.CUSTOM_TAB.name)
        assertEquals("EXTERNAL_BROWSER", LinkHandlingMode.EXTERNAL_BROWSER.name)
        assertEquals("CUSTOM", LinkHandlingMode.CUSTOM.name)
    }

    @Test
    fun `link handling mode valueOf`() {
        assertEquals(LinkHandlingMode.CUSTOM_TAB, LinkHandlingMode.valueOf("CUSTOM_TAB"))
        assertEquals(LinkHandlingMode.EXTERNAL_BROWSER, LinkHandlingMode.valueOf("EXTERNAL_BROWSER"))
        assertEquals(LinkHandlingMode.CUSTOM, LinkHandlingMode.valueOf("CUSTOM"))
    }
}
