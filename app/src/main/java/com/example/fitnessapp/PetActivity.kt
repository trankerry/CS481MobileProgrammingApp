package com.example.fitnessapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.databinding.ActivityPetBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.EditText

data class Pet(
    var name: String = "Buddy",
    var level: Int = 1,
    var currentXP: Int = 0,
    var xpToNextLevel: Int = 100,
    var evolutionStage: Int = 0, // 0=Egg, 1=Baby, 2=Teen, 3=Adult, 4=Legendary
    var happiness: Int = 80,
    var energy: Int = 75,
    var lastFed: Long = System.currentTimeMillis(),
    var streak: Int = 0 // Days of consistent activity
)

class PetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPetBinding
    private lateinit var pet: Pet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPet()
        setupUI()
        updatePetDisplay()
    }

    private fun loadPet() {
        // In a real app, load from SharedPreferences or database
        pet = Pet()
    }

    private fun setupUI() {
        binding.feedBtn.setOnClickListener {
            feedPet()
        }

        binding.playBtn.setOnClickListener {
            playWithPet()
        }

        binding.trainBtn.setOnClickListener {
            trainPet()
        }

        binding.renameBtn.setOnClickListener {
            renamePet()
        }
    }

    private fun updatePetDisplay() {
        // Update pet info
        binding.petNameText.text = pet.name
        binding.petLevelText.text = "Level ${pet.level}"
        binding.streakText.text = "${pet.streak} Day Streak 🔥"

        // Update evolution stage
        val (stageName, stageEmoji, stageDesc) = when (pet.evolutionStage) {
            0 -> Triple("Egg", "🥚", "Keep working out to hatch your companion!")
            1 -> Triple("Baby", "🐣", "Your pet is growing! Stay consistent!")
            2 -> Triple("Teen", "🦎", "Almost there! Keep pushing!")
            3 -> Triple("Adult", "🐉", "Fully evolved! A true companion!")
            4 -> Triple("Legendary", "✨🐉✨", "Maximum evolution achieved!")
            else -> Triple("Unknown", "❓", "")
        }

        binding.evolutionStageText.text = "$stageEmoji $stageName"
        binding.evolutionDescText.text = stageDesc

        // Update XP bar
        binding.xpProgressBar.max = pet.xpToNextLevel
        binding.xpProgressBar.progress = pet.currentXP
        binding.xpText.text = "${pet.currentXP}/${pet.xpToNextLevel} XP"

        // Update stats
        binding.happinessBar.progress = pet.happiness
        binding.happinessText.text = "${pet.happiness}%"

        binding.energyBar.progress = pet.energy
        binding.energyText.text = "${pet.energy}%"

        // Update pet image based on evolution
        val petImage = when (pet.evolutionStage) {
            0 -> "🥚"
            1 -> "🐣"
            2 -> "🦎"
            3 -> "🐉"
            4 -> "✨🐉✨"
            else -> "❓"
        }
        binding.petImageText.text = petImage
    }

    private fun feedPet() {
        if (pet.energy >= 100) {
            showMessage("Full Energy", "${pet.name} is already full of energy!")
            return
        }

        pet.energy = minOf(100, pet.energy + 25)
        pet.happiness = minOf(100, pet.happiness + 10)

        showMessage("Fed ${pet.name}!", "Energy and happiness increased!")
        updatePetDisplay()
    }

    private fun playWithPet() {
        if (pet.energy < 20) {
            showMessage("Too Tired", "${pet.name} is too tired to play. Feed them first!")
            return
        }

        pet.energy = maxOf(0, pet.energy - 15)
        pet.happiness = minOf(100, pet.happiness + 20)
        pet.currentXP += 10

        showMessage("Playtime!", "${pet.name} had fun! +10 XP")
        checkLevelUp()
        updatePetDisplay()
    }

    private fun trainPet() {
        if (pet.energy < 30) {
            showMessage("Too Tired", "${pet.name} needs more energy to train!")
            return
        }

        pet.energy = maxOf(0, pet.energy - 25)
        pet.currentXP += 25
        pet.happiness = maxOf(0, pet.happiness - 5)

        showMessage("Training Complete!", "${pet.name} gained +25 XP!")
        checkLevelUp()
        updatePetDisplay()
    }

    private fun checkLevelUp() {
        if (pet.currentXP >= pet.xpToNextLevel) {
            pet.currentXP -= pet.xpToNextLevel
            pet.level++
            pet.xpToNextLevel = (pet.xpToNextLevel * 1.5).toInt()

            // Check for evolution
            val oldStage = pet.evolutionStage
            when {
                pet.level >= 30 && pet.streak >= 30 -> pet.evolutionStage = 4 // Legendary
                pet.level >= 20 -> pet.evolutionStage = 3 // Adult
                pet.level >= 10 -> pet.evolutionStage = 2 // Teen
                pet.level >= 5 -> pet.evolutionStage = 1 // Baby
            }

            if (pet.evolutionStage > oldStage) {
                showEvolutionDialog()
            } else {
                showMessage("Level Up! 🎉", "${pet.name} reached Level ${pet.level}!")
            }
        }
    }

    private fun showEvolutionDialog() {
        val (stageName, stageEmoji) = when (pet.evolutionStage) {
            1 -> Pair("Baby", "🐣")
            2 -> Pair("Teen", "🦎")
            3 -> Pair("Adult", "🐉")
            4 -> Pair("Legendary", "✨🐉✨")
            else -> Pair("Unknown", "❓")
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("$stageEmoji EVOLUTION! $stageEmoji")
            .setMessage("${pet.name} evolved into $stageName stage!\n\nYour consistency is paying off!")
            .setPositiveButton("Awesome!") { _, _ ->
                updatePetDisplay()
            }
            .setCancelable(false)
            .show()
    }

    private fun renamePet() {
        val input = EditText(this)
        input.setText(pet.name)
        input.setTextColor(resources.getColor(android.R.color.black, null))

        MaterialAlertDialogBuilder(this)
            .setTitle("Rename Your Pet")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    pet.name = newName
                    updatePetDisplay()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMessage(title: String, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    // This would be called when user completes workouts/quests
    fun addActivityXP(amount: Int) {
        pet.currentXP += amount
        pet.streak++ // Increment streak
        pet.happiness = minOf(100, pet.happiness + 5)
        checkLevelUp()
        updatePetDisplay()
    }
}