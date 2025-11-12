package com.example.fitnessapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

/**
 * Base activity that applies theme colors to all child activities
 * All activities should extend this instead of AppCompatActivity
 */
open class ThemedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.init(this)
    }

    override fun onResume() {
        super.onResume()
        // Reapply theme when activity resumes (in case theme changed)
        applyTheme()
    }

    protected fun applyTheme() {
        val theme = ThemeManager.getCurrentTheme()
        val rootView = findViewById<View>(android.R.id.content)
        applyThemeToView(rootView, theme)
    }

    private fun applyThemeToView(view: View, theme: ThemeManager.AppTheme) {
        when (view) {
            is CardView -> {
                if (view.cardBackgroundColor.defaultColor == 0xFF1A1A1A.toInt()) {
                    view.setCardBackgroundColor(theme.cardColor)
                }
            }
            is AppCompatButton -> {
                // Apply to primary buttons (orange background)
                val drawable = view.background
                if (drawable != null) {
                    drawable.setTint(theme.primaryColor)
                }
            }
            is TextView -> {
                // Apply accent color to specific text views (XP, rewards, etc.)
                if (view.currentTextColor == 0xFFFF6B35.toInt()) {
                    view.setTextColor(theme.primaryColor)
                }
            }
        }

        // Apply to background
        if (view.id == R.id.main) {
            view.setBackgroundColor(theme.backgroundColor)
        }

        // Recursively apply to children
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                applyThemeToView(view.getChildAt(i), theme)
            }
        }
    }

    /**
     * Helper method to get current theme colors
     */
    protected fun getThemeColor(colorType: ColorType): Int {
        val theme = ThemeManager.getCurrentTheme()
        return when (colorType) {
            ColorType.PRIMARY -> theme.primaryColor
            ColorType.ACCENT -> theme.accentColor
            ColorType.BACKGROUND -> theme.backgroundColor
            ColorType.CARD -> theme.cardColor
            ColorType.TEXT -> theme.textColor
        }
    }

    enum class ColorType {
        PRIMARY, ACCENT, BACKGROUND, CARD, TEXT
    }
}