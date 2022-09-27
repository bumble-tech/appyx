package com.bumble.appyx.core.integrationpoint.activitystarter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter.ActivityResultEvent
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeBasedEventStreamImpl
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeClient
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeRegistry

class ActivityBoundary(
    private val activityStarterHost: ActivityStarterHost,
    requestCodeRegistry: RequestCodeRegistry
) : RequestCodeBasedEventStreamImpl<ActivityResultEvent>(requestCodeRegistry),
    ActivityStarter,
    ActivityResultHandler {

    constructor(
        activity: Activity,
        requestCodeRegistry: RequestCodeRegistry
    ) : this(
        ActivityStarterHost.ActivityHost(activity),
        requestCodeRegistry
    )

    constructor(
        fragment: Fragment,
        requestCodeRegistry: RequestCodeRegistry
    ) : this(
        ActivityStarterHost.FragmentHost(fragment),
        requestCodeRegistry
    )

    override fun startActivity(createIntent: Context.() -> Intent) {
        activityStarterHost.startActivity(activityStarterHost.context.createIntent())
    }

    override fun startActivityForResult(
        client: RequestCodeClient,
        requestCode: Int,
        createIntent: Context.() -> Intent
    ) {
        activityStarterHost.startActivityForResult(
            activityStarterHost.context.createIntent(),
            client.forgeExternalRequestCode(requestCode)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        publishSafely(
            requestCode,
            ActivityResultEvent(
                requestCode = requestCode.toInternalRequestCode(),
                resultCode = resultCode,
                data = data
            )
        )
    }
}
