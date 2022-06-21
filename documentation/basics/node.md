# Node

```Nodes``` are the main structural element of an Appyx tree. 
 
A ```Node``` is analogous to a Fragment in many ways: it has a ```@Composable View``` which is a part of UI tree, and it has similar (although simplified) lifecycle events.

## Tree structure of Nodes
To support delegating responsibilities, ```Nodes``` can have children, which can have their own children, and so on. 

There are two type of ```Nodes``` in Appyx tree: ```Node``` and ```ParentNode```.
1. ```Node``` is a leaf in a graph representation. It can not have children.
2. ```ParentNode``` is a node in a graph representation. It can have children.

This is similar to nesting Fragments, however, the typical nesting level of ```Node``` tree is encouraged to be a lot deeper than it's usual with Fragments. Also, children are referenced directly instead of a FragmentManager-like mechanism).

## Base functionality
```Node``` as a base class provides these functionalities:
- has a lifecycle
- can host generic [Plugins] to extract all extra concerns to

```ParentNode``` has extends ```Node's``` functionality:
- has children and manages their lifecycle
- has routing source - data source for children entities

## Responsibilities
To avoid creating a god object similar to what can happen with Activities or Fragments, we consciously try to keep all extra responsibilities out of them. 

On the client code side we highly encourage to extract all extra concerns into either dedicated classes, or delegate them to children in the tree.
