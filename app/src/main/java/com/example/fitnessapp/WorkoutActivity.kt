package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fitnessapp.databinding.ActivityWorkoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.EditText

data class Exercise(
    val name: String,
    val type: String, // "strength" or "cardio"
    val sets: Int = 0,
    val reps: Int = 0,
    val weight: Double = 0.0,
    val duration: Int = 0, // minutes for cardio
    val distance: Double = 0.0 // miles/km for cardio
)

class WorkoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkoutBinding
    private val exercises = mutableListOf<Exercise>()
    private var currentMode = "strength" // "strength" or "cardio"

    // Activity result launcher for outdoor tracking
    private val outdoorActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!
            val activityType = data.getStringExtra("activity_type") ?: "Running"
            val duration = data.getIntExtra("duration", 0)
            val distance = data.getDoubleExtra("distance", 0.0)

            // Add outdoor activity as cardio exercise
            val exercise = Exercise(
                name = "$activityType (Outdoor)",
                type = "cardio",
                duration = duration,
                distance = distance
            )
            exercises.add(exercise)
            updateExerciseList()
            updateStats()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        updateStats()

        // Setup bottom navigation - find the included layout
        val bottomNav = findViewById<View>(R.id.bottomNav)
        BottomNavHelper.setupBottomNav(this, bottomNav)
    }

    private fun setupUI() {
        // Toggle between strength and cardio
        binding.strengthBtn.setOnClickListener {
            currentMode = "strength"
            binding.strengthBtn.alpha = 1.0f
            binding.cardioBtn.alpha = 0.5f
            binding.addExerciseBtn.text = "Add Strength Exercise"
        }

        binding.cardioBtn.setOnClickListener {
            currentMode = "cardio"
            binding.strengthBtn.alpha = 0.5f
            binding.cardioBtn.alpha = 1.0f
            binding.addExerciseBtn.text = "Add Cardio Session"
        }

        // Outdoor activity button
        binding.outdoorActivityBtn.setOnClickListener {
            android.util.Log.d("WorkoutActivity", "Outdoor activity button clicked")
            showOutdoorActivityDialog()
        }

        // Add exercise button
        binding.addExerciseBtn.setOnClickListener {
            if (currentMode == "strength") {
                showAddStrengthDialog()
            } else {
                showAddCardioDialog()
            }
        }

        // End workout button
        binding.endWorkoutBtn.setOnClickListener {
            endWorkout()
        }
    }

    private fun showAddStrengthDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_strength, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.exerciseNameInput)
        val setsInput = dialogView.findViewById<EditText>(R.id.setsInput)
        val repsInput = dialogView.findViewById<EditText>(R.id.repsInput)
        val weightInput = dialogView.findViewById<EditText>(R.id.weightInput)

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Strength Exercise")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val exercise = Exercise(
                    name = nameInput.text.toString(),
                    type = "strength",
                    sets = setsInput.text.toString().toIntOrNull() ?: 0,
                    reps = repsInput.text.toString().toIntOrNull() ?: 0,
                    weight = weightInput.text.toString().toDoubleOrNull() ?: 0.0
                )
                exercises.add(exercise)
                updateExerciseList()
                updateStats()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddCardioDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_cardio, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.cardioNameInput)
        val durationInput = dialogView.findViewById<EditText>(R.id.durationInput)
        val distanceInput = dialogView.findViewById<EditText>(R.id.distanceInput)

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Cardio Session")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val exercise = Exercise(
                    name = nameInput.text.toString(),
                    type = "cardio",
                    duration = durationInput.text.toString().toIntOrNull() ?: 0,
                    distance = distanceInput.text.toString().toDoubleOrNull() ?: 0.0
                )
                exercises.add(exercise)
                updateExerciseList()
                updateStats()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateExerciseList() {
        // Clear and rebuild exercise list
        binding.exerciseListContainer.removeAllViews()

        exercises.forEach { exercise ->
            val itemView = layoutInflater.inflate(R.layout.item_exercise, null)
            val nameText = itemView.findViewById<android.widget.TextView>(R.id.exerciseName)
            val detailsText = itemView.findViewById<android.widget.TextView>(R.id.exerciseDetails)

            nameText.text = exercise.name
            detailsText.text = if (exercise.type == "strength") {
                "${exercise.sets} sets × ${exercise.reps} reps @ ${exercise.weight}lbs"
            } else {
                "${exercise.duration} min • ${exercise.distance} mi"
            }

            binding.exerciseListContainer.addView(itemView)
        }

        binding.emptyState.visibility = if (exercises.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun updateStats() {
        val totalSets = exercises.filter { it.type == "strength" }.sumOf { it.sets }
        val totalVolume = exercises.filter { it.type == "strength" }
            .sumOf { it.sets * it.reps * it.weight }
        val totalCardio = exercises.filter { it.type == "cardio" }.sumOf { it.duration }
        val totalDistance = exercises.filter { it.type == "cardio" }.sumOf { it.distance }

        binding.totalSetsText.text = totalSets.toString()
        binding.totalVolumeText.text = String.format("%.0f lbs", totalVolume)
        binding.totalCardioText.text = "$totalCardio min"
        binding.totalDistanceText.text = String.format("%.1f mi", totalDistance)

        // Calculate XP gains (rough formula)
        val strXP = (totalVolume / 100).toInt()
        val aglXP = (totalDistance * 10).toInt()
        val staXP = (totalCardio * 2).toInt()

        binding.xpGainText.text = "+${strXP + aglXP + staXP} XP"
    }

    private fun endWorkout() {
        if (exercises.isEmpty()) {
            MaterialAlertDialogBuilder(this)
                .setTitle("No Exercises Logged")
                .setMessage("Add some exercises before ending your workout!")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Workout Complete! 💪")
            .setMessage("Great work! Your stats have been updated and XP has been awarded.")
            .setPositiveButton("Finish") { _, _ ->
                finish()
            }
            .show()
    }

    private fun showOutdoorActivityDialog() {
        val activities = arrayOf("Running", "Cycling", "Walking")

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Outdoor Activity")
            .setItems(activities) { _, which ->
                val selectedActivity = activities[which]
                startOutdoorTracking(selectedActivity)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startOutdoorTracking(activityType: String) {
        val intent = Intent(this, OutdoorTrackingActivity::class.java)
        intent.putExtra("ACTIVITY_TYPE", activityType)
        outdoorActivityLauncher.launch(intent)
    }
}