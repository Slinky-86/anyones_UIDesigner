package com.uidesigner.library.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UIComponent(
    val id: String,
    val type: ComponentType,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val properties: Map<String, String> = emptyMap(),
    val constraints: ConstraintSet = ConstraintSet(),
    val parentId: String? = null,
    val children: List<String> = emptyList(),
    val zIndex: Int = 0,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val rotation: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val alpha: Float = 1f,
    val elevation: Float = 0f,
    val margins: Margins = Margins(),
    val padding: Padding = Padding(),
    val background: BackgroundStyle = BackgroundStyle(),
    val textStyle: TextStyle? = null,
    val animations: List<Animation> = emptyList()
) : Parcelable

@Parcelize
@Serializable
data class ConstraintSet(
    val startToStart: String? = null,
    val startToEnd: String? = null,
    val endToStart: String? = null,
    val endToEnd: String? = null,
    val topToTop: String? = null,
    val topToBottom: String? = null,
    val bottomToTop: String? = null,
    val bottomToBottom: String? = null,
    val baselineToBaseline: String? = null,
    val centerHorizontally: String? = null,
    val centerVertically: String? = null,
    val horizontalBias: Float = 0.5f,
    val verticalBias: Float = 0.5f,
    val dimensionRatio: String? = null,
    val chainStyle: ChainStyle = ChainStyle.SPREAD,
    val goneMarginStart: Float = 0f,
    val goneMarginTop: Float = 0f,
    val goneMarginEnd: Float = 0f,
    val goneMarginBottom: Float = 0f
) : Parcelable

@Parcelize
@Serializable
data class Margins(
    val start: Float = 0f,
    val top: Float = 0f,
    val end: Float = 0f,
    val bottom: Float = 0f
) : Parcelable

@Parcelize
@Serializable
data class Padding(
    val start: Float = 0f,
    val top: Float = 0f,
    val end: Float = 0f,
    val bottom: Float = 0f
) : Parcelable

@Parcelize
@Serializable
data class BackgroundStyle(
    val color: String = "#FFFFFF",
    val drawable: String? = null,
    val cornerRadius: Float = 0f,
    val strokeWidth: Float = 0f,
    val strokeColor: String = "#000000",
    val gradient: GradientStyle? = null
) : Parcelable

@Parcelize
@Serializable
data class GradientStyle(
    val type: GradientType = GradientType.LINEAR,
    val startColor: String = "#FFFFFF",
    val endColor: String = "#000000",
    val centerColor: String? = null,
    val angle: Float = 0f,
    val centerX: Float = 0.5f,
    val centerY: Float = 0.5f,
    val radius: Float = 0.5f
) : Parcelable

@Parcelize
@Serializable
data class TextStyle(
    val textSize: Float = 14f,
    val textColor: String = "#000000",
    val fontFamily: String = "default",
    val fontWeight: FontWeight = FontWeight.NORMAL,
    val fontStyle: FontStyle = FontStyle.NORMAL,
    val textAlignment: TextAlignment = TextAlignment.START,
    val lineHeight: Float = 1.2f,
    val letterSpacing: Float = 0f,
    val textDecoration: TextDecoration = TextDecoration.NONE,
    val maxLines: Int = Int.MAX_VALUE,
    val ellipsize: TextEllipsize = TextEllipsize.NONE
) : Parcelable

@Parcelize
@Serializable
data class Animation(
    val type: AnimationType,
    val duration: Long = 300L,
    val delay: Long = 0L,
    val interpolator: AnimationInterpolator = AnimationInterpolator.EASE_IN_OUT,
    val repeatCount: Int = 0,
    val repeatMode: RepeatMode = RepeatMode.RESTART,
    val properties: Map<String, String> = emptyMap()
) : Parcelable

enum class ComponentType(val displayName: String, val xmlTag: String, val category: ComponentCategory) {
    // Basic Views
    TEXT_VIEW("TextView", "TextView", ComponentCategory.BASIC),
    BUTTON("Button", "Button", ComponentCategory.BASIC),
    IMAGE_VIEW("ImageView", "ImageView", ComponentCategory.BASIC),
    EDIT_TEXT("EditText", "EditText", ComponentCategory.INPUT),
    
    // Input Controls
    CHECKBOX("CheckBox", "CheckBox", ComponentCategory.INPUT),
    RADIO_BUTTON("RadioButton", "RadioButton", ComponentCategory.INPUT),
    SWITCH("Switch", "Switch", ComponentCategory.INPUT),
    SEEK_BAR("SeekBar", "SeekBar", ComponentCategory.INPUT),
    RATING_BAR("RatingBar", "RatingBar", ComponentCategory.INPUT),
    SPINNER("Spinner", "Spinner", ComponentCategory.INPUT),
    
