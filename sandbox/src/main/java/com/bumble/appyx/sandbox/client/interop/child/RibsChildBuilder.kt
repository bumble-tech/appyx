package com.bumble.appyx.sandbox.client.interop.child

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildParams

class RibsChildBuilder : SimpleBuilder<Rib>() {

    override fun build(buildParams: BuildParams<Nothing?>): Rib =
        RibsChildNode(buildParams = buildParams)
}
