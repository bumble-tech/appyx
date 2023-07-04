# Spotlight

Implements a mechanism analogous to a view pager; has a single active element marked by an `activeIndex` ("it's in the spotlight", hence the name), but unlike the back stack, it does not remove other elements.

It's great for flows or tabbed containers.


## Standard visualisations

### Slider

Class: `SpotlightSlider`

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/spotlight/slider/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:spotlight:slider:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-spotlight-slider",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}


### Slider + scale

Class: `SpotlightSliderScale`

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/spotlight/sliderscale/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:spotlight:sliderscale:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-spotlight-sliderscale",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

### Slider + rotation

Class: `SpotlightSliderRotation`

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/spotlight/sliderrotation/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:spotlight:sliderrotation:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-spotlight-sliderrotation",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

### 3D stack

Class: `SpotlightStack3D`

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/spotlight/stack3d/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:spotlight:stack3d:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-spotlight-stack3d",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

### Fader

Class: `SpotlightFader`

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/spotlight/fader/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:spotlight:fader:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-spotlight-fader",
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
    val positions: @RawValue List<Position<InteractionTarget>>,
    val activeIndex: Float
) : Parcelable {

    @Parcelize
    data class Position<InteractionTarget>(
        val elements: Map<Element<InteractionTarget>, ElementState> = mapOf()
    ) : Parcelable

    enum class ElementState {
        CREATED, STANDARD, DESTROYED
    }

    fun hasPrevious(): Boolean =
        activeIndex >= 1

    fun hasNext(): Boolean =
        activeIndex <= positions.lastIndex - 1
}
```

Note that a `Position` in the list can contain multiple elements, each with their own state. This allows an element at a specific position to be destroyed (animated out) and another one to be animated in in its place.




## Constructing an instance

Requires defining items and an active index.

```kotlin
// InteractionTarget – generic type
sealed class InteractionTarget {
    data class SomeElement(val someParam: Int) : InteractionTarget()
}

private val spotlight = Spotlight(
    model = SpotlightModel(
        items = listOf(SomeElement(1), SomeElement(2), SomeElement(3)),
        savedStateMap = null
    ),
    motionController = { SpotlightSlider(it) }
)
```

## Operations

#### Next

`spotlight.next()`

Increases the `activeIndex`.

#### Previous

`spotlight.previous()`

Decreases the `activeIndex`.

#### First

`spotlight.first()`

Sets the `activeIndex` to `0`.

#### Last

`spotlight.last()`

Sets the `activeIndex` to the maximum value.

#### Activate

`spotlight.activate(index)`

Sets the `activeIndex` to a specific value.


#### Update elements

`spotlight.updateElements(items, initialActiveIndex)`

Replaces elements held by the spotlight instance with a new list. Transitions:

- new elements: `CREATED` → `STANDARD`
- destroyed elements: `STANDARD` → `DESTROYED`.

