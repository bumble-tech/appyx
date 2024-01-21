---
title: Appyx Navigation – Multiplatform
---

# Multiplatform

## Supported platforms

![badge-android](https://img.shields.io/badge/platform-android-brightgreen)
![badge-jvm](https://img.shields.io/badge/platform-jvm-orange)
![badge-macos](https://img.shields.io/badge/platform-macos-purple)
![badge-js](https://img.shields.io/badge/platform-js-yellow)
![badge-ios](https://img.shields.io/badge/platform-ios-lightgray)

## Lifecycle

Multiplatform interface:

```kotlin
package com.bumble.appyx.navigation.lifecycle

interface Lifecycle {
    
    val currentState: State

    val coroutineScope: CoroutineScope

    fun addObserver(observer: PlatformLifecycleObserver)

    fun removeObserver(observer: PlatformLifecycleObserver)

    fun asFlow(): Flow<State>

    enum class State {
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED,
        DESTROYED,
    }

    enum class Event {
        ON_CREATE,
        ON_START,
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY,
        ON_ANY,
    }
}
```

- On Android, it is implemented by `AndroidLifecycle`, which is backed by  `androidx.lifecycle`.
- On other platforms, it is implemented by the corresponding `PlatformLifecycleRegistry` 


## Parcelable, Parcelize, RawValue

Multiplatform typealiases that are backed by their proper platform-specific ones.

E.g. on Android:

```kotlin
package com.bumble.appyx.utils.multiplatform

actual typealias Parcelize = kotlinx.parcelize.Parcelize

actual typealias Parcelable = android.os.Parcelable

actual typealias RawValue = kotlinx.parcelize.RawValue
```

## Node hosts

The root node of an Appyx navigation tree needs to be connected to the platform. This ensures that system events (lifecycle, back press, etc.) reach your components in the tree.

You only need to do this for the root of the tree.


### Android

```kotlin
// Please note we are extending NodeActivity
class MainActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourAppTheme {
                NodeHost(
                    lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                    integrationPoint = appyxV2IntegrationPoint
                ) {
                    RootNode(nodeContext = it)
                }
            }
        }
    }
}
```

### Desktop

```kotlin
fun main() = application {
    val events: Channel<Events> = Channel()
    val windowState = rememberWindowState(size = DpSize(480.dp, 640.dp))
    val eventScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        onKeyEvent = {
            // See back handling section in the docs below!    
            onKeyEvent(it, events, eventScope) 
        },
    ) {
        YourAppTheme {
            Surface(color = MaterialTheme.colorScheme.background) {
                DesktopNodeHost(
                    windowState = windowState,
                    onBackPressedEvents = events.receiveAsFlow().mapNotNull {
                        if (it is Events.OnBackPressed) Unit else null
                    }
                ) { 
                    RootNode(nodeContext = it)
                }
            }
        }
    }
}
```

### Web

```kotlin
fun main() {
    val events: Channel<Unit> = Channel()
    onWasmReady {
        BrowserViewportWindow("Your app") {
            val requester = remember { FocusRequester() }
            var hasFocus by remember { mutableStateOf(false) }
            var screenSize by remember { mutableStateOf(ScreenSize(0.dp, 0.dp)) }
            val eventScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

            YourAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { screenSize = ScreenSize(it.width.dp, it.height.dp) }
                        .onKeyEvent {
                            // See back handling section in the docs below!    
                            onKeyEvent(it, events, eventScope) 
                        }
                        .focusRequester(requester)
                        .focusable()
                        .onFocusChanged { hasFocus = it.hasFocus }
                ) {
                    WebNodeHost(
                        screenSize = screenSize,
                        onBackPressedEvents = events.receiveAsFlow(),
                    ) { 
                        RootNode(nodeContext = it)
                    }
                }

                if (!hasFocus) {
                    LaunchedEffect(Unit) {
                        requester.requestFocus()
                    }
                }
            }
        }
    }
}

```

### iOS

For a complete example on how to pass a `LifecycleHelper` class to `MainViewController` please refer to our [multiplatform starter kit](https://github.com/bumble-tech/appyx-starter-kit/tree/a7331be581a6727597eab35744fe1bcd26f3fa87/iosApp/iosApp)

```kotlin
val backEvents: Channel<Unit> = Channel()

fun MainViewController(lifecycleHelper: LifecycleHelper) = ComposeUIViewController {
    YourAppTheme {
        IosNodeHost(
            modifier = Modifier,
            lifecycle = lifecycleHelper.lifecycle,
            // See back handling section in the docs below!
            onBackPressedEvents = backEvents.receiveAsFlow(),
        ) {
            RootNode(
               nodeContext = it
            )
        }
    }
}

@Composable
private fun BackButton(coroutineScope: CoroutineScope) {
    IconButton(
        onClick = {
            coroutineScope.launch {
                backEvents.send(Unit)
            }
        },
        modifier = Modifier.zIndex(99f)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            tint = Color.White,
            contentDescription = "Go Back"
        )
    }
}

```

## Back handling

### Android

On Android back events are handled automatically.

### Desktop & Web

In the above [desktop](#desktop) and [web](#web) examples there is a reference to an `onKeyEvent` method.

You can configure any `KeyEvent` to trigger a back event via the events `Channel`. In this example the `OnBackPressed` event is launched when the backspace key is pressed down:

```kotlin
private fun onKeyEvent(
    keyEvent: KeyEvent,
    events: Channel<Events>,
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
): Boolean =
    when {
        // You can also register e.g. Key.Escape instead of BackSpace: 
        keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace -> {
            coroutineScope.launch { events.send(Events.OnBackPressed) }
            true
        }

        else -> false
    }
``` 
### iOS

On [iOS](#ios), you can design a user interface element to enable back navigation, similar to how it's done on other platforms. 
In the example mentioned earlier, we create a Composable component `BackButton` that includes an `ArrowBack` icon. 
When this button is clicked, it triggers the back event through the `backEvents` `Channel`. 

## Setting up the environment for execution

Setting up the environment for execution

{==

**Warning**

In order to launch the iOS target, you need a Mac with macOS to write and run iOS-specific code on simulated or real devices.

This is an Apple requirement.

The instructions here are tweaked and inspired from the [Compose-Multiplatform-iOS template](https://github.com/JetBrains/compose-multiplatform-ios-android-template).

==}

To work with this project, you need the following:

* A machine running a recent version of macOS
* [Xcode](https://apps.apple.com/us/app/xcode/id497799835)
* [Android Studio](https://developer.android.com/studio)
* [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
* [CocoaPods dependency manager](https://kotlinlang.org/docs/native-cocoapods.html)

### Check your environment

Before you start, use the [KDoctor](https://github.com/Kotlin/kdoctor) tool to ensure that your development environment is configured correctly:

1. Install KDoctor with [Homebrew](https://brew.sh/):

    ```text
    brew install kdoctor
    ```

2. Run KDoctor in your terminal:

    ```text
    kdoctor
    ```

   If everything is set up correctly, you'll see valid output:

   ```text
   Environment diagnose (to see all details, use -v option):
   [✓] Operation System
   [✓] Java
   [✓] Android Studio
   [✓] Xcode
   [✓] Cocoapods

   Conclusion:
     ✓ Your system is ready for Kotlin Multiplatform Mobile development!
   ```

Otherwise, KDoctor will highlight which parts of your setup still need to be configured and will suggest a way to fix them.

## The project structure

Open the project in Android Studio and switch the view from **Android** to **Project** to see all the files and targets belonging to the project.
The :demos module contains the sample target [appyx-navigation](https://bumble-tech.github.io/appyx/navigation/).

This module follows the standard compose multiplatform project structure:

### common

This is a Kotlin module that contains the logic common for Android, Desktop, iOS and web applications, that is, the code you share between platforms.

### android

This is a Kotlin module that builds into an Android application. It uses Gradle as the build system.
The `android` module depends on and uses the `common` module as a regular Android library.

### desktop

This module builds into a Desktop application.

### ios

This is an Xcode project that builds into an iOS application.
The `:demos:appyx-navigation` module depends on and uses the `:demos:appyx-navigation:common` module as a CocoaPods dependency.

### web

This module builds into a Web app.

## Run your application

### On Android

To run your application on an Android emulator:

1. Ensure you have an Android virtual device available. Otherwise, [create one](https://developer.android.com/studio/run/managing-avds#createavd).
2. In the list of run configurations, select `demos.appyx-navigation.android`.
3. Choose your virtual/physical device and click **Run**.

### On iOS

#### Running on a simulator

To run your application on an iOS simulator in Android Studio, modify the `iOS` run configuration:

{==

1. In the list of run configurations, select **Edit Configurations**:
2. Navigate to **iOS Application** | **iosApp**.
3. Select the desired `.xcworkspace` file under `XCode project file` which can be found in `/demos/appyx-navigation/iosApp/iosApp.xcworkspace`.
4. Ensure `Xcode project scheme` is set to `iosApp`.
5. In the **Execution target** list, select your target device. Click **OK**.
6. The `iosApp` run configuration is now available. Click **Run** next to your virtual device.

==}

#### Running on a real device

To run the Compose Multiplatform application on a real iOS device. You'll need the following:

* The `TEAM_ID` associated with your [Apple ID](https://support.apple.com/en-us/HT204316)
* The iOS device registered in Xcode

##### Finding your Team ID

In the terminal, run `kdoctor --team-ids` to find your Team ID.
KDoctor will list all Team IDs currently configured on your system.

To run the application, set the `TEAM_ID`:

{==

1. In the project, navigate to the `iosApp/Configuration/Config.xcconfig` file.
2. Set your `TEAM_ID`.
3. Re-open the project in Android Studio. It should show the registered iOS device in the `iosApp` run configuration.

==}

### On Desktop

To run the application as a JVM target on desktop:

{==

1. In the list of run configurations, select **Edit Configurations**.
2. Click **Add new configuration** and select **Gradle**.
3. Set `run` configuration under `Run`.
4. Select the desired target under `Gradle project` to be executed (for example: `appyx:demos:appyx-navigation:desktop`).
5. The desktop configuration for the desired target is now available. Click **Run** to execute.

==}

### On Web

To run the application on web:

{==

1. In the list of run configurations, select **Edit Configurations**.
2. Click **Add new configuration** and select **Gradle**.
3. Set `jsBrowserDevelopmentRun` under `Run`.
4. Select the desired target under `Gradle project` to be executed (for example: `appyx:demos:appyx-navigation:web`).
5. The web configuration for the desired target is now available. Click **Run** to execute.

==}
