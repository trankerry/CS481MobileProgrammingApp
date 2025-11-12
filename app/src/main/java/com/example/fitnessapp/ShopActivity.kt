package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.fitnessapp.databinding.ActivityShopBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout

data class ShopItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val emoji: String,
    val themeId: String? = null, // For cosmetic themes
    var isPurchased: Boolean = false
)

class ShopActivity : ThemedActivity() {
    private lateinit var binding: ActivityShopBinding
    private var currentXP = 2500
    private val purchasedItems = mutableSetOf<Int>()

    private val allItems = listOf(
        // Cosmetics - Themes
        ShopItem(1, "Golden Theme", "Unlock golden UI theme", 500, "cosmetic", "✨", "golden"),
        ShopItem(2, "Dark Purple Theme", "Sleek purple color scheme", 400, "cosmetic", "💜", "purple"),
        ShopItem(3, "Neon Theme", "Cyberpunk neon aesthetics", 600, "cosmetic", "🌃", "neon"),

        // Other Cosmetics
        ShopItem(4, "Profile Frame: Fire", "Legendary fire border", 800, "cosmetic", "🔥"),
        ShopItem(5, "Profile Frame: Ice", "Cool ice border", 800, "cosmetic", "❄️"),
        ShopItem(6, "Title: Warrior", "Display 'Fitness Warrior' title", 300, "cosmetic", "⚔️"),
        ShopItem(7, "Title: Legend", "Display 'Living Legend' title", 1000, "cosmetic", "👑"),

        // Power-ups
        ShopItem(8, "2x XP Boost", "Double XP for 24 hours", 750, "powerup", "⚡"),
        ShopItem(9, "Stat Multiplier", "+50% all stats for 1 week", 1200, "powerup", "📈"),
        ShopItem(10, "Quest Refresh", "Get 5 new quests instantly", 300, "powerup", "🔄"),
        ShopItem(11, "Instant Level Up", "Gain 1 level immediately", 1500, "powerup", "🆙"),
        ShopItem(12, "Streak Saver", "Protect your streak for 3 days", 500, "powerup", "🛡️"),

        // Pet Items
        ShopItem(13, "Pet Costume: Ninja", "Dress your pet as ninja", 600, "pet", "🥷"),
        ShopItem(14, "Pet Costume: Knight", "Dress your pet as knight", 600, "pet", "🛡️"),
        ShopItem(15, "Pet Accessory: Crown", "Royal crown for your pet", 400, "pet", "👑"),
        ShopItem(16, "Pet Accessory: Wings", "Give your pet wings", 800, "pet", "🪽"),
        ShopItem(17, "Pet Food Bundle", "5x premium pet food", 350, "pet", "🍖"),
        ShopItem(18, "Pet XP Boost", "2x pet XP for 3 days", 700, "pet", "🌟"),
        ShopItem(19, "Evolution Potion", "Instant pet evolution", 2000, "pet", "🧪")
    )

    private var currentCategory = "cosmetic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPurchasedItems()
        setupUI()
        updateXPDisplay()
        showItems(currentCategory)
        applyTheme()

