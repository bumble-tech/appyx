package com.bumble.appyx.navmodel.modal.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.modal.Modal
import com.bumble.appyx.navmodel.modal.Modal.State.MODAL
import com.bumble.appyx.navmodel.modal.ModalElements
import com.bumble.appyx.core.navigation.NavKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Show<T : Parcelable>(
    private val key: NavKey<T>
) : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>) = true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements.map {
            if (it.key == key) {
                it.transitionTo(
                    newTargetState = MODAL,
                    operation = this
                )
            } else {
                it
            }
        }
    }
}

fun <T : Parcelable> Modal<T>.show(key: NavKey<T>) {
    accept(Show(key))
}
