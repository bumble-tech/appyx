package com.bumble.appyx.dagger.hilt.internal

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.bumble.appyx.dagger.hilt.NodeFactoryProvider
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
internal interface AppyxHiltEntryPoint {
    val nodeFactoryProvider: NodeFactoryProvider
}

internal fun getHiltEntrypoint(context: Context): AppyxHiltEntryPoint {
    val activity = context.let {
        var ctx = it
        while (ctx is ContextWrapper) {
            if (ctx is Activity) {
                return@let ctx
            }
            ctx = ctx.baseContext
        }
        error(
            "Expected an activity context for accessing AppyxNodeFactoryEntryPoint " +
                    "but instead found: $ctx"
        )
    }
    return EntryPoints
        .get(activity, AppyxHiltEntryPoint::class.java)
}