    // Layouts
    LINEAR_LAYOUT("LinearLayout", "LinearLayout", ComponentCategory.LAYOUT),
    RELATIVE_LAYOUT("RelativeLayout", "RelativeLayout", ComponentCategory.LAYOUT),
    CONSTRAINT_LAYOUT("ConstraintLayout", "androidx.constraintlayout.widget.ConstraintLayout", ComponentCategory.LAYOUT),
    FRAME_LAYOUT("FrameLayout", "FrameLayout", ComponentCategory.LAYOUT),
    TABLE_LAYOUT("TableLayout", "TableLayout", ComponentCategory.LAYOUT),
    GRID_LAYOUT("GridLayout", "GridLayout", ComponentCategory.LAYOUT),
    COORDINATOR_LAYOUT("CoordinatorLayout", "androidx.coordinatorlayout.widget.CoordinatorLayout", ComponentCategory.LAYOUT),
    
    // Containers
    SCROLL_VIEW("ScrollView", "ScrollView", ComponentCategory.CONTAINER),
    HORIZONTAL_SCROLL_VIEW("HorizontalScrollView", "HorizontalScrollView", ComponentCategory.CONTAINER),
    NESTED_SCROLL_VIEW("NestedScrollView", "androidx.core.widget.NestedScrollView", ComponentCategory.CONTAINER),
    CARD_VIEW("CardView", "androidx.cardview.widget.CardView", ComponentCategory.CONTAINER),
    
    // Lists & Collections
    RECYCLER_VIEW("RecyclerView", "androidx.recyclerview.widget.RecyclerView", ComponentCategory.COLLECTION),
    LIST_VIEW("ListView", "ListView", ComponentCategory.COLLECTION),
    GRID_VIEW("GridView", "GridView", ComponentCategory.COLLECTION),
    VIEW_PAGER("ViewPager2", "androidx.viewpager2.widget.ViewPager2", ComponentCategory.COLLECTION),
    
    // Navigation
    TAB_LAYOUT("TabLayout", "com.google.android.material.tabs.TabLayout", ComponentCategory.NAVIGATION),
    BOTTOM_NAVIGATION("BottomNavigationView", "com.google.android.material.bottomnavigation.BottomNavigationView", ComponentCategory.NAVIGATION),
    NAVIGATION_DRAWER("NavigationView", "com.google.android.material.navigation.NavigationView", ComponentCategory.NAVIGATION),
    
    // Material Components
    FLOATING_ACTION_BUTTON("FloatingActionButton", "com.google.android.material.floatingactionbutton.FloatingActionButton", ComponentCategory.MATERIAL),
    APP_BAR_LAYOUT("AppBarLayout", "com.google.android.material.appbar.AppBarLayout", ComponentCategory.MATERIAL),
    TOOLBAR("Toolbar", "androidx.appcompat.widget.Toolbar", ComponentCategory.MATERIAL),
    COLLAPSING_TOOLBAR("CollapsingToolbarLayout", "com.google.android.material.appbar.CollapsingToolbarLayout", ComponentCategory.MATERIAL),
    
    // Advanced
    WEB_VIEW("WebView", "WebView", ComponentCategory.ADVANCED),
    MAP_VIEW("MapView", "com.google.android.gms.maps.MapView", ComponentCategory.ADVANCED),
    VIDEO_VIEW("VideoView", "VideoView", ComponentCategory.ADVANCED),
    PROGRESS_BAR("ProgressBar", "ProgressBar", ComponentCategory.ADVANCED),
    
    // Custom
    CUSTOM_VIEW("CustomView", "View", ComponentCategory.CUSTOM)
}

enum class ComponentCategory(val displayName: String) {
    BASIC("Basic"),
    INPUT("Input Controls"),
    LAYOUT("Layouts"),
    CONTAINER("Containers"),
    COLLECTION("Lists & Collections"),
    NAVIGATION("Navigation"),
    MATERIAL("Material Design"),
    ADVANCED("Advanced"),
    CUSTOM("Custom")
}

enum class ChainStyle { SPREAD, SPREAD_INSIDE, PACKED }
enum class GradientType { LINEAR, RADIAL, SWEEP }
enum class FontWeight { THIN, LIGHT, NORMAL, MEDIUM, BOLD, BLACK }
enum class FontStyle { NORMAL, ITALIC }
enum class TextAlignment { START, CENTER, END, JUSTIFY }
enum class TextDecoration { NONE, UNDERLINE, STRIKETHROUGH }
enum class TextEllipsize { NONE, START, MIDDLE, END, MARQUEE }
enum class AnimationType { FADE, SLIDE, SCALE, ROTATE, TRANSLATE }
enum class AnimationInterpolator { LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT, BOUNCE, OVERSHOOT }
enum class RepeatMode { RESTART, REVERSE }
