# Terminology

### Node
The main structural element of the Appyx tree.
 
It might or might not have a view, has its own lifecycle, and can host plugins. 

See more in [Nodes and routing](nodes-and-routing.md).

#### Root
The topmost `Node` in the tree.

#### Leaf Node
A `Node` without any further children.

#### Parent Node
A `Node` that has other child Nodes. It has a `Routing source` too.

#### Container
A `Node` with children whose only responsibility is to coordinate between them. This allows both the children and the container to each keep a single responsibility.

### Plugin
A generic piece of functionality that can be added into a `Node`. 

Typically you will want to implement any extra concerns, any architectural patterns, or other moving parts as plugins. See more in [Plugins](plugins.md)

### Routing source
A control structure dealing with state changes of children.

#### Back stack
A straightforward routing source implementation: supports a linear history and a single child being active on screen. 

### Builder
A class to build a ```Node``` from its dependencies.






