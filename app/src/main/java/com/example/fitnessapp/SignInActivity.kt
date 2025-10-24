
package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.databinding.ActivitySigninBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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

        binding.signInBtn.setOnClickListener { validateAndSignIn() }
        binding.backBtn.setOnClickListener { finish() }
        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java)); finish()
        }
        binding.forgotPasswordText.setOnClickListener { sendPasswordReset() }
    }

    private fun validateAndSignIn() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        when {
            email.isEmpty() -> { binding.emailInput.error = "Enter your email"; binding.emailInput.requestFocus(); return }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { binding.emailInput.error = "Enter a valid email"; binding.emailInput.requestFocus(); return }
            password.isEmpty() -> { binding.passwordInput.error = "Enter your password"; binding.passwordInput.requestFocus(); return }
        }

        binding.signInBtn.isEnabled = false

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            }
            .addOnFailureListener { e ->
                binding.signInBtn.isEnabled = true
                MaterialAlertDialogBuilder(this)
                    .setTitle("Sign in failed")
                    .setMessage(e.localizedMessage ?: "Unknown error")
                    .setPositiveButton("OK", null)
                    .show()
            }
    }

    private fun sendPasswordReset() {
        val email = binding.emailInput.text.toString().trim()
        if (email.isEmpty()) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Reset Password")
                .setMessage("Enter your email first, then tap Reset again.")
                .setPositiveButton("OK", null)
                .show()
            return
        }
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Check your email")
                    .setMessage("We sent a reset link to $email.")
                    .setPositiveButton("OK", null)
                    .show()
            }
            .addOnFailureListener { e ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Couldn't send reset email")
                    .setMessage(e.localizedMessage ?: "Unknown error")
                    .setPositiveButton("OK", null)
                    .show()
            }
    }
}
