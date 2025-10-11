# Permisso Android SDK

The Permisso Android SDK provides a simple way to integrate the Permisso widget into your Android application with proper link navigation handling.

## Requirements

- **Android API level 24+ (Android 7.0)**
- Internet permission
- Chrome browser for Custom Tabs (optional but recommended)

## Features

- Easy integration with Permisso widget via short links
- Smart link navigation using Chrome Custom Tabs or external browser
- PostMessage API communication between widget and native app
- Configurable link handling modes

## Installation

Add the following dependency to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'io.permisso:permisso-android:1.0.0'
}
```

## Configuration Options

### PermissoConfig

- `linkHandlingMode`: How external links should be handled
  - `CUSTOM_TAB`: Open in Chrome Custom Tabs (recommended)
  - `EXTERNAL_BROWSER`: Open in default browser
  - `CUSTOM`: Use custom handling logic

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

Copyright 2025 Permisso. All rights reserved.
