# Permisso Android SDK

The Permisso Android SDK provides a simple way to integrate the Permisso widget into your Android application with proper link navigation handling.

## Requirements

- **Android API level 24+ (Android 7.0)**
- Internet permission
- Camera permission
- Record Audio permission
- Chrome browser for Custom Tabs (optional but recommended)

## Features

- Easy integration with Permisso widget via short links
- Smart link navigation using Chrome Custom Tabs or external browser
- PostMessage API communication between widget and native app
- Camera and microphone permission handling with callbacks
- Configurable link handling modes

## Installation

Add the following dependency to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'io.permisso:permisso-android:1.0.5'
}
```

## Configuration Options

### PermissoConfig

- `linkHandlingMode`: How external links should be handled
  - `CUSTOM_TAB`: Open in Chrome Custom Tabs (recommended)
  - `EXTERNAL_BROWSER`: Open in default browser
  - `CUSTOM`: Use custom handling logic
- `permissionCallback`: Callback for handling runtime permission requests from WebView (camera, microphone, etc.)

### Link Handling Modes

#### Custom Tabs (Recommended)

```kotlin
val config = PermissoConfig(
    linkHandlingMode = LinkHandlingMode.CUSTOM_TAB
)
```

#### External Browser

```kotlin
val config = PermissoConfig(
    linkHandlingMode = LinkHandlingMode.EXTERNAL_BROWSER
)
```

#### Custom Handling

```kotlin
val config = PermissoConfig(
    linkHandlingMode = LinkHandlingMode.CUSTOM
)

// Set custom link handler
permissoWebView.setLinkNavigationHandler(CustomNavigationHandler())
```

## Handling Camera and Microphone Permissions

The Permisso widget may require access to the camera and/or microphone for certain features. The SDK provides a callback mechanism to handle these permission requests.

### Required Manifest Permissions

Add the necessary permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
<uses-feature android:name="android.hardware.microphone" android:required="false" />
```

### Implementing PermissionCallback

To handle permission requests, implement the `PermissionCallback` interface in your Activity:

### Permission Flow

1. **WebView Request**: When the Permisso widget needs camera/microphone access, the WebView triggers a permission request
2. **Callback Invocation**: The SDK calls `onPermissionRequired()` on your `PermissionCallback` implementation
3. **Runtime Permission**: Your app requests the necessary Android runtime permissions from the user
4. **Grant/Deny**: Based on the user's response, call `request.grant()` or `request.deny()`

### Best Practices

- **Show Rationale**: Use `shouldShowRequestPermissionRationale()` to display a dialog explaining why permissions are needed before requesting them
- **Handle Denials**: Gracefully handle cases where users deny permissions
- **Check Existing Permissions**: Always check if permissions are already granted before requesting them again

## Advanced Usage

### Custom Message Handling

To listen messages raised by the Permisso widget, you should pass an instance
of `PermissoMessageListener` to the `PermissoWebView` on the initialize method.

```kotlin
permissoWebView.initialize(permissoConfig, this)

override fun onMessageReceived(message: String) {
    // the message is a JSON string containing the event name and data
}
```

### Custom Link Navigation

```kotlin
class CustomLinkHandler(context: Context) : LinkNavigationHandler(context, LinkHandlingMode.CUSTOM) {
    override fun handleExternalLink(url: String, callback: (Boolean) -> Unit) {
        // Your custom link handling logic
        openCustomBrowser(url)
        callback(true)
    }
}
```

## Sample App

Check out the `example` module in this repository for a complete integration example.

## License

Copyright 2026 Permisso. All rights reserved.
