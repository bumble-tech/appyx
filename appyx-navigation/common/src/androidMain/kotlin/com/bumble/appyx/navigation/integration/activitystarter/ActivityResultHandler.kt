package com.bumble.appyx.navigation.integration.activitystarter

import android.content.Intent

interface ActivityResultHandler {

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}
