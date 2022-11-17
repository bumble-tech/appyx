package com.bumble.appyx.interop.rx2.plugin

import com.bumble.appyx.core.plugin.Destroyable
import com.bumble.appyx.core.plugin.Plugin
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

private class DisposeOnDestroy(disposables: List<Disposable>) : Destroyable {
    private val disposable = CompositeDisposable(disposables)

    override fun destroy() {
        disposable.dispose()
    }
}

fun disposeOnDestroyPlugin(vararg disposables: Disposable): Plugin =
    DisposeOnDestroy(disposables.toList())
