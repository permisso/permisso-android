# Changelog

All notable changes to the Permisso Android SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2025-10-12

### Fixes
- Handling postMessage events

## [1.0.0] - 2025-10-08

### Added
- Initial release of Permisso Android SDK
- PermissoWebView component with smart link navigation
- Support for Chrome Custom Tabs and external browser navigation
- PostMessage API communication between widget and native app
- Configurable link handling modes (Custom Tab, External Browser, Custom)
- Debug logging support
- Example app demonstrating SDK integration
- Complete documentation and README

### Features
- Easy integration with Permisso widget via short links (https://prms.io/XXXX)
- Automatic redirection handling to widget.permisso.io
- Intercepts external links to prevent navigation away from widget
- Opens external links in in-app browser (Custom Tabs) or external browser
- Sends postMessage callbacks when links are opened
- Handles widget ready, navigation, and error states
- Compatible with Android API 24+ (Android 7.0)

### Dependencies
- AndroidX WebKit for enhanced WebView features
- Kotlin standard library

### Documentation
- Complete README with installation and usage instructions
- Code examples for common use cases
- API documentation with KDoc comments