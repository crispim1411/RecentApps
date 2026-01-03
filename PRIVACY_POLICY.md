### RecentApps: Privacy Policy

Welcome to the RecentApps app for Android!

This is a utility app designed to add custom shortcuts to your device. As the developer, I take your privacy very seriously. I have not programmed this app to collect any personally identifiable information.

### Data collected by the app

I hereby state that RecentApps **does not collect, store, or transmit any personally identifiable information**. All data created by you (the user), such as app preferences (like the double-press delay), is stored locally in your device's private storage. This data can be erased at any time by clearing the app's data or uninstalling it. No analytics or tracking software is present in the app.

### Explanation of permissions requested in the app

The list of permissions required by the app can be found in the `AndroidManifest.xml` file. Below is an explanation for why each permission is necessary for the app to function.

| Permission | Why it is required |
| :---: | --- |
| `android.permission.BIND_ACCESSIBILITY_SERVICE` | This is the core permission that enables all of the app's features. The Accessibility Service is used in two ways: 1) It listens for `KeyEvent`s from the physical volume buttons to detect double-presses. 2) It listens for `AccessibilityEvent`s of type `TYPE_VIEW_LONG_CLICKED` to detect when the Home button is long-pressed. **The service does not monitor, log, or store any text you type, your notifications, or any other content on your screen.** Its sole purpose is to provide shortcuts. |

<hr style="border:1px solid gray">

If you have any questions or concerns regarding how the app protects your privacy, please feel free to send me an email.

Yours sincerely,  
Crispim.