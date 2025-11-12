package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ThemedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UserManager
        UserManager.init(this)

        // Load user data from Firestore
        loadUserData()

        setupQuickActions()
        setupProfileClick()
        applyTheme()

        // Setup bottom navigation
        val bottomNav = findViewById<View>(R.id.bottomNav)
        BottomNavHelper.setupBottomNav(this, bottomNav)
    }

    override fun onResume() {
        super.onResume()
        // Refresh user data when returning to main activity
        updateUserDisplay()
    }

    private fun loadUserData() {
        UserManager.loadUserDataFromFirestore(this) { success ->
            if (success) {
                updateUserDisplay()
            } else {
                // If loading fails, still show cached data
                updateUserDisplay()
            }
        }
    }

    private fun updateUserDisplay() {
        // Update user name
        val userNameText = findViewById<TextView>(R.id.userNameText)
        val userName = UserManager.getUserName()
        userNameText.text = userName

        // Update level
        val levelText = findViewById<TextView>(R.id.levelText)
        levelText.text = UserManager.getUserLevel().toString()

        // Update XP progress
        val currentXP = UserManager.getUserXP()
        val xpProgressText = findViewById<TextView>(R.id.xpProgressText)
        xpProgressText.text = "$currentXP / 10,000 XP" // You can make this dynamic

        // Update stats
        val strengthText = findViewById<TextView>(R.id.strengthText)
        val agilityText = findViewById<TextView>(R.id.agilityText)
        val staminaText = findViewById<TextView>(R.id.staminaText)

        strengthText.text = UserManager.getUserStrength().toString()
        agilityText.text = UserManager.getUserAgility().toString()
        staminaText.text = UserManager.getUserStamina().toString()
    }

    private fun setupProfileClick() {
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        profileImage.setOnClickListener {
            showProfileMenu()
        }
    }

    private fun showProfileMenu() {
        val options = arrayOf(
            "View Profile",
            "Settings",
            "Logout"
        )

        MaterialAlertDialogBuilder(this)
            .setTitle("${UserManager.getUserName()}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // View Profile - You can implement this later
                        showMessage("Profile", "Profile view coming soon!")
                    }
                    1 -> {
                        // Settings - You can implement this later
                        showMessage("Settings", "Settings coming soon!")
                    }
                    2 -> {
                        // Logout
                        showLogoutConfirmation()
                    }
                }
            }
            .show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?\n\nYour progress is saved and will be here when you return!")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        // Sign out from Firebase
        Firebase.auth.signOut()

        // Clear user data
        UserManager.clearUserData()

        // Clear theme preferences (optional - remove if you want theme to persist)
        // ThemeManager.setTheme(ThemeManager.AppTheme.DEFAULT)

        // Navigate to intro screen
        val intent = Intent(this, IntroActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showMessage(title: String, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun setupQuickActions() {
        // Start Workout
        findViewById<CardView>(R.id.startWorkoutCard).setOnClickListener {
            startActivity(Intent(this, WorkoutActivity::class.java))
        }

        // View Quests
        findViewById<CardView>(R.id.viewQuestsCard).setOnClickListener {
            startActivity(Intent(this, QuestsActivity::class.java))
        }

        // Check Pet
        findViewById<CardView>(R.id.checkPetCard).setOnClickListener {
            startActivity(Intent(this, PetActivity::class.java))
        }
    }
}