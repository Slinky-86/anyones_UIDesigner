package com.uidesigner.library.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.uidesigner.library.R
import com.uidesigner.library.UIDesignerConfig
import com.uidesigner.library.UIDesignerTheme

/**
 * SplashActivity - Professional splash screen for UI Designer Module
 * 
 * Features:
 * - Animated logo with drawable resources
 * - Professional branding with "anyones-UIDesigner"
 * - Smooth transitions with component icons
 * - Theme-aware styling
 * - Auto-navigation to main designer
 */
class SplashActivity : ComponentActivity() {
    
    private lateinit var logoImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var subtitleTextView: TextView
    private lateinit var componentIconsContainer: View
    
    private val splashDuration = 2500L
    private val animationDuration = 1500L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get configuration from intent or use default
        val config = intent.getParcelableExtra<UIDesignerConfig>("config") 
            ?: UIDesignerConfig()
        
        // Set theme-appropriate status bar
        when (config.theme) {
            UIDesignerTheme.VS_CODE_DARK -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.vs_code_dark_bg)
            }
            UIDesignerTheme.VS_CODE_LIGHT -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.vs_code_light_bg)
            }
            else -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.splash_background)
            }
        }
        
        setContentView(R.layout.activity_splash)
        
        initializeViews()
        setupThemeBasedStyling(config.theme)
        startAnimations()
        
        // Navigate to main designer after splash duration
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMainDesigner(config)
        }, splashDuration)
    }
    
    private fun initializeViews() {
        logoImageView = findViewById(R.id.splash_logo)
        titleTextView = findViewById(R.id.splash_title)
        subtitleTextView = findViewById(R.id.splash_subtitle)
        componentIconsContainer = findViewById(R.id.component_icons_container)
        
        // Set initial alpha for animation
        logoImageView.alpha = 0f
        titleTextView.alpha = 0f
        subtitleTextView.alpha = 0f
        componentIconsContainer.alpha = 0f
        
        // Set logo drawable (using a component icon as logo)
        logoImageView.setImageResource(R.drawable.ic_constraint_layout)
        logoImageView.setColorFilter(ContextCompat.getColor(this, R.color.designer_primary))
    }
    
    private fun setupThemeBasedStyling(theme: UIDesignerTheme) {
        val backgroundColor = when (theme) {
            UIDesignerTheme.VS_CODE_DARK -> R.color.vs_code_dark_bg
            UIDesignerTheme.VS_CODE_LIGHT -> R.color.vs_code_light_bg
            else -> R.color.splash_background
        }
        
        val textColor = when (theme) {
            UIDesignerTheme.VS_CODE_DARK -> R.color.vs_code_light_text
            UIDesignerTheme.VS_CODE_LIGHT -> R.color.vs_code_dark_text
            else -> R.color.splash_text
        }
        
        val accentColor = when (theme) {
            UIDesignerTheme.VS_CODE_DARK -> R.color.vs_code_accent_text
            UIDesignerTheme.VS_CODE_LIGHT -> R.color.vs_code_blue
            else -> R.color.splash_accent
        }
        
        // Apply theme colors
        findViewById<View>(R.id.splash_root).setBackgroundColor(
            ContextCompat.getColor(this, backgroundColor)
        )
        
        titleTextView.setTextColor(ContextCompat.getColor(this, textColor))
        subtitleTextView.setTextColor(ContextCompat.getColor(this, accentColor))
        
        // Setup component icons with theme-appropriate colors
        setupComponentIcons(theme)
    }
    
    private fun setupComponentIcons(theme: UIDesignerTheme) {
        val iconContainer = componentIconsContainer as androidx.constraintlayout.widget.ConstraintLayout
        
        // Create sample component icons to showcase the library
        val componentIcons = listOf(
            R.drawable.ic_textview to R.color.component_basic,
            R.drawable.ic_button to R.color.component_basic,
            R.drawable.ic_edittext to R.color.component_input,
            R.drawable.ic_linear_layout to R.color.component_layout,
            R.drawable.ic_recycler_view to R.color.component_collection,
            R.drawable.ic_floating_action_button to R.color.component_material
        )
        
        componentIcons.forEachIndexed { index, (iconRes, colorRes) ->
            val iconView = ImageView(this).apply {
                setImageResource(iconRes)
                setColorFilter(ContextCompat.getColor(this@SplashActivity, colorRes))
                alpha = 0f
                scaleX = 0.5f
                scaleY = 0.5f
            }
            
            val layoutParams = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.icon_size_large),
                resources.getDimensionPixelSize(R.dimen.icon_size_large)
            )
            
            // Position icons in a circle around the center
            val angle = (index * 60f) * Math.PI / 180f
            val radius = resources.getDimensionPixelSize(R.dimen.splash_icon_radius)
            val centerX = radius * Math.cos(angle).toFloat()
            val centerY = radius * Math.sin(angle).toFloat()
            
            layoutParams.leftMargin = centerX.toInt()
            layoutParams.topMargin = centerY.toInt()
            layoutParams.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            
            iconView.layoutParams = layoutParams
            iconContainer.addView(iconView)
            
            // Animate icon appearance with delay
            Handler(Looper.getMainLooper()).postDelayed({
                animateIconAppearance(iconView)
            }, 500L + (index * 100L))
        }
    }
    
    private fun startAnimations() {
        // Logo animation
        val logoFadeIn = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f)
        val logoScaleX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.5f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.5f, 1f)
        
        val logoAnimatorSet = AnimatorSet().apply {
            playTogether(logoFadeIn, logoScaleX, logoScaleY)
            duration = animationDuration / 2
            interpolator = AccelerateDecelerateInterpolator()
        }
        
        // Title animation
        val titleFadeIn = ObjectAnimator.ofFloat(titleTextView, "alpha", 0f, 1f)
        val titleTranslateY = ObjectAnimator.ofFloat(titleTextView, "translationY", 50f, 0f)
        
        val titleAnimatorSet = AnimatorSet().apply {
            playTogether(titleFadeIn, titleTranslateY)
            duration = animationDuration / 2
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 300L
        }
        
        // Subtitle animation
        val subtitleFadeIn = ObjectAnimator.ofFloat(subtitleTextView, "alpha", 0f, 1f)
        val subtitleTranslateY = ObjectAnimator.ofFloat(subtitleTextView, "translationY", 30f, 0f)
        
        val subtitleAnimatorSet = AnimatorSet().apply {
            playTogether(subtitleFadeIn, subtitleTranslateY)
            duration = animationDuration / 2
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 600L
        }
        
        // Component icons container animation
        val iconsFadeIn = ObjectAnimator.ofFloat(componentIconsContainer, "alpha", 0f, 1f)
        val iconsScale = ObjectAnimator.ofFloat(componentIconsContainer, "scaleX", 0.8f, 1f)
        val iconsScaleY = ObjectAnimator.ofFloat(componentIconsContainer, "scaleY", 0.8f, 1f)
        
        val iconsAnimatorSet = AnimatorSet().apply {
            playTogether(iconsFadeIn, iconsScale, iconsScaleY)
            duration = animationDuration / 2
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 400L
        }
        
        // Start all animations
        logoAnimatorSet.start()
        titleAnimatorSet.start()
        subtitleAnimatorSet.start()
        iconsAnimatorSet.start()
    }
    
    private fun animateIconAppearance(iconView: ImageView) {
        val fadeIn = ObjectAnimator.ofFloat(iconView, "alpha", 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(iconView, "scaleX", 0.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(iconView, "scaleY", 0.5f, 1f)
        val rotation = ObjectAnimator.ofFloat(iconView, "rotation", -180f, 0f)
        
        AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY, rotation)
            duration = 600L
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }
    
    private fun navigateToMainDesigner(config: UIDesignerConfig) {
        val intent = Intent(this, UIDesignerActivity::class.java).apply {
            putExtra("config", config)
            // Pass any additional data from splash
            putExtra("splash_completed", true)
            putExtra("animation_duration", animationDuration)
        }
        
        startActivity(intent)
        
        // Custom transition animation
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        
        finish()
    }
    
    override fun onBackPressed() {
        // Disable back button during splash
        // User should wait for automatic navigation
    }
}
