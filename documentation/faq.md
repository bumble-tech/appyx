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


#### **Q: What about dialogs & bottom sheets?**

You can use Appyx in conjunction with Accompanist or any other Compose mechanism.

If you wish, you can model your own Modal with Appyx too. We'll add an example soon.

---

## Using Appyx in an app


#### **Q: What architectural patterns can I use?**

Appyx is agnostic of architectural patterns. You can use any architectural pattern in the `Nodes` you'd like. You can even use a different one in each.

---

#### **Q: Can I use it with ViewModel?**

Yes, we'll add an example soon.

---


#### **Q: Can I use it with Hilt?**

Yes, we'll add an example soon.

---


## On the project itself

#### **Q: Is it production ready?**

We do use it at Bumble in production, and as such, we're committed to maintaining and improving it.

The project is currently in an alpha stage only to allow API changes for now. However, we commit ourselves to communicating all such changes in the [Changelog](releases/changelog.md).

---

#### **Q: What's your roadmap?**

We're full with ideas where to take Appyx further! A more detailed roadmap will be added later. Come back for more updates.

---

## Other

Have a question? Raise it in [Discussions](https://github.com/bumble-tech/appyx/discussions)!.