package com.bumble.appyx.interactions

import kotlin.random.Random

actual object UUID {

    actual fun randomUUID(): String = Random.nextInt().toString()

}
