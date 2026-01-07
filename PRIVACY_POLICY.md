### RecentApps: Privacy Policy

Welcome to the RecentApps app for Android!

This is a utility app designed to add a custom shortcut to your device. As the developer, I take your privacy very seriously. I have not programmed this app to collect any personally identifiable information.

### Data Collected by the App

I hereby state that RecentApps **does not collect, store, or transmit any personally identifiable information**. All data created by the app, such as user preferences, is stored locally in your device's private storage. This data can be erased at any time by clearing the app's data or uninstalling it. No analytics or tracking software is present in the app.

### Explanation of Permissions Requested in the App

The list of permissions required by the app can be found in the `AndroidManifest.xml` file. Below is an explanation for why each permission is necessary for the app to function.

| Permission | Why it is required |
| :---: | --- |
| `android.permission.BIND_ACCESSIBILITY_SERVICE` | This is the core permission that enables the app's main feature. The Accessibility Service is used to listen for `AccessibilityEvent`s of type `TYPE_VIEW_LONG_CLICKED` on the system's home button or gesture navigation bar. **The service does not monitor, log, or store any text you type, your notifications, or any other content on your screen.** Its sole purpose is to provide the long-press shortcut. |
| `android.permission.VIBRATE` | This permission is used to provide brief haptic feedback (a small vibration) when a valid long-press gesture is successfully detected. This confirms to the user that the action was registered. |

<hr style="border:1px solid gray">

If you have any questions or concerns regarding how the app protects your privacy, please feel free to send me an email.

Yours sincerely,  
Crispim.