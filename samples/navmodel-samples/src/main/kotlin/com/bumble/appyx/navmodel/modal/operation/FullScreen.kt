package com.bumble.appyx.navmodel.modal.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.modal.Modal
import com.bumble.appyx.navmodel.modal.Modal.State.FULL_SCREEN
import com.bumble.appyx.navmodel.modal.ModalElements
import com.bumble.appyx.core.navigation.NavKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class FullScreen<T : Parcelable>(
    private val key: NavKey<T>
) : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>) = true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements.map {
            if (it.key == key) {
                it.transitionTo(
                    newTargetState = FULL_SCREEN,
                    operation = this
                )
            } else {
                it
            }
        }
    }
}

fun <T : Parcelable> Modal<T>.fullScreen(key: NavKey<T>) {
    accept(FullScreen(key))
}
