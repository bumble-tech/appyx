package com.bumble.appyx.core.integrationpoint.requestcode

import java.lang.RuntimeException

@Deprecated("Use AndroidX API")
class RequestCodeDoesntFitInMask(override val message: String?) : RuntimeException(message)
