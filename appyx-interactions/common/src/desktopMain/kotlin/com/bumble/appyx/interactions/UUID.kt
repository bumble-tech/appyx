package com.bumble.appyx.interactions

import java.util.UUID as JvmUUID

actual object UUID {

    actual fun randomUUID(): String =
        JvmUUID.randomUUID().toString()

}