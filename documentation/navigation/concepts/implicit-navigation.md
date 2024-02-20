---
title: Appyx Navigation – Implicit navigation
---

# Implicit navigation

<img src="https://i.imgur.com/hKvOs3w.gif" width="450">

How can we go from one part of the tree to another? In almost all cases navigation can be implicit instead of explicit. We don't need to specify the target – navigation will happen as a consequence of individual pieces of the puzzle.

!!! info "Relevant methods"

    - `Node.onChildFinished(child: Node)` can be overridden by client code to handle a child finishing
    - `Node.finish()` invokes the above method on its parent


## Use-case 1

<img src="https://i.imgur.com/jkZQJBC.png" width="450">

### Requirement

After onboarding finishes, the user should land in the message list screen.

### Solution

1. `O3` calls its `finish()` method
2. `Onboarding` notices `O3` finished; if it had more children, it could switch to another; now it calls `finish()` too
3. `Logged in` notices `Onboarding` finished, and switches its navigation to `Main`
4. `Main` is initialised, and loads its default navigation target (based on product requirements) to be `Messages`
5. `Messages` is initialised, and loads its default navigation target to be `List`

!!! success "Bonus"

    Every `Node` in the above sequence only needed to care about its own local concern.


## Use-case 2

<img src="https://i.imgur.com/jkZQJBC.png" width="450">

### Requirement

Pressing the logout button on the profile screen should land us back to the login screen.

### Solution 

1. `Root` either implements a `logout` callback, or subscribes to the changes of a user repository; in both cases, either the callback or the repository is passed down the tree as a dependency
2. `Profile` invokes the callback or a `logout` method on the repository
3. `Root` notices the state change, and switches its navigation to the `Logged out` scope
4. `Logged out` loads its initial navigation target, `Login`

!!! success "Bonus"

    Note how the entire `Logged in` scope is destroyed without any extra effort. The next time a login happens, all state is created anew. 


## Summary

Implicit navigation allows you to implement navigation without introducing unnecessary coupling in the tree, and successfully covers the majority of navigation scenarios.

In case it's not enough to meet your needs, see the next chapter, [Explicit navigation](explicit-navigation.md)



