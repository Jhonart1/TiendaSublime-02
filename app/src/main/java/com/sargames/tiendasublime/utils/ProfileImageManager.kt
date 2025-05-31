package com.sargames.tiendasublime.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileImageManager(private val context: Context) {
    companion object {
        private const val TAG = "ProfileImageManager"
        private const val PROFILE_IMAGES_DIR = "ProfileImages"
    }

    fun saveProfileImage(bitmap: Bitmap, userEmail: String): Boolean {
        return try {
            val imageFile = getProfileImageFile(userEmail)
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            true
        } catch (e: IOException) {
            Log.e(TAG, "Error al guardar la imagen de perfil: ${e.message}")
            false
        }
    }

    fun loadProfileImage(userEmail: String): Bitmap? {
        return try {
            val imageFile = getProfileImageFile(userEmail)
            if (imageFile.exists()) {
                BitmapFactory.decodeFile(imageFile.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar la imagen de perfil: ${e.message}")
            null
        }
    }

    fun deleteProfileImage(userEmail: String): Boolean {
        return try {
            val imageFile = getProfileImageFile(userEmail)
            if (imageFile.exists()) {
                imageFile.delete()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar la imagen de perfil: ${e.message}")
            false
        }
    }

    private fun getProfileImageFile(userEmail: String): File {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val profileDir = File(picturesDir, PROFILE_IMAGES_DIR)
        if (!profileDir.exists()) {
            profileDir.mkdirs()
        }
        return File(profileDir, "profile_${userEmail.hashCode()}.jpg")
    }

    fun getImageUri(userEmail: String): Uri? {
        val imageFile = getProfileImageFile(userEmail)
        return if (imageFile.exists()) {
            Uri.fromFile(imageFile)
        } else {
            null
        }
    }
} 