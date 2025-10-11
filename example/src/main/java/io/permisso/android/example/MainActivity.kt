package io.permisso.android.example

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.permisso.android.LinkHandlingMode
import io.permisso.android.PermissoConfig
import io.permisso.android.PermissoMessageListener
import io.permisso.android.PermissoWebView

class MainActivity : AppCompatActivity(), PermissoMessageListener {
    private lateinit var permissoWebView: PermissoWebView
    private val permissoConfig = PermissoConfig(
        linkHandlingMode = LinkHandlingMode.CUSTOM_TAB,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the root LinearLayout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            fitsSystemWindows = true
        }

        // Handle window insets to position content right after action bar
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            // Apply top padding for system bars (status bar + action bar) and bottom for keyboard
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, ime.bottom)
            WindowInsetsCompat.CONSUMED
        }

        // Create the PermissoWebView
        permissoWebView = PermissoWebView(this)
        permissoWebView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f // Fill remaining space
        )
        rootLayout.addView(permissoWebView)

        setContentView(rootLayout)
        setupPermissoWebView()
    }

    private fun setupPermissoWebView() {
        // Initialize the PermissoWebView
        permissoWebView.initialize(permissoConfig, this)

        // Load a sample Permisso widget (replace with actual short link)
        val shortLink = "https://s.prms.io/m/5dapwREh"
        permissoWebView.loadWidget(shortLink)
    }

    override fun onMessageReceived(event: String) {
        Log.d("PermissoExample", "Received message: $event")
    }
}