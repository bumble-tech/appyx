package com.bumble.appyx.interactions

@JsModule("uuid")
@JsNonModule
private external object Uuid {
    fun v4(): String
}

actual object UUID {

    actual fun randomUUID(): String =
        Uuid.v4()

}