package com.example.fitnessapp

import android.content.Context
import android.content.SharedPreferences

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_CURRENT_THEME = "current_theme"

    // Theme definitions
    enum class AppTheme(
        val id: String,
        val displayName: String,
        val primaryColor: Int,
        val accentColor: Int,
        val backgroundColor: Int,
        val cardColor: Int,
        val textColor: Int
    ) {
        DEFAULT(
            "default",
            "Default",
            0xFFFF6B35.toInt(), // Orange
            0xFFFF8C5A.toInt(), // Light Orange
            0xFF0D0D0D.toInt(), // Dark background
            0xFF1A1A1A.toInt(), // Card background
            0xFFFFFFFF.toInt()  // White text
        ),
        GOLDEN(
            "golden",
            "Golden",
            0xFFFFD700.toInt(), // Gold
            0xFFFFE55C.toInt(), // Light Gold
            0xFF1A1410.toInt(), // Dark gold tint
            0xFF2A2418.toInt(), // Card with gold tint
            0xFFFFFFFF.toInt()
        ),
        PURPLE(
            "purple",
            "Dark Purple",
            0xFF9C27B0.toInt(), // Purple
            0xFFBA68C8.toInt(), // Light Purple
            0xFF0D0A0F.toInt(), // Dark purple tint
            0xFF1A141D.toInt(), // Card with purple tint
            0xFFFFFFFF.toInt()
        ),
        NEON(
            "neon",
            "Neon",
            0xFF00F0FF.toInt(), // Cyan
            0xFFFF10F0.toInt(), // Magenta
            0xFF0A0A0F.toInt(), // Very dark blue
            0xFF14141F.toInt(), // Card with blue tint
            0xFFFFFFFF.toInt()
        )
    }

    private var currentTheme: AppTheme = AppTheme.DEFAULT
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedThemeId = prefs.getString(KEY_CURRENT_THEME, AppTheme.DEFAULT.id)
        currentTheme = AppTheme.values().find { it.id == savedThemeId } ?: AppTheme.DEFAULT
    }

    fun getCurrentTheme(): AppTheme = currentTheme

    fun setTheme(theme: AppTheme) {
        currentTheme = theme
        prefs.edit().putString(KEY_CURRENT_THEME, theme.id).apply()
    }

    fun setThemeById(themeId: String) {
        val theme = AppTheme.values().find { it.id == themeId } ?: AppTheme.DEFAULT
        setTheme(theme)
    }

    fun isThemeUnlocked(context: Context, themeId: String): Boolean {
        if (themeId == AppTheme.DEFAULT.id) return true

        val purchasedPrefs = context.getSharedPreferences("shop_prefs", Context.MODE_PRIVATE)
        return purchasedPrefs.getBoolean("theme_$themeId", false)
    }

    fun unlockTheme(context: Context, themeId: String) {
        val purchasedPrefs = context.getSharedPreferences("shop_prefs", Context.MODE_PRIVATE)
        purchasedPrefs.edit().putBoolean("theme_$themeId", true).apply()
    }
}