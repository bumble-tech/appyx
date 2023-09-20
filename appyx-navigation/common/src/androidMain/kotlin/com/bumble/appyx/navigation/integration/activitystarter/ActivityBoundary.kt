package com.bumble.appyx.navigation.integration.activitystarter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.bumble.appyx.navigation.integration.activitystarter.ActivityStarter.ActivityResultEvent
import com.bumble.appyx.navigation.integration.requestcode.RequestCodeBasedEventStreamImpl
import com.bumble.appyx.navigation.integration.requestcode.RequestCodeClient
import com.bumble.appyx.navigation.integration.requestcode.RequestCodeRegistry

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
        publish(
            requestCode,
            ActivityResultEvent(
                requestCode = requestCode.toInternalRequestCode(),
                resultCode = resultCode,
                data = data
            )
        )
    }
}
