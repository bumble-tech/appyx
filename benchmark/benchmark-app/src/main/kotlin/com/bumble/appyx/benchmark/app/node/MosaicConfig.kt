package com.bumble.appyx.benchmark.app.node

enum class MosaicConfig(
    val columns: Int,
    val rows: Int
) {

    MOSAIC1(25, 12);

    val maxEntryCount: Int = columns * rows
}
