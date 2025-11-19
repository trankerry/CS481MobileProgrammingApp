package com.example.fitnessapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fitnessapp.databinding.ActivityOutdoorTrackingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.Intent

class OutdoorTrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOutdoorTrackingBinding

    private var activityType = "Running" // Running, Cycling, Walking
    private var isTracking = false
    private var startTime = 0L
    private var pauseOffset = 0L

    private var totalDistance = 0.0 // in meters
    private var lastLocation: Location? = null

    private val LOCATION_PERMISSION_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutdoorTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityType = intent.getStringExtra("ACTIVITY_TYPE") ?: "Running"

        setupUI()
        checkLocationPermission()
    }

    private fun setupUI() {
        binding.activityTypeText.text = activityType

        binding.backBtn.setOnClickListener {
            if (isTracking) {
                showExitConfirmation()
            } else {
                finish()
            }
        }

        binding.startPauseBtn.setOnClickListener {
            if (isTracking) {
                pauseTracking()
            } else {
                startTracking()
            }
        }

        binding.finishBtn.setOnClickListener {
            finishWorkout()
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                binding.permissionCard.visibility = android.view.View.GONE
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Show explanation
                showPermissionRationale()
            }

            else -> {
                // Request permission
                binding.requestPermissionBtn.setOnClickListener {
                    requestLocationPermission()
                }
            }
        }
    }

    private fun showPermissionRationale() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Location Permission Needed")
            .setMessage("This app needs location permission to track your outdoor ${activityType.lowercase()} activity and calculate distance.\n\nYour location data is only used during active workouts and is not shared.")
            .setPositiveButton("Grant Permission") { _, _ ->
                requestLocationPermission()
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .show()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    binding.permissionCard.visibility = android.view.View.GONE
                    MaterialAlertDialogBuilder(this)
                        .setTitle("✓ Permission Granted")
                        .setMessage("Location tracking is now enabled! You can start your ${activityType.lowercase()} session.")
                        .setPositiveButton("Got it!", null)
                        .show()
                } else {
                    // Permission denied
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Permission Denied")
                        .setMessage("Location permission is required to track outdoor activities. You can grant it later in Settings.")
                        .setPositiveButton("Exit") { _, _ ->
                            finish()
                        }
                        .show()
                }
            }
        }
    }

    private fun startTracking() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Location Permission Required")
                .setMessage("Please grant location permission to start tracking.")
                .setPositiveButton("Grant") { _, _ ->
                    requestLocationPermission()
                }
                .setNegativeButton("Cancel", null)
                .show()
            return
        }

        isTracking = true
        startTime = SystemClock.elapsedRealtime() - pauseOffset

        binding.startPauseBtn.text = "PAUSE"
        binding.startPauseBtn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light))
        binding.finishBtn.visibility = android.view.View.VISIBLE
        binding.statusText.text = "Tracking..."

        // In a real app, start LocationManager or FusedLocationProviderClient here
        startTimer()
    }

    private fun pauseTracking() {
        isTracking = false
        pauseOffset = SystemClock.elapsedRealtime() - startTime

        binding.startPauseBtn.text = "RESUME"
        binding.statusText.text = "Paused"

        // Stop location updates
    }

    private fun startTimer() {
        binding.chronometer.base = startTime
        binding.chronometer.start()
    }

    private fun finishWorkout() {
        if (totalDistance == 0.0) {
            // Simulate some distance for demo
            totalDistance = (1000..5000).random().toDouble() // Random distance between 1-5km
        }

        val elapsedMillis = SystemClock.elapsedRealtime() - startTime
        val elapsedMinutes = (elapsedMillis / 1000 / 60).toInt()

        val distanceKm = totalDistance / 1000
        val distanceMiles = distanceKm * 0.621371

        MaterialAlertDialogBuilder(this)
            .setTitle("🎉 Workout Complete!")
            .setMessage(
                "Activity: $activityType\n" +
                        "Time: $elapsedMinutes minutes\n" +
                        "Distance: ${String.format("%.2f", distanceMiles)} miles\n\n" +
                        "Great job! Your workout has been saved."
            )
            .setPositiveButton("Finish") { _, _ ->
                // Return data to WorkoutActivity
                val resultIntent = Intent()
                resultIntent.putExtra("activity_type", activityType)
                resultIntent.putExtra("duration", elapsedMinutes)
                resultIntent.putExtra("distance", distanceMiles)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showExitConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Exit Workout?")
            .setMessage("Your workout is in progress. Are you sure you want to exit?\n\nYour progress will be lost.")
            .setPositiveButton("Exit") { _, _ ->
                finish()
            }
            .setNegativeButton("Continue", null)
            .show()
    }
}