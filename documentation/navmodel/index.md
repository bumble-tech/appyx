# Navigation models

Navigation models are one of the most important abstractions of Appyx.

## What's a NavModel?

Navigation models describe navigation itself – by the states and operations they define, any custom navigation mechanism can be implemented. The state is then yours to be controlled from your business logic, which then becomes the state of navigation directly.

`NavModel` capabilities differ from implementation to implementation, however, typically:

1. They store information on the states of all children 
2. They behave like a state machine
3. They offer some public API to trigger changing the state of children  


## A list of navigation models

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
