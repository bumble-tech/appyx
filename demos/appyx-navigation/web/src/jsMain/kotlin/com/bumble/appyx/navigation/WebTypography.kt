package com.bumble.appyx.navigation

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.bumble.appyx.navigation.ui.typography

internal val webTypography = typography.copy(
    bodySmall = typography.bodySmall.copy(
        fontSize = 8.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    bodyMedium = typography.bodyMedium.copy(
        fontSize = 10.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    bodyLarge = typography.bodyLarge.copy(
        fontSize = 12.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    titleSmall = typography.titleSmall.copy(
        fontSize = 8.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    titleMedium = typography.titleMedium.copy(
        fontSize = 10.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    titleLarge = typography.titleLarge.copy(
        fontSize = 12.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    headlineSmall = typography.headlineSmall.copy(
        fontSize = 14.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    headlineMedium = typography.headlineMedium.copy(
        fontSize = 16.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    headlineLarge = typography.headlineLarge.copy(
        fontSize = 18.sp,
        fontFamily = FontFamily.SansSerif,
    ),
)
