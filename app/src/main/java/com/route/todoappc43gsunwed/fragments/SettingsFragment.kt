package com.route.todoappc43gsunwed.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.route.todoappc43gsunwed.R
import com.route.todoappc43gsunwed.databinding.FragmentSettingsBinding
import java.util.Locale


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLanguageSpinner()
        setupThemeSpinner()
    }


    private fun setupLanguageSpinner() {
        val languages = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            languages
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        val savedLang = getSavedLanguage()
        binding.languageSpinner.setSelection(if (savedLang == "ar") 1 else 0)

        binding.languageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                var isFirst = true
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (isFirst) {
                        isFirst = false; return
                    }
                    val langCode = if (position == 1) "ar" else "en"
                    if (langCode != getSavedLanguage()) {
                        saveLanguage(langCode)
                        applyLanguage(langCode)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    private fun setupThemeSpinner() {
        val themes = resources.getStringArray(R.array.themes)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, themes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.themeSpinner.adapter = adapter

        val isDark = getSavedTheme()
        binding.themeSpinner.setSelection(if (isDark) 1 else 0)

        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var isFirst = true
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isFirst) {
                    isFirst = false; return
                }
                val isDarkMode = position == 1
                saveTheme(isDarkMode)
                applyTheme(isDarkMode)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun applyTheme(isDark: Boolean) {
        val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun saveTheme(isDark: Boolean) {
        requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit { putBoolean("dark_mode", isDark) }
    }

    private fun getSavedTheme(): Boolean {
        return requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getBoolean("dark_mode", false)
    }


    private fun applyLanguage(langCode: String) {
        saveLanguage(langCode)
        requireActivity().recreate()
    }

    private fun saveLanguage(langCode: String) {
        requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit { putString("language", langCode) }
    }

    private fun getSavedLanguage(): String {
        return requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("language", "en") ?: "en"
    }

}