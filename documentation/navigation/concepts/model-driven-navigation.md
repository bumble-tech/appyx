---
title: Appyx Navigation – Model-driven navigation
---

# Model-driven navigation


## Your own navigation model

Generally speaking, most navigation solutions have fixed navigation mechanisms (e.g. a back stack).

Appyx gives you the freedom to define your own navigation model using any [Appyx component](../../components/index.md). For example, you can implement any of the examples you see here with the same approach, then use them in your navigation tree:

<img src="https://i.imgur.com/N8rEPrJ.gif" width="150"> <img src="https://i.imgur.com/esLXh61.gif" width="150"> <img src="https://i.imgur.com/8gy3Ghb.gif" width="150"> <img src="https://cdn-images-1.medium.com/max/1600/1*mEg8Ebem3Hd2knQSA0yI1A.gif" width="150">


## No screen, only a viewport

Generally speaking, most navigation solutions model a "Screen" and focus on how to get from one screen to another.

Appyx does not have the concept of the screen baked in – there's only a viewport, and whatever fills the available space will feel like the screen to the user.

This freedom allows you to implement:

- Navigation that feels like going from "screen to screen"
- Navigation "inside the screen"
- Navigation that bridges between the two

For example, you can transform the screen itself as part of navigation:

<img src="https://i.imgur.com/EKjwaqW.gif" width="150">


## AppyxComponents

An `AppyxComponent` implements any of the above mechanisms. 

See [AppyxComponents](../../components/index.md) for more details. 


## Composable navigation

`AppyxComponents` in Appyx are composable.

See [Composable navigation](composable-navigation.md) for more details.
