package com.bumble.appyx.interactions

import platform.Foundation.NSUUID

actual object UUID {

    actual fun randomUUID(): String = NSUUID().UUIDString()

}
