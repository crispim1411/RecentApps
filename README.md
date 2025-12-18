# RecentApps for Foldable Flip Devices

RecentApps is an Android utility designed for foldable flip devices. It allows you to open the recent apps screen directly on the external (cover) screen by using the physical volume keys as a shortcut.

## Features

*   **External Screen Access**: Launch the recent applications view on your device's cover screen, a feature not typically available by default.
*   **Volume Key Shortcut**: Use a simple double-press of the volume up key to trigger the recent apps screen when the device is closed.
*   **Customizable Delay**: Adjust the sensitivity of the double-press shortcut via a slider in the app's settings.
*   **Easy Setup**: A simple interface to guide you through granting the necessary permissions.

## How It Works

The app uses an Accessibility Service to listen for key presses from the volume buttons *only when the main screen is off* (i.e., the device is closed). When a double-press is detected, it launches the recent apps activity on the external display.

## Setup

1.  Install the application.
2.  Open the app and you will be guided to grant two main permissions:
    *   **Accessibility Service**: This is required to detect the volume key presses.
    *   **Display Over Other Apps (System Alert Window)**: This is necessary to launch the activity on the external screen.
3.  (Optional) Adjust the "Double-Press Delay" slider to your preference.
4.  Once permissions are granted, the service runs in the background. Close your device and double-press the **Volume Up** button to see it in action.

## Important Note

Do not add this app to Samsung's **GoodLock** or any other task manager that might restrict its background processes. Doing so will prevent the accessibility service from running correctly and will break the app's functionality.

## Privacy

This app was built with privacy in mind. It does not collect, store, or transmit any personal data. For more details, please see the [Privacy Policy](PRIVACY_POLICY.md).
