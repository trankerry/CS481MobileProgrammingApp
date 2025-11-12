package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.databinding.ActivitySignupBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding.createAccountBtn.setOnClickListener { validateAndCreateAccount() }
        binding.backBtn.setOnClickListener { finish() }
        binding.signInText.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java)); finish()
        }
    }

    private fun validateAndCreateAccount() {
        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirm = binding.confirmPasswordInput.text.toString()

        when {
            name.isEmpty() -> { binding.nameInput.error = "Enter your name"; binding.nameInput.requestFocus(); return }
            email.isEmpty() -> { binding.emailInput.error = "Enter your email"; binding.emailInput.requestFocus(); return }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { binding.emailInput.error = "Enter a valid email"; binding.emailInput.requestFocus(); return }
            password.length < 6 -> { binding.passwordInput.error = "Min 6 characters"; binding.passwordInput.requestFocus(); return }
            password != confirm -> { binding.confirmPasswordInput.error = "Passwords don't match"; binding.confirmPasswordInput.requestFocus(); return }
        }

        binding.createAccountBtn.isEnabled = false

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = Firebase.auth.currentUser!!.uid
                val profile = mapOf(
                    "name" to name,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis(),
                    "level" to 1,
                    "xp" to 0,
                    "strength" to 10,
                    "agility" to 10,
                    "stamina" to 10,
                    "streak" to 0,
                    "pet" to mapOf(
                        "name" to "Buddy",
                        "level" to 1,
                        "currentXP" to 0,
                        "xpToNextLevel" to 100,
                        "evolutionStage" to 0,
                        "happiness" to 80,
                        "energy" to 75
                    )
                )

                Firebase.firestore.collection("users").document(uid).set(profile)
                    .addOnSuccessListener {
                        // Initialize UserManager and save profile locally
                        UserManager.init(this)
                        UserManager.saveUserProfile(
                            name = name,
                            email = email,
                            level = 1,
                            xp = 0,
                            strength = 10,
                            agility = 10,
                            stamina = 10
                        )

                        // Navigate to main activity
                        startActivity(Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        binding.createAccountBtn.isEnabled = true
                        MaterialAlertDialogBuilder(this)
                            .setTitle("Profile save failed")
                            .setMessage(e.localizedMessage ?: "Unknown error")
                            .setPositiveButton("OK", null)
                            .show()
                    }
            }
            .addOnFailureListener { e ->
                binding.createAccountBtn.isEnabled = true
                MaterialAlertDialogBuilder(this)
                    .setTitle("Sign up failed")
                    .setMessage(e.localizedMessage ?: "Unknown error")
                    .setPositiveButton("OK", null)
                    .show()
            }
    }
}