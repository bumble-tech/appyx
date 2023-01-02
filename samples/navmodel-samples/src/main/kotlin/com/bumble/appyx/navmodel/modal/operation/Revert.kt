package com.bumble.appyx.navmodel.modal.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.modal.Modal
import com.bumble.appyx.navmodel.modal.Modal.State.FULL_SCREEN
import com.bumble.appyx.navmodel.modal.Modal.State.MODAL
import com.bumble.appyx.navmodel.modal.ModalElements
import kotlinx.parcelize.Parcelize

@Parcelize
class Revert<T : Parcelable> : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>): Boolean =
        true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements.map {
            when (it.targetState) {
                FULL_SCREEN -> {
                    it.transitionTo(
                        newTargetState = MODAL,
                        operation = this
                    )
                }
                MODAL -> {
                    it.transitionTo(
                        newTargetState = FULL_SCREEN,
                        operation = this
                    )
                }
                else -> {
                    it
                }
            }
        }
    }
}

fun <T : Parcelable> Modal<T>.revert() {
    accept(Revert())
}
