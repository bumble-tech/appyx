package com.bumble.appyx.demos

import kotlinx.browser.document
import org.w3c.dom.get

private val LOADER_STYLES = """
    .loader {
        width: 48px;
        height: 48px;
        position: fixed;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        border: 5px solid rgba(255, 227, 0, 255);
        border-bottom-color: transparent;
        border-radius: 50%;
        animation: rotation 1s linear 1s infinite;
        visibility: hidden;
        margin: auto;
    }
    
    @keyframes rotation {
        0% {
            visibility: visible;
            transform: rotate(0deg);
        }
        100% {
            transform: rotate(360deg);
        }
    }
""".trimIndent()

external fun onWasmReady(onReady: () -> Unit)

fun appyxSample(
    block: () -> Unit,
) {
    appendLoaderStyles()
    appendLoaderElement()
    onWasmReady {
        block()
        removeLoaderElement()
    }
}

private fun appendLoaderStyles() {
    val head = document.head ?: document.getElementsByTagName("head")[0]
    head?.apply {
        val style = document.createElement("style")
        head.appendChild(style)
        style.appendChild(document.createTextNode(LOADER_STYLES))
    }
}

private fun appendLoaderElement() {
    val composeTarget = document.getElementById("ComposeTarget")
    val loader = document.createElement("div")
    loader.className = "loader"
    composeTarget?.parentNode?.appendChild(loader)
}

private fun removeLoaderElement() {
    val loader = document.getElementsByClassName("loader")[0]
    loader?.parentNode?.removeChild(loader)
}

