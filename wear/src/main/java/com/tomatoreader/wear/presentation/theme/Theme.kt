package com.tomatoreader.wear.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Typography

// Material Design 3 颜色调色板
private val DarkColorPalette = Colors(
    primary = androidx.compose.ui.graphics.Color(0xFF6750A4),
    primaryVariant = androidx.compose.ui.graphics.Color(0xFF4F378B),
    secondary = androidx.compose.ui.graphics.Color(0xFF625B71),
    secondaryVariant = androidx.compose.ui.graphics.Color(0xFF4A4458),
    tertiary = androidx.compose.ui.graphics.Color(0xFF7D5260),
    error = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onError = androidx.compose.ui.graphics.Color.White,
    surface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    background = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE6E1E5)
)

private val LightColorPalette = Colors(
    primary = androidx.compose.ui.graphics.Color(0xFF6750A4),
    primaryVariant = androidx.compose.ui.graphics.Color(0xFF4F378B),
    secondary = androidx.compose.ui.graphics.Color(0xFF625B71),
    secondaryVariant = androidx.compose.ui.graphics.Color(0xFF4A4458),
    tertiary = androidx.compose.ui.graphics.Color(0xFF7D5260),
    error = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onError = androidx.compose.ui.graphics.Color.White,
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F)
)

// Material Design 3 字体系统
private val WearTypography = Typography(
    // 大标题
    display1 = androidx.wear.compose.material.Typography.default.display1,
    display2 = androidx.wear.compose.material.Typography.default.display2,
    display3 = androidx.wear.compose.material.Typography.default.display3,
    
    // 标题
    title1 = androidx.wear.compose.material.Typography.default.title1,
    title2 = androidx.wear.compose.material.Typography.default.title2,
    title3 = androidx.wear.compose.material.Typography.default.title3,
    
    // 正文
    body1 = androidx.wear.compose.material.Typography.default.body1,
    body2 = androidx.wear.compose.material.Typography.default.body2,
    
    // 说明文字
    caption1 = androidx.wear.compose.material.Typography.default.caption1,
    caption2 = androidx.wear.compose.material.Typography.default.caption2,
    
    // 按钮
    button = androidx.wear.compose.material.Typography.default.button
)

@Composable
fun TomatoReaderWearTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = WearTypography,
        content = content
    )
}