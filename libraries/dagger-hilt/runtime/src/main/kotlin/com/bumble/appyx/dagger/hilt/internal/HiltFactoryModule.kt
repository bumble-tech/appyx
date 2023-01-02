package com.bumble.appyx.dagger.hilt.internal

import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.dagger.hilt.NodeFactoryProvider
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Provider

@Module
@InstallIn(ActivityComponent::class)
internal object HiltFactoryModule {
    @Provides
    fun provideNodeFactoryProvider(
        hiltFactoriesProviderMap: Lazy<Map<Class<*>, Provider<AggregateNodeFactoryBuilder<*>>>>,
        nodeFactoryMap: Lazy<Map<Class<*>, Provider<NodeFactory<*>>>>,
        @CustomNodeFactoryQualifier customNodeFactoryMap: Lazy<Map<Class<*>, Provider<@JvmSuppressWildcards Any>>>
    ): NodeFactoryProvider =
        HiltNodeFactoryProvider(hiltFactoriesProviderMap, nodeFactoryMap, customNodeFactoryMap)
}
