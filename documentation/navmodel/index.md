# Navigation models

Navigation model is a core concept of Appyx.


## What's a NavModel?

Navigation models describe navigation itself – by the states and operations they define, any custom navigation mechanism can be implemented.

`NavModel` capabilities differ across implementations, however, typically:

1. They store information on the states of all children 
2. They behave like a state machine
3. They offer some public API to trigger changing the state of children  


## Some examples of navigation models

You can take a look at some of these examples:

1. [Back stack](backstack.md)
1. [Spotlight](spotlight.md)
2. [Tiles](tiles.md)
3. [Promoter carousel](promoter.md)

When you feel ready, you can try to [implement your own NavModel](custom.md).


## What does a NavModel not do?

The `NavModel` represents only the model, not the looks:

- UI representation depends on your `@Composable` view hosting the children – See [Adding children to the view](../ui/children-view.md)
- [Transition animations](../ui/transitions.md) (if any) is a separate concern
