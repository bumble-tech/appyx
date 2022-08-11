package com.bumble.appyx.sandbox.client.interop.child

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams

class RibsChildBuilder : SimpleBuilder<RibsChild>() {

    override fun build(buildParams: BuildParams<Nothing?>): RibsChildNode =
        RibsChildNode(buildParams = buildParams)
}
