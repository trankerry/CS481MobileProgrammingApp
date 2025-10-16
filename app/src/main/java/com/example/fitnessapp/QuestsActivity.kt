package com.example.fitnessapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.databinding.ActivityQuestsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

data class Quest(
    val id: Int,
    val title: String,
    val description: String,
    val xpReward: Int,
    val statBonus: String, // "STR", "AGL", or "STA"
    val statPoints: Int,
    var isCompleted: Boolean = false,
    var progress: Int = 0,
    val goal: Int = 1
)

class QuestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestsBinding
    private val dailyQuests = mutableListOf<Quest>()
    private var totalXPEarned = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        generateDailyQuests()
        updateQuestList()

        // Setup bottom navigation - find the included layout
        val bottomNav = findViewById<View>(R.id.bottomNav)
        BottomNavHelper.setupBottomNav(this, bottomNav)
    }

    private fun setupUI() {
        // Display current date
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        binding.dateText.text = dateFormat.format(Date())

        // Refresh quests button (simulates new day)
        binding.refreshBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Refresh Quests?")
                .setMessage("This will generate new daily quests. Current progress will be lost!")
                .setPositiveButton("Refresh") { _, _ ->
                    generateDailyQuests()
                    updateQuestList()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun generateDailyQuests() {
        dailyQuests.clear()
        totalXPEarned = 0

        val allQuests = listOf(
            Quest(1, "Morning Walk", "Walk 1 mile", 50, "AGL", 5, goal = 1),
            Quest(2, "Push-Up Challenge", "Complete 50 push-ups", 75, "STR", 8, goal = 50),
            Quest(3, "Sit-Up Master", "Complete 50 sit-ups", 75, "STA", 8, goal = 50),
            Quest(4, "Cardio Warrior", "Run for 20 minutes", 100, "STA", 10, goal = 20),
            Quest(5, "Weight Lifter", "Complete 3 strength exercises", 100, "STR", 12, goal = 3),
            Quest(6, "Hydration Hero", "Drink 8 glasses of water", 50, "STA", 5, goal = 8),
            Quest(7, "Step Counter", "Walk 10,000 steps", 150, "AGL", 15, goal = 10000),
            Quest(8, "Plank Master", "Hold plank for 2 minutes", 100, "STA", 10, goal = 120),
            Quest(9, "Sprint Session", "Run 3 miles", 125, "AGL", 12, goal = 3),
            Quest(10, "Gym Rat", "Complete a full workout session", 200, "STR", 20, goal = 1)
        )

        // Select 5 random quests for today
        dailyQuests.addAll(allQuests.shuffled().take(5))

        binding.totalQuestsText.text = "0/5"
        binding.xpEarnedText.text = "+0 XP"
    }

    private fun updateQuestList() {
        binding.questListContainer.removeAllViews()

        var completedCount = 0
        totalXPEarned = 0

        dailyQuests.forEach { quest ->
            val itemView = layoutInflater.inflate(R.layout.item_quest, null)

            val questTitle = itemView.findViewById<android.widget.TextView>(R.id.questTitle)
            val questDesc = itemView.findViewById<android.widget.TextView>(R.id.questDescription)
            val questReward = itemView.findViewById<android.widget.TextView>(R.id.questReward)
            val progressBar = itemView.findViewById<android.widget.ProgressBar>(R.id.questProgressBar)
            val progressText = itemView.findViewById<android.widget.TextView>(R.id.questProgressText)
            val completeBtn = itemView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.completeQuestBtn)
            val completedIcon = itemView.findViewById<android.widget.ImageView>(R.id.completedIcon)

            questTitle.text = quest.title
            questDesc.text = quest.description
            questReward.text = "+${quest.xpReward} XP • +${quest.statPoints} ${quest.statBonus}"

            progressBar.max = quest.goal
            progressBar.progress = quest.progress
            progressText.text = "${quest.progress}/${quest.goal}"

            if (quest.isCompleted) {
                completedCount++
                totalXPEarned += quest.xpReward
                completeBtn.visibility = View.GONE
                completedIcon.visibility = View.VISIBLE
                itemView.alpha = 0.6f
            } else {
                completeBtn.visibility = View.VISIBLE
                completedIcon.visibility = View.GONE
                itemView.alpha = 1.0f

                completeBtn.setOnClickListener {
                    showQuestCompleteDialog(quest)
                }
            }

            binding.questListContainer.addView(itemView)
        }

        binding.totalQuestsText.text = "$completedCount/5"
        binding.xpEarnedText.text = "+$totalXPEarned XP"

        // Check if all quests completed
        if (completedCount == 5) {
            showAllQuestsCompleteDialog()
        }
    }

    private fun showQuestCompleteDialog(quest: Quest) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Mark as Complete?")
            .setMessage("Did you complete: ${quest.title}?\n\nYou'll earn:\n+${quest.xpReward} XP\n+${quest.statPoints} ${quest.statBonus} points")
            .setPositiveButton("Complete") { _, _ ->
                quest.isCompleted = true
                quest.progress = quest.goal
                updateQuestList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAllQuestsCompleteDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("🎉 All Quests Complete!")
            .setMessage("You crushed it today! You earned $totalXPEarned XP and massive stat bonuses!\n\nCome back tomorrow for new quests!")
            .setPositiveButton("Awesome!") { _, _ ->
                finish()
            }
            .show()
    }
}