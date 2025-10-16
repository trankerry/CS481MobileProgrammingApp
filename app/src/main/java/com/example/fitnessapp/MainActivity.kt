package com.example.fitnessapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.view.View
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupQuickActions()

        // Setup bottom navigation - find the included layout
        val bottomNav = findViewById<View>(R.id.bottomNav)
        BottomNavHelper.setupBottomNav(this, bottomNav)
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