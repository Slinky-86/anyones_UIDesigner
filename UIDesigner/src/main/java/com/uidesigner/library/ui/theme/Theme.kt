package com.uidesigner.library.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.uidesigner.library.UIDesignerTheme

// VS Code Dark Theme Colors
private val VSCodeDarkColors = darkColors(
    primary = Color(0xFF007ACC),
    primaryVariant = Color(0xFF005A9E),
    secondary = Color(0xFF569CD6),
    secondaryVariant = Color(0xFF4A8BC2),
    background = Color(0xFF1E1E1E),
    surface = Color(0xFF252526),
    error = Color(0xFFF44747),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFCCCCCC),
    onSurface = Color(0xFFCCCCCC),
    onError = Color.White
)

// VS Code Light Theme Colors
private val VSCodeLightColors = lightColors(
    primary = Color(0xFF005A9E),
    primaryVariant = Color(0xFF003D6B),
    secondary = Color(0xFF0066B8),
    secondaryVariant = Color(0xFF004C87),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF3F3F3),
    error = Color(0xFFE51400),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1E1E1E),
    onSurface = Color(0xFF1E1E1E),
    onError = Color.White
)

// Android Studio Dark Theme Colors
private val AndroidStudioDarkColors = darkColors(
    primary = Color(0xFF4CAF50),
    primaryVariant = Color(0xFF388E3C),
    secondary = Color(0xFF03DAC6),
    secondaryVariant = Color(0xFF018786),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    onError = Color.Black
)

// Android Studio Light Theme Colors
private val AndroidStudioLightColors = lightColors(
    primary = Color(0xFF4CAF50),
    primaryVariant = Color(0xFF388E3C),
    secondary = Color(0xFF018786),
    secondaryVariant = Color(0xFF00695C),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFAFAFA),
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000),
    onError = Color.White
)

@Composable
fun UIDesignerTheme(
    theme: UIDesignerTheme = UIDesignerTheme.VS_CODE_DARK,
    content: @Composable () -> Unit
) {
    val colors = when (theme) {
        UIDesignerTheme.VS_CODE_DARK -> VSCodeDarkColors
        UIDesignerTheme.VS_CODE_LIGHT -> VSCodeLightColors
        UIDesignerTheme.ANDROID_STUDIO_DARK -> AndroidStudioDarkColors
        UIDesignerTheme.ANDROID_STUDIO_LIGHT -> AndroidStudioLightColors
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
