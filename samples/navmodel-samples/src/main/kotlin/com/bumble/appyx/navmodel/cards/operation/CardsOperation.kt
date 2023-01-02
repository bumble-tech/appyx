package com.bumble.appyx.navmodel.cards.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.cards.Cards

interface CardsOperation<T : Parcelable> : Operation<T, Cards.State>
