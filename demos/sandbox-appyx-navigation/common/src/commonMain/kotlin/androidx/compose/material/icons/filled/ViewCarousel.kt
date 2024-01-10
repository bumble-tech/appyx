/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.material.icons.filled

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.Filled.ViewCarousel: ImageVector
    get() {
        if (_viewCarousel != null) {
            return _viewCarousel!!
        }
        _viewCarousel = materialIcon(name = "Filled.ViewCarousel") {
            materialPath {
                moveTo(2.0f, 7.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(10.0f)
                horizontalLineTo(2.0f)
                verticalLineTo(7.0f)
                close()
                moveTo(7.0f, 19.0f)
                horizontalLineToRelative(10.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(19.0f)
                close()
                moveTo(18.0f, 7.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(10.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineTo(7.0f)
                close()
            }
        }
        return _viewCarousel!!
    }

private var _viewCarousel: ImageVector? = null
