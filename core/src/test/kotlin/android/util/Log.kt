package android.util

/** Provides class to use without Robolectric. */
object Log {
    @JvmStatic
    fun d(tag: String, msg: String): Int {
        println("DEBUG: $tag: $msg")
        return 0
    }
}
