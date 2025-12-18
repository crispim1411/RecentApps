### RecentApps: Privacy Policy

Welcome to the RecentApps app for Android!

This is a utility app designed to provide custom shortcuts on the cover screens of foldable devices. As the developer, I take your privacy very seriously. I have not programmed this app to collect any personally identifiable information.

### Data collected by the app

I hereby state that RecentApps **does not collect, store, or transmit any personally identifiable information**. All data created by you (the user), such as app preferences (like the double-press delay), is stored locally in your device's private storage. This data can be erased at any time by clearing the app's data or uninstalling it. No analytics or tracking software is present in the app.

### Explanation of permissions requested in the app

The list of permissions required by the app can be found in the `AndroidManifest.xml` file. Below is an explanation for why each permission is necessary for the app to function.

| Permission | Why it is required |
| :---: | --- |
| `android.permission.SYSTEM_ALERT_WINDOW` | This permission is required to allow the app to launch other application windows on the external (cover) screen, which is considered a non-standard display that requires special management. |
| `android.permission.BIND_ACCESSIBILITY_SERVICE` | This permission is required for the volume button shortcuts feature. The Accessibility Service listens for `KeyEvent`s from the physical volume buttons to detect single and double presses. **The service does not monitor, log, or store any text you type or any other content on your screen.** Its sole purpose is to provide shortcuts from the cover screen. The app declares itself to the Android system as not being a primary accessibility tool (`isAccessibilityTool=\"false\"`). |
| `android.permission.FOREGROUND_SERVICE` & `android.permission.FOREGROUND_SERVICE_SPECIAL_USE` | These permissions allow RecentApps' key-listening service to run reliably in the background. This is necessary for the app to function when the cover screen is active and the main configuration app is not visible. |

### Explanation of App Queries

The app also declares its intent to query for other installed packages, specifically `com.google.android.inputmethod.latin` (Gboard). This is necessary to get the correct Intent to launch Gboard on the external display as part of the shortcut functionality. No data is collected or stored about other installed apps on your device.

<hr style="border:1px solid gray">

If you have any questions or concerns regarding how the app protects your privacy, please feel free to send me an email.

Yours sincerely,  
Crispim.