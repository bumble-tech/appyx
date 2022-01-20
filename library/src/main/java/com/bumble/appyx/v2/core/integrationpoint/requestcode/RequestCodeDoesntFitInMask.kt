package com.bumble.appyx.v2.core.integrationpoint.requestcode

import java.lang.RuntimeException

class RequestCodeDoesntFitInMask(override val message: String?) : RuntimeException(message)
