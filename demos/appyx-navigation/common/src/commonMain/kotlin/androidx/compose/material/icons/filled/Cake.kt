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

public val Icons.Filled.Cake: ImageVector
    get() {
        if (_cake != null) {
            return _cake!!
        }
        _cake = materialIcon(name = "Filled.Cake") {
            materialPath {
                moveTo(12.0f, 6.0f)
                curveToRelative(1.11f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                curveToRelative(0.0f, -0.38f, -0.1f, -0.73f, -0.29f, -1.03f)
                lineTo(12.0f, 0.0f)
                lineToRelative(-1.71f, 2.97f)
                curveToRelative(-0.19f, 0.3f, -0.29f, 0.65f, -0.29f, 1.03f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                close()
                moveTo(16.6f, 15.99f)
                lineToRelative(-1.07f, -1.07f)
                lineToRelative(-1.08f, 1.07f)
                curveToRelative(-1.3f, 1.3f, -3.58f, 1.31f, -4.89f, 0.0f)
                lineToRelative(-1.07f, -1.07f)
                lineToRelative(-1.09f, 1.07f)
                curveTo(6.75f, 16.64f, 5.88f, 17.0f, 4.96f, 17.0f)
                curveToRelative(-0.73f, 0.0f, -1.4f, -0.23f, -1.96f, -0.61f)
                lineTo(3.0f, 21.0f)
                curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
                horizontalLineToRelative(16.0f)
                curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
                verticalLineToRelative(-4.61f)
                curveToRelative(-0.56f, 0.38f, -1.23f, 0.61f, -1.96f, 0.61f)
                curveToRelative(-0.92f, 0.0f, -1.79f, -0.36f, -2.44f, -1.01f)
                close()
                moveTo(18.0f, 9.0f)
                horizontalLineToRelative(-5.0f)
                lineTo(13.0f, 7.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                lineTo(6.0f, 9.0f)
                curveToRelative(-1.66f, 0.0f, -3.0f, 1.34f, -3.0f, 3.0f)
                verticalLineToRelative(1.54f)
                curveToRelative(0.0f, 1.08f, 0.88f, 1.96f, 1.96f, 1.96f)
                curveToRelative(0.52f, 0.0f, 1.02f, -0.2f, 1.38f, -0.57f)
                lineToRelative(2.14f, -2.13f)
                lineToRelative(2.13f, 2.13f)
                curveToRelative(0.74f, 0.74f, 2.03f, 0.74f, 2.77f, 0.0f)
                lineToRelative(2.14f, -2.13f)
                lineToRelative(2.13f, 2.13f)
                curveToRelative(0.37f, 0.37f, 0.86f, 0.57f, 1.38f, 0.57f)
                curveToRelative(1.08f, 0.0f, 1.96f, -0.88f, 1.96f, -1.96f)
                lineTo(20.99f, 12.0f)
                curveTo(21.0f, 10.34f, 19.66f, 9.0f, 18.0f, 9.0f)
                close()
            }
        }
        return _cake!!
    }

private var _cake: ImageVector? = null
