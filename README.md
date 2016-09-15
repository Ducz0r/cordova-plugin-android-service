# cordova-plugin-android-service

## About

### What does this plugin do?

The purpose of this Cordova plugin is to allow *asynchronous*, two-way communication between a Cordova/Phonegap application, and a native Android application. The communication is provided via asynchronous signals, that carry a signal type, and data in the form of a JSON object.

### Who is this plugin for?

This plugin is probably the right choice for you if you need to integrate/connect a Cordova, and an Android app, to work together while running at the same time. The message protocol, however, is a pretty simple implementation, so this plugin might *not* be for you if you need to transfer huge chunks of data in real-time between the Android & Cordova app.

### How does it work?

The biggest struggle I had when developing this plugin was that on Android (as you probably know), only a single `Activity` can run at all times. The only way to build a communication channel between 2 GUI apps, therefore, was to implement a service (services, on the other hand, can run in the background), that serves as a simple message queue between 2 applications (Android & Cordova). Communication is done by using native Android `Message` objects, where the provided data (a `JSONObject`) is deserialized into a String variable, and sent along with the `Message`. That's why this mechanism is not suited for larger amounts of data.

## Cordova plugin

Cordova plugin code is located in [cordova-plugin-android-service](cordova-plugin-android-service) folder. The plugin is ready to be included in a Cordova application, simply navigate to the folder where your Cordova app resides, and call the following command from the shell:

```
cordova plugin add <path_to_plugin_root_folder>
```

### Eclipse IDE

This plugin can be developed (but **only** developed, not debugged etc.) in Eclipse IDE by doing the following steps:

1. First, import this code into Eclipse as a generic `[Java project]` (create new Java project inside the existing plugin folder).
2. Once this is done, you will want to use the autocomplete features. For that, you need to include two external libraries to your project.
3. Right click on project and choose `[Properties]`. On the left menu, click on `[Java Build Path]`, and click on the `[Libraries]` tab.
4. First, you need to add `android.jar`. This file is usually located inside your Android folder (you need Android SDK) - on Windows systems, this will typically be along the lines of  `C:\Program Files\Android\android-sdk\platforms\android-<i>\android.jar`. You can add it by clicking on `[Add External JARs...]`, and navigating to the mentioned JAR.
5. Next, you will also need to add Cordova plugin Java classes. Those are trickier to find. I managed to find them by navigating to an existing Cordova application folder (can be a blank, new Cordova application, that's been deployed to Android at least once), and going into the path `<app_folder>\platforms\android\CordovaLib\build\intermediates\classes\debug` (this might differ on your machine). This folder should now contain the packaged folder structure, and classes in the end. You can copy these contents into a folder of your choice (for later use), or just reference this path from Eclipse. You can do that by clicking on `[Add External Class Folder...]`, and navigating to this folder.
6. That's it, code-completion and other fancy feature of Eclipse IDE should now work!

## Cordova application

*TODO*

## Android application

The project has a very basic sample Android application attached, see [SampleAndroidApp](SampleAndroidApp) folder. The [AndroidAppIncomingHandler.java](SampleAndroidApp/src/org/lmurn/cordova/AndroidAppIncomingHandler.java), [AndroidAppServiceConnection.java](SampleAndroidApp/src/org/lmurn/cordova/AndroidAppServiceConnection.java) and [Messages.java](SampleAndroidApp/src/org/lmurn/cordova/Messages.java) are generic, and can be included in any Android application.

For actual usage of communication with the Cordova application, see (very brief & neat) code in [MainActivity.java](SampleAndroidApp/src/org/lmurn/cordova/MainActivity.java). In short, what you need to do is following:

* Include the `AndroidAppServiceConnection`, `AndroidAppIncomingHandler` and `Messages` classes in your Android project;
* Inside an `Activity`, initialize an instance of `AndroidAppIncomingHandler` (to listen for incoming messages), and an instance of `AndroidAppIncomingHandler` to send messages to the Cordova app. Also make sure to destroy the service connection in `onDestroy()` callback.
* There's no step 3 :smile:.

## License

This code is licensed under [MIT license](LICENSE.txt).