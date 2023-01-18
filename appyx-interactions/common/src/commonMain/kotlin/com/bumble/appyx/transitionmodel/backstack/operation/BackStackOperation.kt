package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.backstack.BackStackModel

interface BackStackOperation<T> : Operation<T, BackStackModel.State>
