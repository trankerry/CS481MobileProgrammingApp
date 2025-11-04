package com.example.fitnessapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnessapp.databinding.ActivityWorkoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

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

    // List of common strength exercises for autocomplete
    private val strengthExercises = listOf(
        "Barbell Bench Press",
        "Barbell Squat",
        "Barbell Deadlift",
        "Barbell Row",
        "Barbell Overhead Press",
        "Barbell Curl",
        "Dumbbell Bench Press",
        "Dumbbell Squat",
        "Dumbbell Press",
        "Dumbbell Row",
        "Dumbbell Curl",
        "Dumbbell Bicep Curl",
        "Dumbbell Tricep Extension",
        "Dumbbell Shoulder Press",
        "Dumbbell Lateral Raise",
        "Dumbbell Fly",
        "Dumbbell Lunge",
        "Pull-ups",
        "Push-ups",
        "Chin-ups",
        "Dips",
        "Plank",
        "Leg Press",
        "Leg Extension",
        "Leg Curl",
        "Calf Raise",
        "Lat Pulldown",
        "Cable Fly",
        "Cable Row",
        "Cable Curl",
        "Tricep Pushdown",
        "Face Pull",
        "Hammer Curl",
        "Skull Crusher",
        "Russian Twist",
        "Sit-ups",
        "Crunches",
        "Hanging Leg Raise"
    )

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
        val nameInput = dialogView.findViewById<AutoCompleteTextView>(R.id.exerciseNameInput)
        val setsInput = dialogView.findViewById<EditText>(R.id.setsInput)
        val repsInput = dialogView.findViewById<EditText>(R.id.repsInput)
        val weightInput = dialogView.findViewById<EditText>(R.id.weightInput)

        // Set up autocomplete adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, strengthExercises)
        nameInput.setAdapter(adapter)

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
}