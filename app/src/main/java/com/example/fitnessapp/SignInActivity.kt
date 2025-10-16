package com.example.fitnessapp

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.databinding.ActivitySigninBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.WindowManager

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setupUI()
    }

    private fun setupUI() {
        binding.signInBtn.setOnClickListener {
            validateAndSignIn()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.signUpText.setOnClickListener {
            // Navigate to Sign Up
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.forgotPasswordText.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun validateAndSignIn() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        // Validation
        when {
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
                binding.passwordInput.error = "Please enter your password"
                binding.passwordInput.requestFocus()
                return
            }
        }

        // Sign in successful (in a real app, you'd verify credentials)
        // For demo purposes, we'll just navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showForgotPasswordDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Reset Password")
            .setMessage("Password reset feature coming soon!\n\nFor now, you can create a new account or contact support.")
            .setPositiveButton("OK", null)
            .show()
    }
}