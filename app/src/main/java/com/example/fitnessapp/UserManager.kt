package com.example.fitnessapp

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object UserManager {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_LEVEL = "user_level"
    private const val KEY_USER_XP = "user_xp"
    private const val KEY_USER_STRENGTH = "user_strength"
    private const val KEY_USER_AGILITY = "user_agility"
    private const val KEY_USER_STAMINA = "user_stamina"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Save user profile data locally
    fun saveUserProfile(
        name: String,
        email: String,
        level: Int = 1,
        xp: Int = 0,
        strength: Int = 10,
        agility: Int = 10,
        stamina: Int = 10
    ) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putInt(KEY_USER_LEVEL, level)
            putInt(KEY_USER_XP, xp)
            putInt(KEY_USER_STRENGTH, strength)
            putInt(KEY_USER_AGILITY, agility)
            putInt(KEY_USER_STAMINA, stamina)
            apply()
        }
    }

    // Load user data from Firestore and cache locally
    fun loadUserDataFromFirestore(context: Context, onComplete: (Boolean) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            onComplete(false)
            return
        }

        Firebase.firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "User"
                    val email = document.getString("email") ?: ""
                    val level = (document.getLong("level") ?: 1L).toInt()
                    val xp = (document.getLong("xp") ?: 0L).toInt()
                    val strength = (document.getLong("strength") ?: 10L).toInt()
                    val agility = (document.getLong("agility") ?: 10L).toInt()
                    val stamina = (document.getLong("stamina") ?: 10L).toInt()

                    saveUserProfile(name, email, level, xp, strength, agility, stamina)
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    // Getters for user data
    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "User") ?: "User"
    }

    fun getUserEmail(): String {
        return prefs.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserLevel(): Int {
        return prefs.getInt(KEY_USER_LEVEL, 1)
    }

    fun getUserXP(): Int {
        return prefs.getInt(KEY_USER_XP, 0)
    }

    fun getUserStrength(): Int {
        return prefs.getInt(KEY_USER_STRENGTH, 10)
    }

    fun getUserAgility(): Int {
        return prefs.getInt(KEY_USER_AGILITY, 10)
    }

    fun getUserStamina(): Int {
        return prefs.getInt(KEY_USER_STAMINA, 10)
    }

    // Update stats
    fun updateUserXP(xp: Int) {
        prefs.edit().putInt(KEY_USER_XP, xp).apply()
    }

    fun updateUserLevel(level: Int) {
        prefs.edit().putInt(KEY_USER_LEVEL, level).apply()
    }

    fun updateStats(strength: Int, agility: Int, stamina: Int) {
        prefs.edit().apply {
            putInt(KEY_USER_STRENGTH, strength)
            putInt(KEY_USER_AGILITY, agility)
            putInt(KEY_USER_STAMINA, stamina)
            apply()
        }
    }

    // Sync local data to Firestore
    fun syncToFirestore() {
        val userId = Firebase.auth.currentUser?.uid ?: return

        val userData = mapOf(
            "name" to getUserName(),
            "email" to getUserEmail(),
            "level" to getUserLevel(),
            "xp" to getUserXP(),
            "strength" to getUserStrength(),
            "agility" to getUserAgility(),
            "stamina" to getUserStamina()
        )

        Firebase.firestore.collection("users").document(userId)
            .update(userData)
    }

    // Clear all user data (for logout)
    fun clearUserData() {
        prefs.edit().clear().apply()
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }
}