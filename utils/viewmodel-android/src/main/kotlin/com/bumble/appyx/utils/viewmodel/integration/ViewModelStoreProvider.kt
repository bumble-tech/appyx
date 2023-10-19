package com.bumble.appyx.utils.viewmodel.integration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get

open class ViewModelStoreProvider : ViewModel() {
    private val viewModelStores = mutableMapOf<String, ViewModelStore>()
    fun clear(nodeId: String) {
        val viewModelStore = viewModelStores.remove(nodeId)
        viewModelStore?.clear()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelStores.values.forEach { it.clear() }
        viewModelStores.clear()
    }

    fun getViewModelStoreForNode(nodeId: String): ViewModelStore =
        viewModelStores.getOrPut(nodeId) { ViewModelStore() }

    companion object {
        fun getInstance(viewModelStoreOwner: ViewModelStoreOwner): ViewModelStoreProvider {
            return ViewModelProvider(viewModelStoreOwner).get()
        }
    }
}
