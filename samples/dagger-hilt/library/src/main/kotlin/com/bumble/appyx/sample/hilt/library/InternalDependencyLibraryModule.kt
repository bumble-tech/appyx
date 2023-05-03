package com.bumble.appyx.sample.hilt.library

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
internal object InternalDependencyLibraryModule {
    @Provides
    fun provideInternalDependency(activity: Activity): InternalDependency =
        InternalDependency(activity)
}
