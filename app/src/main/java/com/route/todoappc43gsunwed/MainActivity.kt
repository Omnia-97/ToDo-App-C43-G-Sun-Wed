package com.route.todoappc43gsunwed

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.route.todoappc43gsunwed.callbacks.OnTaskAddedListener
import com.route.todoappc43gsunwed.databinding.ActivityMainBinding
import com.route.todoappc43gsunwed.extension.clearTime
import com.route.todoappc43gsunwed.extension.toCalendarInstant
import com.route.todoappc43gsunwed.fragments.AddTaskBottomSheetFragment
import com.route.todoappc43gsunwed.fragments.SettingsFragment
import com.route.todoappc43gsunwed.fragments.TaskListFragment
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // null
    private val taskListFragment = TaskListFragment()
    private val settingsFragment = SettingsFragment()
    private val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavView()


    }
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = newBase.resources.configuration
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    private fun initBottomNavView() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        binding.todoBottomAppBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_tasks -> pushFragment(taskListFragment)
                R.id.navigation_settings -> pushFragment(settingsFragment)
            }
            return@setOnItemSelectedListener true
        }

        val lastTab = prefs.getInt("last_tab", R.id.navigation_tasks)
        binding.todoBottomAppBar.selectedItemId = lastTab

        binding.todoBottomAppBar.setOnItemSelectedListener {
            prefs.edit().putInt("last_tab", it.itemId).apply()
            when (it.itemId) {
                R.id.navigation_tasks -> pushFragment(taskListFragment)
                R.id.navigation_settings -> pushFragment(settingsFragment)
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.taskFragmentContainer.id, fragment)
            .commit()
    }
}