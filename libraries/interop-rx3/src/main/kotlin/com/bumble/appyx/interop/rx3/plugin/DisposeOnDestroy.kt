package com.bumble.appyx.interop.rx3.plugin

import com.bumble.appyx.core.plugin.Destroyable
import com.bumble.appyx.core.plugin.Plugin
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

private class DisposeOnDestroy(disposables: List<Disposable>) : Destroyable {
    private val disposable = CompositeDisposable(disposables)

    override fun destroy() {
        disposable.dispose()
    }
}

fun disposeOnDestroyPlugin(vararg disposables: Disposable): Plugin =
    DisposeOnDestroy(disposables.toList())
