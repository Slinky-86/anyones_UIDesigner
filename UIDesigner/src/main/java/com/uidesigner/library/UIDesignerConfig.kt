package com.uidesigner.library

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UIDesignerConfig(
    val theme: UIDesignerTheme = UIDesignerTheme.VS_CODE_DARK,
    val enableXMLGeneration: Boolean = true,
    val enablePreview: Boolean = true,
    val maxUndoSteps: Int = 50,
    val gridSize: Int = 8,
    val snapToGrid: Boolean = true
) : Parcelable

enum class UIDesignerTheme {
    VS_CODE_DARK,
    VS_CODE_LIGHT,
    ANDROID_STUDIO_DARK,
    ANDROID_STUDIO_LIGHT
}
