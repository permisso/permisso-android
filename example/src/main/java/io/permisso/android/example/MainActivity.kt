package io.permisso.android.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.result.contract.ActivityResultContracts
import android.webkit.PermissionRequest
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import io.permisso.android.LinkHandlingMode
import io.permisso.android.PermissionCallback
import io.permisso.android.PermissoConfig
import io.permisso.android.PermissoMessageListener
import io.permisso.android.PermissoWebView

class MainActivity : AppCompatActivity(), PermissoMessageListener, PermissionCallback {
    private lateinit var permissoWebView: PermissoWebView
    
    // Store current permission request for callback
    private var currentPermissionRequest: PermissionRequest? = null
    private var currentPermissionCallback: ((Boolean) -> Unit)? = null
    
    // Permission launcher for runtime permissions
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        Log.d("PermissoExample", "Permissions result: $permissions, all granted: $allGranted")
        
        // Notify the WebView about the permission result
        currentPermissionCallback?.invoke(allGranted)
        currentPermissionRequest = null
        currentPermissionCallback = null
    }
    
    private val permissoConfig = PermissoConfig(
        linkHandlingMode = LinkHandlingMode.CUSTOM_TAB,
        permissionCallback = this // Set this activity as the permission callback
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
    
    // Implementation of PermissionCallback interface
    override fun onPermissionRequired(
        request: PermissionRequest,
    ) {
        Log.d("PermissoExample", "Permission required for resources: ${request.resources.joinToString()}")
        currentPermissionRequest = request

        // Determine required Android permissions based on WebView request
        val requiredPermissions = mutableListOf<String>()
        for (resource in request.resources) {
            when (resource) {
                PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                    requiredPermissions.add(Manifest.permission.CAMERA)
                }
                PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                    requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
                }
            }
        }

        // Check if permissions are already granted
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            // All permissions already granted
            Log.d("PermissoExample", "All required permissions already granted")
            request.grant(request.resources)
        } else {
            // Show rationale dialog if needed
            val showRationale = permissionsToRequest.any {
                shouldShowRequestPermissionRationale(it)
            }

            if (showRationale) {
                AlertDialog.Builder(this)
                    .setTitle("Permissions Required")
                    .setMessage("This feature requires camera and/or microphone access. Please grant the permissions.")
                    .setPositiveButton("OK") { _, _ ->
                        // Launch permission request
                        currentPermissionCallback = { granted ->
                            if (granted) {
                                request.grant(request.resources)
                            } else {
                                request.deny()
                            }
                        }
                        permissionLauncher.launch(permissionsToRequest.toTypedArray())
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        request.deny()
                    }
                    .show()
            } else {
                // Directly request permissions
                currentPermissionCallback = { granted ->
                    if (granted) {
                        request.grant(request.resources)
                    } else {
                        request.deny()
                    }
                }
                permissionLauncher.launch(permissionsToRequest.toTypedArray())
            }
        }
    }
}