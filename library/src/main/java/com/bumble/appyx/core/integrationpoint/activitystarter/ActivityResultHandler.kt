package com.bumble.appyx.core.integrationpoint.activitystarter

import android.content.Intent

interface ActivityResultHandler {

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}
