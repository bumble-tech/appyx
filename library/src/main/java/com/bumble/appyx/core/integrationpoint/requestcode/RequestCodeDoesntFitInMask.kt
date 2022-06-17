package com.bumble.appyx.core.integrationpoint.requestcode

import java.lang.RuntimeException

class RequestCodeDoesntFitInMask(override val message: String?) : RuntimeException(message)
