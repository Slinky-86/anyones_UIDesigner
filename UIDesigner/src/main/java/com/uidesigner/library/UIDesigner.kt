package com.uidesigner.library

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Main entry point for the UIDesigner library
 */
object UIDesigner {
    
    /**
     * Creates a default configuration for the UI Designer
     */
    fun createDefaultConfig(): UIDesignerConfig {
        return UIDesignerConfig()
    }
    
    /**
     * Creates a configuration with custom settings
     */
    fun createConfig(
        theme: UIDesignerTheme = UIDesignerTheme.VS_CODE_DARK,
        showGrid: Boolean = true,
        snapToGrid: Boolean = true,
        gridSize: Int = 20,
        maxUndoSteps: Int = 50
    ): UIDesignerConfig {
        return UIDesignerConfig(
            theme = theme,
            showGrid = showGrid,
            snapToGrid = snapToGrid,
            gridSize = gridSize,
            maxUndoSteps = maxUndoSteps
        )
    }
}

/**
 * Configuration class for the UI Designer
 */
@Parcelize
data class UIDesignerConfig(
    val theme: UIDesignerTheme = UIDesignerTheme.VS_CODE_DARK,
    val showGrid: Boolean = true,
    val snapToGrid: Boolean = true,
    val gridSize: Int = 20,
    val maxUndoSteps: Int = 50
) : Parcelable

/**
 * Available themes for the UI Designer
 */
enum class UIDesignerTheme {
    VS_CODE_DARK,
    VS_CODE_LIGHT,
    ANDROID_STUDIO_DARK,
    ANDROID_STUDIO_LIGHT
}
