package com.github.zsoltk.composeribs.core.integrationpoint.requestcode

import java.lang.RuntimeException

class RequestCodeDoesntFitInMask(override val message: String?) : RuntimeException(message)
