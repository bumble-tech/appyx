package com.bumble.appyx.interactions.core.ui.property

import androidx.compose.runtime.Composable
import com.bumble.appyx.interactions.core.ui.LocalMotionProperties
import kotlinx.coroutines.flow.StateFlow

@Composable
inline fun <reified M : MotionProperty<*, *>> motionProperty(): M? {
    val motionProperties = LocalMotionProperties.current
    return motionProperties?.find { it is M } as M?
}

@Composable
inline fun <T, reified M : MotionProperty<T, *>> motionPropertyState(): StateFlow<T>? {
    val motionProperties = LocalMotionProperties.current
    return (motionProperties?.find { it is M } as M?)?.renderValueFlow
}

@Composable
inline fun <T, reified M : MotionProperty<T, *>> motionPropertyRenderValue(): T? {
    val motionProperties = LocalMotionProperties.current
    return (motionProperties?.find { it is M } as M?)?.renderValue ?: return null
}
