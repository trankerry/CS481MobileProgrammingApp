package com.example.fitnessapp

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.LinearLayout

object BottomNavHelper {

    fun setupBottomNav(activity: Activity, bottomNavView: View) {
        val navTab1 = bottomNavView.findViewById<LinearLayout>(R.id.navTab1)
        val navTab2 = bottomNavView.findViewById<LinearLayout>(R.id.navTab2)
        val navHome = bottomNavView.findViewById<LinearLayout>(R.id.navHome)
        val navTab4 = bottomNavView.findViewById<LinearLayout>(R.id.navTab4)
        val navShop = bottomNavView.findViewById<LinearLayout>(R.id.navShop)

        // Tab 1 - Workout Tracker
        navTab1?.setOnClickListener {
            if (activity !is WorkoutActivity) {
                val intent = Intent(activity, WorkoutActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                activity.startActivity(intent)
            }
        }

        // Tab 2 - Daily Quests
        navTab2?.setOnClickListener {
            if (activity !is QuestsActivity) {
                val intent = Intent(activity, QuestsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                activity.startActivity(intent)
            }
        }

        // Home
        navHome?.setOnClickListener {
            if (activity !is MainActivity) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                activity.startActivity(intent)
            }
        }

        // Tab 4 - Pet Companion
        navTab4?.setOnClickListener {
            if (activity !is PetActivity) {
                val intent = Intent(activity, PetActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                activity.startActivity(intent)
            }
        }

        // Shop
        navShop?.setOnClickListener {
            if (activity !is ShopActivity) {
                val intent = Intent(activity, ShopActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                activity.startActivity(intent)
            }
        }

        // Highlight current tab
        highlightCurrentTab(activity, bottomNavView)
    }

    private fun highlightCurrentTab(activity: Activity, bottomNavView: View) {
        val navTab1 = bottomNavView.findViewById<LinearLayout>(R.id.navTab1)
        val navTab2 = bottomNavView.findViewById<LinearLayout>(R.id.navTab2)
        val navHome = bottomNavView.findViewById<LinearLayout>(R.id.navHome)
        val navTab4 = bottomNavView.findViewById<LinearLayout>(R.id.navTab4)
        val navShop = bottomNavView.findViewById<LinearLayout>(R.id.navShop)

        // Reset all to default opacity
        navTab1?.alpha = 0.6f
        navTab2?.alpha = 0.6f
        navHome?.alpha = 0.6f
        navTab4?.alpha = 0.6f
        navShop?.alpha = 0.6f

        // Highlight current tab
        when (activity) {
            is WorkoutActivity -> navTab1?.alpha = 1.0f
            is QuestsActivity -> navTab2?.alpha = 1.0f
            is MainActivity -> navHome?.alpha = 1.0f
            is PetActivity -> navTab4?.alpha = 1.0f
            is ShopActivity -> navShop?.alpha = 1.0f
        }
    }
}