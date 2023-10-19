package com.bumble.appyx.utils.viewmodel.node

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.lifecycle.viewmodel.CreationExtras

@MainThread
inline fun <reified VM : ViewModel> ViewModelStoreOwner.viewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        ViewModelProvider.NewInstanceFactory.instance
    }

    return ViewModelLazy(
        VM::class,
        { viewModelStore },
        factoryPromise,
        { extrasProducer?.invoke() ?: CreationExtras.Empty }
    )
}

@MainThread
inline fun <reified VM : ViewModel> ViewModelStoreOwner.viewModel(factory: ViewModelProvider.Factory): VM {
    return ViewModelProvider(this, factory).get()
}
