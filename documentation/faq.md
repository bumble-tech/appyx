---
title: FAQ
---

# FAQ


## Navigation-related

#### **Q: How does Appyx Navigation relate to Jetpack Compose Navigation?**

We wrote an article on this in the context of Appyx 1.x: [Appyx vs Jetpack Compose Navigation](https://medium.com/bumble-tech/appyx-vs-jetpack-compose-navigation-b91bd23369f2).

Most of the same arguments apply to Appyx 2.x too.

While Appyx represents a different paradigm, it can also co-exist with Jetpack Compose Navigation. This can be helpful if you want to use Appyx for in-screen mechanisms only, or if you plan to migrate gradually.

See [Appyx + Compose Navigation](navigation/integrations/compose-navigation.md) for more details.

---

#### **Q: How does Appyx Navigation compare against other solutions?**

The core concepts of navigation in Appyx differ from most navigation libraries: 

1. You don't have a concept of the "screen" present in the model
2. You can define your own navigation models using [Appyx Components](components/index.md)
3. On the UI level you can transform what feels like the "screen" itself

See [Model-driven navigation](navigation/concepts/model-driven-navigation.md) for more details.

---


#### **Q: How can I navigate to a specific part of my Appyx tree?**

In most cases [Implicit navigation](navigation/concepts/implicit-navigation.md) can be your primary choice, and you don't need to explicitly specify a remote point in the tree. This is helpful to avoid coupling.

For those cases when you can't avoid it, [Explicit navigation](navigation/concepts/explicit-navigation.md) and [Deep linking](navigation/features/deep-linking.md) covers you.

---


#### **Q: What about dialogs & bottom sheets?**

You can use Appyx in conjunction with Accompanist or any other Compose mechanism.

If you wish, you can model your own Modal with Appyx too. We'll add an example soon.


---


## Using Appyx in an app


#### **Q: Is it an all or nothing approach?**

No, you can adopt Appyx gradually:

- Plug an [Appyx Components](components/index.md) in to any screen and just use it as a UI component.
- Plug it in to a few screens and substitute another navigation mechanism with it, such as [Jetpack Compose Navigation](navigation/integrations/compose-navigation.md)

---

#### **Q: What architectural patterns can I use?**

Appyx is agnostic of architectural patterns. You can use any architectural pattern in the `Nodes` you'd like. You can even use a different one in each.

---

#### **Q: Can I use it with ViewModel?**

Please see [Appyx + ViewModel](navigation/integrations/viewmodel.md).

---


#### **Q: Can I use it with Hilt?**

Please see [Appyx + DI frameworks](navigation/integrations/di-frameworks.md).

---

## Performance-related

#### **Q: Are `Nodes` kept alive?**

In short: you can decide whether a `Node`:

- is on-screen
- is off-screen but kept alive
- is off-screen and becomes destroyed

Check the [Lifecycle](navigation/features/lifecycle.md#on-screen-off-screen) for more details.

---


## On the project itself

#### **Q: Is it production ready?**

Appyx matured to its stable version in the `1.x` branch. 

The `2.x` branch is currently in alpha. 

We use Appyx at Bumble in production, and as such, we're committed to maintaining and improving it.

---

## Other

Have a question? Come over to **#appyx** on Kotlinlang Slack!
