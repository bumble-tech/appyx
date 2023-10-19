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
                    RootNode(buildContext = it)
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
                    RootNode(buildContext = it)
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
                        RootNode(buildContext = it)
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

```kotlin
val backEvents: Channel<Unit> = Channel()

fun MainViewController() = ComposeUIViewController {
    YourAppTheme {
        IosNodeHost(
            modifier = Modifier,
            // See back handling section in the docs below!
            onBackPressedEvents = backEvents.receiveAsFlow()
        ) {
            RootNode(
                buildContext = it
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

