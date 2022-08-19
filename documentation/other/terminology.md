# Terminology

### Node
The main structural element of the Appyx tree.
 
It might or might not have a view, has its own lifecycle, and can host plugins. 

See more in [Nodes and routing](../composable-navigation/index.md).

#### Root
The topmost `Node` in the tree.

#### Leaf Node
A `Node` without any further children.

#### Parent Node
A `Node` that has other child Nodes. It has a `NavModel` too.

#### Container
A `Node` with children whose only responsibility is to coordinate between them. This allows both the children and the container to each keep a single responsibility.

### Plugin
A generic piece of functionality that can be added into a `Node`. 

Typically you will want to implement any extra concerns, any architectural patterns, or other moving parts as plugins. See more in [Plugins](plugins.md)

### NavModel
States and operations defining the abstract model of navigation. An abstraction to describe different navigation mechanism such as back stacks, card stacks, view pagers, or any other custom navigation mechanism.

#### Back stack
A straightforward NavModel implementation: supports a linear history and a single child being active on screen. 

### Builder
A class to build a ```Node``` from its dependencies.






