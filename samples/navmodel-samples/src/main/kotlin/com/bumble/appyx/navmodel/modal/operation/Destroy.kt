package com.bumble.appyx.navmodel.modal.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.modal.Modal
import com.bumble.appyx.navmodel.modal.Modal.State.DESTROYED
import com.bumble.appyx.navmodel.modal.ModalElements
import com.bumble.appyx.core.navigation.NavKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Destroy<T : Parcelable>(
    private val key: NavKey<T>
) : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>) = true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements.map {
            if (it.key == key) {
                it.transitionTo(
                    newTargetState = DESTROYED,
                    operation = this
                )
            } else {
                it
            }
        }
    }
}

fun <T : Parcelable> Modal<T>.destroy(key: NavKey<T>) {
    accept(Destroy(key))
}
