---
title: Appyx Navigation – Sample app
---

# Appyx Navigation – Sample app

{{
    compose_mpp_sample(
        project_output_directory="demos/appyx-navigation/web/build/dist/js/productionExecutable",
        compile_task=":demos:appyx-navigation:web:jsBrowserDistribution",
        width=512,
        height=500,
        target_directory="samples/documentation-appyx-navigation",
        html_file_name="index.html",
        classname="compose_mpp_sample_app",
    )
}}

You can try out the app right here in the browser. The above example is interactive!


## Multiplatform

![Overview](/appyx/assets/navigation/sample-app.png)

Check out the `:demos:appyx-navigation` module in the project (see on [GitHub](https://github.com/bumble-tech/appyx/tree/2.x/demos/appyx-navigation)) to launch platform-specific variants.


## Points of interest

You can experiment with the following in the sample app:

### Gestures & transitions

- Swipe left-right in the cake pager
- Tap on any cake to enter or exit hero mode
- Swipe left-right while in hero mode
- Swipe up-down between hero and list mode for a gradual transition


### Remote triggered transitions

**In the app**:

- Go to Home, tap the `Go to a random cake` button


**Trigger via deep link (basic)**:

- Android: `adb shell am start -a "android.intent.action.VIEW" -d "appyx://randomcake"`
- iOS: `xcrun simctl openurl booted 'appyx://randomcake'`


**Trigger via deep link (advanced, waits for user to finish logging in)**:

1. Go to `Profile` in the bottom menu, tap `Log out`
2. Close the app 
3. Trigger the deep link
    - Android: `adb shell am start -a "android.intent.action.VIEW" -d "appyx://randomcake-wait"`
    - iOS: `xcrun simctl openurl booted 'appyx://randomcake-wait'`
4. Tap `Log in` and see the deep link action resume


### Scoped dependencies

<img src="/appyx/assets/navigation/cake-app-tree.png" width="450">

The cart object lives inside the `MainNode`. It's the same instance passed to all child nodes in the tree, but is destroyed when logging out and logging back in. Try:

- Add items to cart, log out from `Profile`, log back in
- Add items to cart, go to checkout, manipulate cart, finish checkout flow


### Other

- Resize window (desktop or standalone launched web) to see the Material 3 Navigation Bar automatically switches to a Navigation Rail depending on screen size.


## Relevant pages from the documentation

### Concepts

Topics on how the sample app is put together, and how navigation works in it: 

- [Model-driven navigation](concepts/model-driven-navigation.md)
- [Composable navigation](concepts/composable-navigation.md)
- [Implicit navigation](concepts/implicit-navigation.md)
- [Explicit navigation](concepts/explicit-navigation.md)

### Features

Library features used in the app:

- [Deep link navigation](features/deep-linking.md)
- [Material 3 support](features/material3.md)
- [Scoped DI](features/scoped-di.md)
- [Surviving configuration changes](features/surviving-configuration-changes.md)


### What's behind the custom component

The cake slider / hero transition is a custom component. Check these out on how it's put together:

- [Code](https://github.com/bumble-tech/appyx/tree/2.x/demos/appyx-navigation/common/src/commonMain/kotlin/com/bumble/appyx/navigation/component/spotlighthero)
- [Appyx Interactions intro](../interactions/index.md)
- [Creating custom components with Appyx Interactions](../interactions/appyxcomponent.md)

