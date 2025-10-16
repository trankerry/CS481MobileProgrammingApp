package com.example.fitnessapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.databinding.ActivityIntroBinding
import android.content.Intent
import android.view.WindowManager

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding.startBtn.setOnClickListener {
            // Navigate to Sign Up page
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.signInBtn.setOnClickListener {
            // Navigate to Sign In page
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}