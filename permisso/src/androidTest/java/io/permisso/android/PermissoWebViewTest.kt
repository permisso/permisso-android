package io.permisso.android

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class PermissoWebViewTest {

    private lateinit var context: Context
    private lateinit var permissoWebView: PermissoWebView

    @Mock
    private lateinit var mockMessageListener: PermissoMessageListener

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        // Ensure we're on the main thread for WebView creation
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            permissoWebView = PermissoWebView(context)
        }
    }

    @Test
    fun testWebViewInitialization() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            // Test that WebView is properly initialized
            assertNotNull(permissoWebView)
            assertTrue(permissoWebView.settings.javaScriptEnabled)
            assertTrue(permissoWebView.settings.domStorageEnabled)
            assertFalse(permissoWebView.settings.allowFileAccess)
            assertFalse(permissoWebView.settings.allowContentAccess)
            assertTrue(permissoWebView.settings.supportMultipleWindows())
            assertTrue(permissoWebView.settings.javaScriptCanOpenWindowsAutomatically)
        }
    }

    @Test
    fun testConfigurationInitialization() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val config = PermissoConfig(
                linkHandlingMode = LinkHandlingMode.CUSTOM_TAB
            )

            permissoWebView.initialize(config, mockMessageListener)

            // Verify that initialization completes without errors
            assertNotNull(permissoWebView)
        }
    }

    @Test
    fun testLoadWidget() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val testUrl = "https://s.bkfp.io/m/test123"

            // This should not throw an exception
            permissoWebView.loadWidget(testUrl)

            // We can't easily test the actual URL loading in unit tests,
            // but we can verify the method completes successfully
            assertNotNull(permissoWebView)
        }
    }

    @Test
    fun testMessageHandling() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            permissoWebView.initialize(PermissoConfig(), mockMessageListener)

            val testMessage = """{"type": "widget_ready", "data": {"status": "loaded"}}"""
            permissoWebView.handleMessage(testMessage)
        }

        // Verify that the message listener was called
        verify(mockMessageListener).onMessageReceived(any())
    }

    @Test
    fun testCustomLinkNavigationHandler() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val config = PermissoConfig(linkHandlingMode = LinkHandlingMode.CUSTOM)
            val customHandler = LinkNavigationHandler(context, LinkHandlingMode.CUSTOM)

            permissoWebView.initialize(config)
            permissoWebView.setLinkNavigationHandler(customHandler)

            // Verify that setting a custom handler doesn't cause errors
            assertNotNull(permissoWebView)
        }
    }

    @Test
    fun testWebViewSettings() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            // Test that all required WebView settings are properly configured
            with(permissoWebView.settings) {
                assertTrue("JavaScript should be enabled", javaScriptEnabled)
                assertTrue("DOM storage should be enabled", domStorageEnabled)
                assertFalse("File access should be disabled", allowFileAccess)
                assertFalse("Content access should be disabled", allowContentAccess)
                assertTrue("Multiple windows should be supported", supportMultipleWindows())
                assertTrue("JavaScript should be able to open windows", javaScriptCanOpenWindowsAutomatically)
            }
        }
    }

    @Test
    fun testMultipleInitializationCalls() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val config1 = PermissoConfig(linkHandlingMode = LinkHandlingMode.CUSTOM_TAB)
            val config2 = PermissoConfig(linkHandlingMode = LinkHandlingMode.EXTERNAL_BROWSER)

            // Multiple initialization calls should not cause issues
            permissoWebView.initialize(config1, mockMessageListener)
            permissoWebView.initialize(config2, mockMessageListener)

            assertNotNull(permissoWebView)
        }
    }
}
