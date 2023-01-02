package com.bumble.appyx.navmodel.modal.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.modal.Modal.State
import com.bumble.appyx.core.navigation.Operation

interface ModalOperation<T : Parcelable> : Operation<T, State>
