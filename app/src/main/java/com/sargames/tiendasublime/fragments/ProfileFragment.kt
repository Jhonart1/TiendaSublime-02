package com.sargames.tiendasublime.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.data.SharedPreferencesManager
import com.sargames.tiendasublime.databinding.FragmentProfileBinding
import com.sargames.tiendasublime.utils.ProfileImageManager
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPrefs: SharedPreferencesManager
    private lateinit var profileImageManager: ProfileImageManager
    private var currentPhotoPath: String? = null
    private var currentUserEmail: String? = null

    companion object {
        private const val TAG = "ProfileFragment"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_CAMERA_PERMISSION = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dbHelper = DatabaseHelper(requireContext())
        sharedPrefs = SharedPreferencesManager(requireContext())
        profileImageManager = ProfileImageManager(requireContext())
        
        currentUserEmail = sharedPrefs.getUserEmail()
        
        setupUI()
        loadUserData()
    }

    private fun setupUI() {
        binding.fabEditProfile.setOnClickListener {
            Log.d(TAG, "Botón de cámara presionado")
            requestCameraPermission()
        }

        binding.btnLogout.setOnClickListener {
            sharedPrefs.clearUserSession()
            findNavController().navigate(R.id.action_profileFragment_to_loginActivity)
        }

        binding.btnShowLocation.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_mapFragment)
        }
    }

    private fun loadUserData() {
        currentUserEmail?.let { email ->
            val userName = dbHelper.getUserName(email)
            binding.tvUserName.text = userName ?: "Usuario"
            binding.tvUserEmail.text = email

            profileImageManager.loadProfileImage(email)?.let { bitmap ->
                binding.ivProfileImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun requestCameraPermission() {
        Log.d(TAG, "Solicitando permiso de cámara")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permiso de cámara no concedido, solicitando...")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            Log.d(TAG, "Permiso de cámara ya concedido, abriendo cámara...")
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: requestCode=$requestCode")
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permiso de cámara concedido")
                    openCamera()
                } else {
                    Log.d(TAG, "Permiso de cámara denegado")
                    Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openCamera() {
        Log.d(TAG, "Intentando abrir la cámara")
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                val photoFile = createImageFile()
                val photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                Log.d(TAG, "Iniciando actividad de cámara")
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } else {
                Log.e(TAG, "No se encontró una aplicación de cámara")
                Toast.makeText(context, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al abrir la cámara: ${e.message}")
            Toast.makeText(context, "Error al abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
            Log.d(TAG, "Archivo de imagen creado: $absolutePath")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.fromFile(File(path)))
                    currentUserEmail?.let { email ->
                        if (profileImageManager.saveProfileImage(bitmap, email)) {
                            binding.ivProfileImage.setImageBitmap(bitmap)
                            Toast.makeText(context, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error al procesar la imagen: ${e.message}")
                    Toast.makeText(context, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 