        val bottomNav = binding.root.findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.bottomNav)
        BottomNavHelper.setupBottomNav(this, bottomNav)
    }

    private fun loadPurchasedItems() {
        val prefs = getSharedPreferences("shop_prefs", MODE_PRIVATE)

        // Load purchased item IDs
        allItems.forEach { item ->
            if (prefs.getBoolean("item_${item.id}", false)) {
                purchasedItems.add(item.id)
            }
        }

        // Load XP
        currentXP = prefs.getInt("user_xp", 2500)
    }

    private fun savePurchase(item: ShopItem) {
        val prefs = getSharedPreferences("shop_prefs", MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("item_${item.id}", true)
            putInt("user_xp", currentXP)

            // If it's a theme, mark it as unlocked
            if (item.themeId != null) {
                putBoolean("theme_${item.themeId}", true)
            }
            apply()
        }
    }

    private fun setupUI() {
        binding.shopTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentCategory = when (tab?.position) {
                    0 -> "cosmetic"
                    1 -> "powerup"
                    2 -> "pet"
                    else -> "cosmetic"
                }
                showItems(currentCategory)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateXPDisplay() {
        binding.xpBalanceText.text = "$currentXP XP"
    }

    private fun showItems(category: String) {
        binding.shopItemsContainer.removeAllViews()
        val items = allItems.filter { it.category == category }

        items.forEach { item ->
            val itemView = layoutInflater.inflate(R.layout.item_shop, null)

            val emojiText = itemView.findViewById<android.widget.TextView>(R.id.itemEmoji)
            val nameText = itemView.findViewById<android.widget.TextView>(R.id.itemName)
            val descText = itemView.findViewById<android.widget.TextView>(R.id.itemDescription)
            val priceText = itemView.findViewById<android.widget.TextView>(R.id.itemPrice)
            val buyBtn = itemView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.buyBtn)
            val ownedBadge = itemView.findViewById<android.widget.TextView>(R.id.ownedBadge)
            val activeBtn = itemView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.activeBtn)

            emojiText.text = item.emoji
            nameText.text = item.name
            descText.text = item.description
            priceText.text = "${item.price} XP"

            if (purchasedItems.contains(item.id)) {
                buyBtn.visibility = View.GONE
                ownedBadge.visibility = View.VISIBLE

                // Show "Active" button for themes
                if (item.themeId != null) {
                    activeBtn.visibility = View.VISIBLE
                    val isActive = ThemeManager.getCurrentTheme().id == item.themeId
                    activeBtn.text = if (isActive) "✓ ACTIVE" else "ACTIVATE"
                    activeBtn.isEnabled = !isActive
                    activeBtn.alpha = if (isActive) 0.6f else 1.0f

                    activeBtn.setOnClickListener {
                        activateTheme(item)
                    }
                } else {
                    activeBtn.visibility = View.GONE
                }
            } else {
                buyBtn.visibility = View.VISIBLE
                ownedBadge.visibility = View.GONE
                activeBtn.visibility = View.GONE

                if (currentXP >= item.price) {
                    buyBtn.isEnabled = true
                    buyBtn.alpha = 1.0f
                } else {
                    buyBtn.isEnabled = false
                    buyBtn.alpha = 0.5f
                }

                buyBtn.setOnClickListener {
                    showPurchaseDialog(item)
                }
            }

            binding.shopItemsContainer.addView(itemView)
        }
    }

    private fun activateTheme(item: ShopItem) {
        if (item.themeId == null) return

        ThemeManager.setThemeById(item.themeId)

        MaterialAlertDialogBuilder(this)
            .setTitle("${item.emoji} Theme Activated!")
            .setMessage("${item.name} is now active!\n\nRestarting to apply changes...")
            .setPositiveButton("OK") { _, _ ->
                // Recreate activity to apply theme
                recreate()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPurchaseDialog(item: ShopItem) {
        if (currentXP < item.price) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Not Enough XP")
                .setMessage("You need ${item.price - currentXP} more XP to purchase this item.\n\nKeep working out to earn more XP!")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val message = if (item.themeId != null) {
            "${item.description}\n\nYou can activate this theme anytime from the shop after purchase!\n\nPrice: ${item.price} XP\nYour Balance: $currentXP XP\nAfter Purchase: ${currentXP - item.price} XP"
        } else {
            "${item.description}\n\nPrice: ${item.price} XP\nYour Balance: $currentXP XP\nAfter Purchase: ${currentXP - item.price} XP"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("${item.emoji} Purchase ${item.name}?")
            .setMessage(message)
            .setPositiveButton("Buy") { _, _ ->
                purchaseItem(item)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun purchaseItem(item: ShopItem) {
        currentXP -= item.price
        purchasedItems.add(item.id)
        savePurchase(item)
        updateXPDisplay()
        showItems(currentCategory)

        val message = if (item.themeId != null) {
            "You bought ${item.name}!\n\nTap 'ACTIVATE' to apply this theme to your app!"
        } else {
            "You bought ${item.name}!\n\nCheck your profile to equip and use your new item."
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("${item.emoji} Purchase Complete!")
            .setMessage(message)
            .setPositiveButton("Awesome!", null)
            .show()
    }
}