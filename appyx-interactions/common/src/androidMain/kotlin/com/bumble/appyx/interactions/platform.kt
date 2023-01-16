package com.bumble.appyx.interactions

actual fun getPlatformName(): String {
    return "Android"
}

actual typealias Parcelize = kotlinx.parcelize.Parcelize

actual typealias Parcelable = android.os.Parcelable

actual typealias RawValue = kotlinx.parcelize.RawValue
