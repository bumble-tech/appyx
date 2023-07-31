# Back stack

Implements a simple linear history:

- The last element at the end of the stack is considered "active".
- All other elements are considered stashed.
- Children associated with stashed elements are off the screen but kept alive (see how the counter values reflect this on the video)

The back stack can never be empty – it always contains at least one element.

The back stack also supports different back press strategies (see further down below).


## Standard visualisations

### Slider

Class: `BackStackSlider`

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/backstack/slider/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:backstack:slider:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-backstack-slider",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

### Parallax

Class: `BackStackParallax`

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/backstack/parallax/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:backstack:parallax:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-backstack-parallax",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

### 3D stack

Class: `BackStack3D`

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/backstack/stack3d/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:backstack:stack3d:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-backstack-stack3d",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

### Fader

Class: `BackStackFader`

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/backstack/fader/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:backstack:fader:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-backstack-fader",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

### Custom

You can always create your own visualisations for Appyx components. Find more info in [UI representation](../interactions/uirepresentation.md).



## ModelState

```kotlin
    @Parcelize
    data class State<InteractionTarget>(
        /**
         * Elements that have been created, but not yet moved to an active state
         */
        val created: Elements<InteractionTarget> = listOf(),
    
        /**
         * The currently active element.
         * There should be only one such element in the stack.
         */
        val active: Element<InteractionTarget>,
    
        /**
         * Elements stashed in the back stack (history).
         */
        val stashed: Elements<InteractionTarget> = listOf(),
    
        /**
         * Elements that will be destroyed after reaching this state.
         */
        val destroyed: Elements<InteractionTarget> = listOf(),
    ) : Parcelable
```

## Constructing the back stack

Note: As the back stack can never be empty, the initial list in the constructor must contain at 
least one element.

```kotlin
// InteractionTarget – generic type
sealed class InteractionTarget {
    data class SomeElement(val someParam: Int) : InteractionTarget()
}

private val backStack: BackStack<InteractionTarget> = BackStack(
    model = BackStackModel(
        initialTargets = listOf(InteractionTarget.SomeElement),
        savedStateMap = buildContext.savedStateMap
    ),
    motionController = { BackStackSlider(it) } // or other visualisations 
)
```

## Operations

#### Push

`backStack.push(navTarget)`

Effect on stack:
```
[A, B, C] + Push(D) = [A, B, C, D]
```

Transitions the active element `ACTIVE` -> `STASHED`.
Adds a new element at the end of the stack with a `CREATED` -> `ACTIVE` transition.


#### Replace

`backStack.replace(navTarget)`

Effect on stack:
```
[A, B, C] + Replace(D) = [A, B, D]
```

Transitions the active element `ACTIVE` -> `DESTROYED`, which will be removed when the transition finishes.
Adds a new element at the end of the stack with a `CREATED` -> `ACTIVE` transition.


#### Pop

`backStack.pop(navTarget)`

Effect on stack:
```
[A, B, C] + Pop = [A, B]
```

Transitions the active element `ACTIVE` -> `DESTROYED`, which will be removed when the transition finishes.
Transitions the last stashed element `STASHED` -> `ACTIVE`.


#### Single top

`backStack.singleTop(navTarget)`

Effect on stack: depends on the contents of the stack:

```
[A, B, C, D] + SingleTop(B)  = [A, B]          // of same type and equals, acts as n * Pop
[A, B, C, D] + SingleTop(B') = [A, B']         // of same type but not equals, acts as n * Pop + Replace
[A, B, C, D] + SingleTop(E)  = [A, B, C, D, E] // not found, acts as Push
```


## Back press strategy

You can override the default strategy in the constructor. You're not limited to using the provided classes, feel free to implement your own.

```kotlin
class BackStack<NavTarget : Any>(
    /* ... */
    backPressHandler: BackPressHandlerStrategy<NavTarget, State> = PopBackPressHandler(),
    /* ... */
) 
```

#### PopBackPressHandler

The default back press handling strategy. Runs a `Pop` operation.

#### DontHandleBackPress

Serves as a no-op.

