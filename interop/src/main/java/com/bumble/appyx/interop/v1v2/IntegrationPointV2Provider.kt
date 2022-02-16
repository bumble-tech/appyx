package com.bumble.appyx.interop.v1v2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumble.appyx.v2.core.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPoint

interface IntegrationPointV2Provider {

    val integrationPointV2: IntegrationPoint
}

class IntegrationPointV2ProviderImpl(
    private val activity: AppCompatActivity,
    private val saveInstanceState: Bundle?
) : IntegrationPointV2Provider {

    override val integrationPointV2: IntegrationPoint
        get() = ActivityIntegrationPoint(activity, saveInstanceState)

}
