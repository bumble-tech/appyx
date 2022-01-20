package com.bumble.appyx.v2.core.integrationpoint.activitystarter

import android.content.Intent

interface ActivityResultHandler {

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}
