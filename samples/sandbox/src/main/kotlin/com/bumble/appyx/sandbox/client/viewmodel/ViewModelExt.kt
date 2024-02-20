package com.bumble.appyx.sandbox.client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import java.lang.reflect.Method

@Suppress("UNCHECKED_CAST")
fun <T : ViewModel> ViewModelStore.removeViewModel(modelClass: Class<T>) {
    val map = this.getPrivateProperty("mMap") as HashMap<String, ViewModel>
    val iterator = map.iterator()
    while (iterator.hasNext()) {
        val next = iterator.next()
        val viewModel = next.value
        if (viewModel::class.java == modelClass) {
            val clearMethod = findViewModelClearMethod()
            clearMethod.invoke(viewModel)
            iterator.remove()
        }
    }
}

private fun <T : Any> T.getPrivateProperty(variableName: String): Any? {
    return javaClass.getDeclaredField(variableName).let { field ->
        field.isAccessible = true
        return@let field.get(this)
    }
}

private fun findViewModelClearMethod(): Method {
    val clazz: Class<*> = ViewModel::class.java
    val clearMethod = clazz.getDeclaredMethod("clear")
    clearMethod.isAccessible = true
    return clearMethod
}
