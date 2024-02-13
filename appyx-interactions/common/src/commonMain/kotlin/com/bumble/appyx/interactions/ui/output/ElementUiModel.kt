package com.bumble.appyx.interactions.ui.output

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.ui.property.MotionProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Immutable
data class ElementUiModel<InteractionTarget>(
    val element: Element<InteractionTarget>,
    val visibleState: StateFlow<Boolean>,
    val motionProperties: List<MotionProperty<*, *>>,
    /**
     * The purpose of this container is to be present in composition as long as corresponding
     * Element exists. With its help we calculate visibility of the element and animate MotionProperties
     * even if the element is invisible. This help to break circular dependency when element is
     * not present in the composition and therefore we can't update MotionProperties, and because
     * MotionProperties are not updated element can never enter composition even when it should.
     */
    val persistentContainer: @Composable () -> Unit,
    val modifier: Modifier,
    val progress: Flow<Float>
)


