# Appyx Interactions – Gestures

{==

Gesture-based transitions are usually done by direct UI mutation. In Appyx, they're done by gradually mutating the abstract model instead.

==}

Gestures in Appyx translate to `Operations` to be executed gradually over the [model state](transitionmodel.md). This allows you to greatly simplify gesture-handling client code, as well as allowing gestures to result in the same exact visual outcome as any other transition between two states.

The big picture:

1. You receive information on the gesture's direction and the current model state
2. Based on this, you will declare what [operation](transitionmodel.md#operation) the gesture corresponds to (if any)
3. You will also specify which visual endpoint should the gesture complete this operation towards



## Detecting the gesture's direction

Appyx comes with the following gesture-related helpers that report the direction of a drag:

```kotlin
/**
 * The angle of the drag such that:
 *
 * - 12 o'clock = 0 degrees
 * - 3 o'clock = 90 degrees
 */
fun dragAngleDegrees(delta: Offset): Float

/**
 * The horizontal aspect of the drag (LEFT or RIGHT), regardless of the dominant direction
 */
fun dragHorizontalDirection(delta: Offset): Drag.HorizontalDirection

/**
 * The vertical aspect of the drag (UP or DOWN), regardless of the dominant direction
 */
fun dragVerticalDirection(delta: Offset): Drag.VerticalDirection

/**
 * The dominant direction of the drag of 4 possible directions
 */
fun dragDirection4(delta: Offset): Drag.Direction4

/**
 * The dominant direction of the drag of 8 possible directions
 */
fun dragDirection8(delta: Offset): Drag.Direction8

/**
 * The drag direction interpreted on the clock
 */
fun dragClockDirection(delta: Offset): Drag.ClockDirection


enum class HorizontalDirection {
    LEFT, RIGHT
}

enum class VerticalDirection {
    UP, DOWN
}

enum class Direction4 {
    UP, DOWN, LEFT, RIGHT
}

enum class Direction8 {
    UP, UPRIGHT, RIGHT, DOWNRIGHT, DOWN, DOWNLEFT, LEFT, UPLEFT
}

enum class ClockDirection(val digit: Int) {
    Clock1(1),
    Clock2(2),
    Clock3(3),
    Clock4(4),
    Clock5(5),
    Clock6(6),
    Clock7(7),
    Clock8(8),
    Clock9(9),
    Clock10(10),
    Clock11(11),
    Clock12(12)
}
    
```

## Gesture

A gesture is defined as:

```kotlin
open class Gesture<InteractionTarget, ModelState>(
    val operation: Operation<ModelState>,
    val completeAt: Offset
)
```

It completes an [Operation](operations.md) at a visual endpoint represented by an `Offset`. You can read about the latter further below on this page.


## Gesture factory

A `GestureFactory` is expected to return an instance of a `Gesture` given:

- the current model state
- the drag delta of the gesture
- the density

```kotlin
class Gestures<InteractionTarget> : GestureFactory<InteractionTarget, SomeModel.State<InteractionTarget>> {
    
    override fun createGesture(
        state: SomeModel.State<InteractionTarget>,
        delta: Offset,
        density: Density
    ): Gesture<InteractionTarget, SomeModel.State<InteractionTarget>> {
        TODO()
    }
}
```

`GestureFactory` implementations are usually nested in a specific UI representation. This makes sense since driving a model with gestures usually results in a natural UX if the gestures are in sync with what happens in the UI. However, it's not a requirement – you could use different gestures than the default one for the UI representation. 


## Choosing an operation

Let's see how the internal demo, `TestDrive` implements its gestures:

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/interactions/sample1/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-interactions:interactions:sample1:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-interactions-index-1",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

Based on this, what we'd want is:

- If the element is in state `A`, a rightward gesture should move it to state `B`
- If the element is in state `B`, a downward gesture should move it to state `C`
- If the element is in state `C`, a leftward gesture should move it to state `D`
- If the element is in state `D`, an upward gesture should move it to state `A`

`TestDrive` already comes with an operation `MoveTo(private val elementState: TestDriveModel.State.ElementState)` that we can make use of.

The `GestureFactory` implementation should look like this then:

```kotlin
class Gestures<InteractionTarget>(
    transitionBounds: TransitionBounds,
) : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
    // We calculate these based on the offset differences between the actual TargetUiState values
    private val widthDp = offsetB.x - offsetA.x
    private val heightDp = offsetD.y - offsetA.y

    override fun createGesture(
        state: TestDriveModel.State<InteractionTarget>,
        delta: Offset,
        density: Density
    ): Gesture<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
        val width = with(density) { widthDp.toPx() }
        val height = with(density) { heightDp.toPx() }

        val direction = dragDirection8(delta)
        return when (state.elementState) {
            A -> when (direction) {
                RIGHT -> Gesture(MoveTo(B), Offset(width, 0f))
                else -> Gesture.Noop()
            }
            B -> when (direction) {
                DOWN -> Gesture(MoveTo(C), Offset(0f, height))
                else -> Gesture.Noop()
            }
            C -> when (direction) {
                LEFT -> Gesture(MoveTo(D), Offset(-width, 0f))
                else -> Gesture.Noop()
            }
            D -> when (direction) {
                UP -> Gesture(MoveTo(A), Offset(0f, -height))
                else -> Gesture.Noop()
            }
        }
    }
}
```
A more advanced version allows every state to move to each of the other 3 states. Try this sample - here you can also move the element backwards to the previous state, and also diagonally across:

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/interactions/sample1/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-interactions:interactions:sample1:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-interactions-index-1",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

To allow this, we now handle more directions:

```kotlin
class Gestures<InteractionTarget>(
    transitionBounds: TransitionBounds,
) : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
    private val widthDp = offsetB.x - offsetA.x
    private val heightDp = offsetD.y - offsetA.y

    override fun createGesture(
        state: TestDriveModel.State<InteractionTarget>,
        delta: Offset,
        density: Density
    ): Gesture<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
        val width = with(density) { widthDp.toPx() }
        val height = with(density) { heightDp.toPx() }

        val direction = dragDirection8(delta)
        return when (state.elementState) {
            A -> when (direction) {
                RIGHT -> Gesture(MoveTo(B), Offset(width, 0f))
                DOWNRIGHT -> Gesture(MoveTo(C), Offset(width, height))
                DOWN -> Gesture(MoveTo(D), Offset(0f, height))
                else -> Gesture.Noop()
            }
            B -> when (direction) {
                DOWN -> Gesture(MoveTo(C), Offset(0f, height))
                DOWNLEFT -> Gesture(MoveTo(D), Offset(-width, height))
                LEFT -> Gesture(MoveTo(A), Offset(-width, 0f))
                else -> Gesture.Noop()
            }
            C -> when (direction) {
                LEFT -> Gesture(MoveTo(D), Offset(-width, 0f))
                UPLEFT -> Gesture(MoveTo(A), Offset(-width, -height))
                UP -> Gesture(MoveTo(B), Offset(0f, -height))
                else -> Gesture.Noop()
            }
            D -> when (direction) {
                UP -> Gesture(MoveTo(A), Offset(0f, -height))
                UPRIGHT -> Gesture(MoveTo(B), Offset(width, -height))
                RIGHT -> Gesture(MoveTo(C), Offset(width, 0f))
                else -> Gesture.Noop()
            }
        }
    }
}
```

Note how whenever a direction should not result in doing anything, you can always return `Gesture.Noop()`



## Visual endpoint

In all of the above cases we needed to pass an `Offset` as a second argument to the `Gesture`.

Note how these offsets have different values in each case: they represent the vector from the current visual to the expected target visual state. This is the vector along which Appyx will interpret the gesture from a 0% to a 100% completion.

For example, the vector between `A` and `C` is `Offset(width, height)` as the gesture should be completed along the downward right diagonal from `A`.

Note that while you're not strictly required to match this offset with how an element moves on the screen, it's recommended to do so – otherwise the UX will be confusing in most cases.


## Drag prediction

The target UI state can be rendered immediately upon starting a drag. Note how the target state here matches not only the position, but the scale and the rotation too.

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/gestures/dragprediction/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-interactions:gestures:dragprediction:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-gestures-drag-prediction",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}


## Settling incomplete gestures

When releasing the drag before reaching the target, the operation is settled. Depending on how far the gesture got, it might be:

- rounded up towards completion, or
- rounded down towards reverting it.

The default threshold is `0.5f` (50%), and can be changed between `0f` (0%) and `1f` (100%). For example, a value of `0.2f` would mean the gesture would be considered completed even after a relatively short movement.

This can be configured in `GestureSettleConfig`, along with animation specs of completion and reversal: 

```kotlin
GestureSettleConfig(
    completionThreshold = 0.2f, // the default is 0.5f
    completeGestureSpec = spring(),
    revertGestureSpec = spring(),
)
```

Here's an example that uses a `completionThreshold` value of `0.15f` (15%). Notice that now you can release the drag much closer to the starting point and it will still complete the animation:

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/gestures/incompletedrag/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-interactions:gestures:incompletedrag:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-interactions-gestures-incomplete-drag",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

## Configuring gestures in the AppyxComponent

You can connect your gesture detection to your [AppyxComponent](appyxcomponent.md) in client code such as:

```kotlin
@Composable
fun SomeComposable() {
    val appyxComponent = remember {
        SomeAppyxComponent(
            // Required
            model = SomeTransitionModel(/*...*/),
            motionController = { SomeMotionController(/*...*/) } ,
            
            // Optional
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            gestureFactory = { SomeMotionController.Gestures(/*...*/) },
            gestureSettleConfig = GestureSettleConfig(
                completionThreshold = 0.5f,
                completeGestureSpec = spring(),
                revertGestureSpec = spring(),
            ),
        )
    }
}
```

Note that as stated above, gestures are usually come hand in hand with a specific visual representation, but you're not strictly limited to using the same ones. For example, you could use a combination of `SpotlightFader` + `SpotlightSlider.Gestures` to have cross-fading visuals controlled by swiping gestures.  
