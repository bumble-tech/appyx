{% include-markdown "./deprecation.md" %}

# FAQ


## Navigation-related

#### **Q: How does Appyx relate to Jetpack Compose Navigation?**

We wrote an article on this: [Appyx vs Jetpack Compose Navigation](https://medium.com/bumble-tech/appyx-vs-jetpack-compose-navigation-b91bd23369f2)

While Appyx represents a different paradigm, it can also co-exist with Jetpack Compose Navigation. This can be helpful if you want to use Appyx for in-screen mechanisms only, or if you plan to migrate gradually.

See [Sample apps](how-to-use-appyx/sample-apps.md) for more details.

---

#### **Q: How does Appyx compare against other navigation solutions?**

The core concepts of navigation in Appyx differ from most navigation libraries: 

1. You don't have a concept of the "screen" present in the model
2. You can define your own navigation models
3. On the UI level you can transform what feels like the "screen" itself

See [Model-driven navigation](navigation/model-driven-navigation.md) for more details.

---


#### **Q: How can I navigate to a specific part of my Appyx tree?**

In most cases [Implicit navigation](navigation/implicit-navigation.md) can be your primary choice, and you don't need to explicitly specify a remote point in the tree. This is helpful to avoid coupling.

For those cases when you can't avoid it, [Explicit navigation](navigation/explicit-navigation.md) and [Deep linking](navigation/deep-linking.md) covers you.

---


#### **Q: What about dialogs & bottom sheets?**

You can use Appyx in conjunction with Accompanist or any other Compose mechanism.

If you wish, you can model your own Modal with Appyx too. We'll add an example soon.

---

#### **Q: Can I have a bottom sheet conditionally?**

You could use a similar approach as we do with back buttons in `SamplesContainerNode` you can find in the `:app` module: store a flag in the `NavTarget` that can be different per instance.

---

## Using Appyx in an app


#### **Q: Is it an all or nothing approach?**

No, you can adopt Appyx gradually:

- Plug it in to one screen and just utilise its screen transformation capabilities (e.g. [Cards](navmodel/cards.md))
- Plug it in to a few screens and substitute another navigation mechanism with it, such as [Jetpack Compose Navigation](how-to-use-appyx/sample-apps.md#appyx-jetpack-compose-navigation-example)

---

#### **Q: What architectural patterns can I use?**

Appyx is agnostic of architectural patterns. You can use any architectural pattern in the `Nodes` you'd like. You can even use a different one in each.

---

#### **Q: Can I use it with ViewModel?**

Yes, we'll add an example soon.

---


#### **Q: Can I use it with Hilt?**

- Our draft PR: [#115](https://github.com/bumble-tech/appyx/pull/115) (Feel free to provide feedback!)
- [https://github.com/jbreitfeller-sfix/appyx-playground](https://github.com/jbreitfeller-sfix/appyx-playground) another approach on this topic

---

## Performance-related

#### **Q: Are `Nodes` kept alive?**

In short: you can decide whether a `Node`:

- is on-screen
- is off-screen but kept alive
- is off-screen and becomes destroyed

Check the [Lifecycle](apps/lifecycle.md#on-screen-off-screen) for more details.

---


## On the project itself

#### **Q: Is it production ready?**

Yes, Appyx matured to its stable version. We also use it at Bumble in production, and as such, we're committed to maintaining and improving it.

---

#### **Q: What's your roadmap?**

We're full with ideas where to take Appyx further! A more detailed roadmap will be added later. Come back for more updates.

---

## Other

Have a question? Raise it in [Discussions](https://github.com/bumble-tech/appyx/discussions)!.
