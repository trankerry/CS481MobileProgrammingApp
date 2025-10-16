package com.example.fitnessapp

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.databinding.ActivitySignupBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.WindowManager

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

        setupUI()
    }

    private fun setupUI() {
        binding.createAccountBtn.setOnClickListener {
            validateAndCreateAccount()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.signInText.setOnClickListener {
            // Navigate to Sign In
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun validateAndCreateAccount() {
        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        // Validation
        when {
            name.isEmpty() -> {
                binding.nameInput.error = "Please enter your name"
                binding.nameInput.requestFocus()
                return
            }
            email.isEmpty() -> {
                binding.emailInput.error = "Please enter your email"
                binding.emailInput.requestFocus()
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailInput.error = "Please enter a valid email"
                binding.emailInput.requestFocus()
                return
            }
            password.isEmpty() -> {
                binding.passwordInput.error = "Please enter a password"
                binding.passwordInput.requestFocus()
                return
            }
            password.length < 6 -> {
                binding.passwordInput.error = "Password must be at least 6 characters"
                binding.passwordInput.requestFocus()
                return
            }
            password != confirmPassword -> {
                binding.confirmPasswordInput.error = "Passwords don't match"
                binding.confirmPasswordInput.requestFocus()
                return
            }
        }

        // Account created successfully
        // In a real app, you'd save this to SharedPreferences or database
        showSuccessDialog(name)
    }

    private fun showSuccessDialog(name: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("🎉 Welcome to the Team!")
            .setMessage("Your account has been created, $name!\n\nYou're now ready to start your fitness journey and level up!")
            .setPositiveButton("Let's Go!") { _, _ ->
                // Navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }
}