# Routing sources

Routing sources are the single most important abstraction of Appyx after `Node`.

## What's a routing source?

Routing sources are controlling entities dealing with state changes of children. 

Their capabilities differ from implementation to implementation, however, typically:

1. They store information on the states all children 
2. They behave like a state machine
3. They offer some public API to trigger changing the state of children  


## A list of routing sources

You can take a look at some of these examples:

1. [Back stack](backstack.md)
2. [Tiles](tiles.md)
3. [Promoter carousel](promoter.md)

When you feel ready, you can try to [implement your own routing source](custom.md).


## What does a routing source not do?

The routing source represents only the model, not the looks:

- UI representation depends on your `@Composable` view hosting the children – See [Adding children to the view](../ui/children-view.md)
- Transition animation (if any) depends on the `TransitionHandler` interpreting the states defined by the `RoutingSource` in question – See individual routing sources for details.
