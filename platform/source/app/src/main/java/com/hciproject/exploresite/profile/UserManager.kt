package com.hciproject.exploresite.profile

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

object UserManager {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER = "current_user"
    private const val KEY_USERS_LIST = "users_list"
    private const val KEY_GLOBAL_LOCATION = "global_location_enabled"
    private val gson = Gson()

    fun saveCurrentUser(context: Context, user: User?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        val persistedUser = if (user?.profileImageUri != null && user.profileImageUri.startsWith("content://")) {
            val internalUri = copyImageToInternalStorage(context, Uri.parse(user.profileImageUri))
            user.copy(profileImageUri = internalUri)
        } else {
            user
        }

        val userJson = if (persistedUser != null) gson.toJson(persistedUser) else null
        prefs.edit().putString(KEY_USER, userJson).apply()
        CurrentUser = persistedUser
        
        if (persistedUser != null) {
            updateUserInList(context, persistedUser)
        }
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USER).apply()
        CurrentUser = null
    }

    fun deleteAccount(context: Context, user: User) {
        val users = getAllUsers(context).toMutableList()
        users.removeAll { it.email == user.email }
        saveUsersList(context, users)
        if (CurrentUser?.email == user.email) {
            logout(context)
        }
    }

    fun loadCurrentUser(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userJson = prefs.getString(KEY_USER, null)
        if (userJson != null) {
            try {
                CurrentUser = gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                // If data structure changed and causes incompatibility, clear the corrupted state
                prefs.edit().remove(KEY_USER).apply()
                CurrentUser = null
            }
        }
    }

    private fun updateUserInList(context: Context, user: User) {
        val users = getAllUsers(context).toMutableList()
        val index = users.indexOfFirst { it.email == user.email }
        if (index != -1) {
            users[index] = user
        } else {
            users.add(user)
        }
        saveUsersList(context, users)
    }

    fun getAllUsers(context: Context): List<User> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val usersJson = prefs.getString(KEY_USERS_LIST, null)
        return if (usersJson != null) {
            try {
                gson.fromJson(usersJson, Array<User>::class.java).toList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    private fun saveUsersList(context: Context, users: List<User>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val usersJson = gson.toJson(users)
        prefs.edit().putString(KEY_USERS_LIST, usersJson).apply()
    }

    private fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun registerUser(context: Context, user: User): Boolean {
        val users = getAllUsers(context).toMutableList()
        if (users.any { it.email == user.email }) {
            return false // User already exists
        }
        
        // Ensure the password is encrypted before storing
        val encryptedUser = user.copy(password = User.encryptPassword(user.password))
        
        val persistedUser = if (encryptedUser.profileImageUri != null && encryptedUser.profileImageUri.startsWith("content://")) {
            val internalUri = copyImageToInternalStorage(context, Uri.parse(encryptedUser.profileImageUri))
            encryptedUser.copy(profileImageUri = internalUri)
        } else {
            encryptedUser
        }
        
        users.add(persistedUser)
        saveUsersList(context, users)
        return true
    }

    fun login(context: Context, email: String, password: String): Boolean {
        val users = getAllUsers(context)
        val encryptedInputPassword = User.encryptPassword(password)
        val user = users.find { it.email == email && it.password == encryptedInputPassword }
        return if (user != null) {
            saveCurrentUser(context, user)
            true
        } else {
            false
        }
    }

    // Global settings for non-logged-in users
    fun isGlobalLocationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_GLOBAL_LOCATION, true)
    }

    fun setGlobalLocationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_GLOBAL_LOCATION, enabled).apply()
    }
}
