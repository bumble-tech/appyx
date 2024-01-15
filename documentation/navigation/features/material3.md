---
title: Appyx Navigation – Material 3 support
---

# Material 3 support

Optional support for [Material 3](https://m3.material.io/) that renders Appyx navigation using a [Navigation bar](https://m3.material.io/components/navigation-bar/overview) or a [Navigation rail](https://m3.material.io/components/navigation-rail/overview).


## How to use

### 1. Setup

To add the proper gradle artifacts, please refer to: [Downloads](../../releases/downloads.md#material-3)


### 2. Define your navigation config

```kotlin
import com.bumble.appyx.utils.material3.AppyxNavItem
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
enum class MainNavItem : Parcelable {
    DESTINATION1, DESTINATION2, DESTINATION3;

    companion object {
        val resolver: (MainNavItem) -> AppyxNavItem = { navBarItem ->
            when (navBarItem) {
                DESTINATION1 -> AppyxNavItem(
                    text = "Title 1",
                    unselectedIcon = TODO(),
                    selectedIcon = TODO(),
                    iconModifier = Modifier,
                    node = { YourAppyxNode1(it) }
                )

                DESTINATION2 -> AppyxNavItem(
                    text = "Title 2",
                    unselectedIcon = TODO(),
                    selectedIcon = TODO(),
                    iconModifier = Modifier,
                    node = { YourAppyxNode2(it) }
                )

                DESTINATION3 -> AppyxNavItem(
                    text = "Title 3",
                    unselectedIcon = TODO(),
                    selectedIcon = TODO(),
                    iconModifier = Modifier,
                    node = { YourAppyxNode3(it) }
                )
            }
        }
    }
}
```

### 3. Create an instance of `AppyxMaterial3NavNode` and pass your config to it

```kotlin
AppyxMaterial3NavNode<MainNavItem>(
    nodeContext = nodeContext,
    navTargets = MainNavItem.values().toList(),
    navTargetResolver = MainNavItem.resolver
)
```

### 4. Done!

Use this node as you would use any other Appyx node. `AppyxMaterial3NavNode` renders either a [Navigation bar](https://m3.material.io/components/navigation-bar/overview) or a [Navigation rail](https://m3.material.io/components/navigation-rail/overview) depending on the screen size:

![Appyx + Navigation bar](/appyx/assets/navigation/features/m3/appyx-m3-nb.png) ![Appyx + Navigation rail](/appyx/assets/navigation/features/m3/appyx-m3-nr.png)

You can check the live multiplatform demo in the **:demos:appyx-navigation** module.


## Customisation options

You can set a different animation spec applied to the default crossfade:

```kotlin
AppyxMaterial3NavNode<MainNavItem>(
    animationSpec = spring(Spring.StiffnessMedium)
)
```

Or you can set a different visualisation altogether:

```kotlin
AppyxMaterial3NavNode<MainNavItem>(
    /*...*/
    visualisation = { SpotlightSlider(it) }
)
```

For a list of possible visualisations you can refer to [Spotlight](../../components/spotlight.md) documentation.

You can also extend the class and override when to display a Navigation bar or a rail. These are the defaults:

```kotlin
@Composable
open fun shouldUseNavigationBar(): Boolean =
    LocalScreenSize.current.windowSizeClass == COMPACT

@Composable
open fun shouldUseNavigationRail(): Boolean =
    !shouldUseNavigationBar()
```
