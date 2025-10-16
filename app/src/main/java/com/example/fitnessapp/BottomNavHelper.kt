package com.example.fitnessapp

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.util.Log

object BottomNavHelper {

    fun setupBottomNav(activity: Activity, bottomNavView: View?) {
        if (bottomNavView == null) {
            Log.e("BottomNav", "bottomNavView is null!")
            return
        }

        val navTab1 = bottomNavView.findViewById<LinearLayout>(R.id.navTab1)
        val navTab2 = bottomNavView.findViewById<LinearLayout>(R.id.navTab2)
        val navHome = bottomNavView.findViewById<LinearLayout>(R.id.navHome)
        val navTab4 = bottomNavView.findViewById<LinearLayout>(R.id.navTab4)
        val navShop = bottomNavView.findViewById<LinearLayout>(R.id.navShop)

        Log.d("BottomNav", "navTab1: $navTab1, navTab2: $navTab2, navHome: $navHome, navTab4: $navTab4, navShop: $navShop")

        // Tab 1 - Workout Tracker
        navTab1?.setOnClickListener {
            Log.d("BottomNav", "Tab 1 clicked from ${activity.javaClass.simpleName}")
            if (activity !is WorkoutActivity) {
                val intent = Intent(activity, WorkoutActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            }
        }

        // Tab 2 - Daily Quests
        navTab2?.setOnClickListener {
            Log.d("BottomNav", "Tab 2 clicked from ${activity.javaClass.simpleName}")
            if (activity !is QuestsActivity) {
                val intent = Intent(activity, QuestsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            }
        }

        // Home
        navHome?.setOnClickListener {
            Log.d("BottomNav", "Home clicked from ${activity.javaClass.simpleName}")
            if (activity !is MainActivity) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            }
        }

        // Tab 4 - Pet Companion
        navTab4?.setOnClickListener {
            Log.d("BottomNav", "Tab 4 clicked from ${activity.javaClass.simpleName}")
            if (activity !is PetActivity) {
                val intent = Intent(activity, PetActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            }
        }

        // Shop
        navShop?.setOnClickListener {
            Log.d("BottomNav", "Shop clicked from ${activity.javaClass.simpleName}")
            if (activity !is ShopActivity) {
                val intent = Intent(activity, ShopActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
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