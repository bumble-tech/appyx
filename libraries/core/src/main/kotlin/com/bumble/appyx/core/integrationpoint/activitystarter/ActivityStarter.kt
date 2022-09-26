package com.bumble.appyx.core.integrationpoint.activitystarter

import android.content.Context
import android.content.Intent
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter.ActivityResultEvent
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeBasedEventStream
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeClient

/**
 * Start Activities.
 *
 * A leaner dependency than passing an entire Activity or Context.
 *
 * @see [com.badoo.ribs.android.requestcode.RequestCodeRegistry] for requirements of The requestcode
 */
interface ActivityStarter : RequestCodeBasedEventStream<ActivityResultEvent> {

    fun startActivity(createIntent: Context.() -> Intent)

    fun startActivityForResult(client: RequestCodeClient, requestCode: Int, createIntent: Context.() -> Intent)

    data class ActivityResultEvent(
        override val requestCode: Int,
        val resultCode: Int,
        val data: Intent?
    ) : RequestCodeBasedEvent
}
