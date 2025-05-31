package com.sargames.tiendasublime.data

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }

    fun isUserLoggedIn(): Boolean {
        return getUserEmail() != null
    }

    fun clearUserSession() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "TiendaSublimePrefs"
        private const val KEY_USER_EMAIL = "user_email"
    }
} 