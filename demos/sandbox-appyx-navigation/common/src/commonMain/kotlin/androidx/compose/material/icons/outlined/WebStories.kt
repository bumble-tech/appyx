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

package androidx.compose.material.icons.outlined

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Outlined.WebStories: ImageVector
    get() {
        if (_webStories != null) {
            return _webStories!!
        }
        _webStories = materialIcon(name = "Outlined.WebStories") {
            materialPath {
                moveTo(17.0f, 4.0f)
                verticalLineToRelative(16.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(6.0f)
                curveTo(19.0f, 4.9f, 18.1f, 4.0f, 17.0f, 4.0f)
                close()
            }
            materialPath {
                moveTo(13.0f, 2.0f)
                horizontalLineTo(4.0f)
                curveTo(2.9f, 2.0f, 2.0f, 2.9f, 2.0f, 4.0f)
                verticalLineToRelative(16.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(9.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(4.0f)
                curveTo(15.0f, 2.9f, 14.1f, 2.0f, 13.0f, 2.0f)
                close()
                moveTo(13.0f, 20.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(4.0f)
                horizontalLineToRelative(9.0f)
                verticalLineTo(20.0f)
                close()
            }
            materialPath {
                moveTo(21.0f, 6.0f)
                verticalLineToRelative(12.0f)
                curveToRelative(0.83f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
                verticalLineToRelative(-9.0f)
                curveTo(22.5f, 6.67f, 21.83f, 6.0f, 21.0f, 6.0f)
                close()
            }
        }
        return _webStories!!
    }

private var _webStories: ImageVector? = null
