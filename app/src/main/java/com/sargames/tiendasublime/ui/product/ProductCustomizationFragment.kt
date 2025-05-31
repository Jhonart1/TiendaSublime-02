package com.sargames.tiendasublime.ui.product

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.data.SharedPreferencesManager
import com.sargames.tiendasublime.databinding.FragmentProductCustomizationBinding
import com.sargames.tiendasublime.utils.ImageCaptureManager
import com.sargames.tiendasublime.utils.PermissionUtils

class ProductCustomizationFragment : Fragment() {
    private var _binding: FragmentProductCustomizationBinding? = null
    private val binding get() = _binding!!
    private val args: ProductCustomizationFragmentArgs by navArgs()
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPrefs: SharedPreferencesManager
    private lateinit var imageCaptureManager: ImageCaptureManager

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            launchCamera()
        } else {
            Toast.makeText(context, "Se requieren permisos de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            launchGallery()
        } else {
            Toast.makeText(context, "Se requieren permisos de almacenamiento", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageCaptureManager.getCurrentPhotoPath()?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                binding.ivCustomImage.setImageBitmap(bitmap)
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                binding.ivCustomImage.setImageURI(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductCustomizationBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())
        sharedPrefs = SharedPreferencesManager(requireContext())
        imageCaptureManager = ImageCaptureManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Aquí cargarías los datos iniciales usando args.productId
        setupChipGroup()
    }

    private fun setupChipGroup() {
        binding.chipGroupThemes.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.chipAll.id -> {
                    // Mostrar todos los diseños
                }
                binding.chipBirthday.id -> {
                    // Filtrar diseños de cumpleaños
                }
                binding.chipHolidays.id -> {
                    // Filtrar diseños festivos
                }
                binding.chipSpecial.id -> {
                    // Filtrar diseños especiales
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnUploadImage.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnAddToCart.setOnClickListener {
            if (!sharedPrefs.isUserLoggedIn()) {
                Toast.makeText(context, "Debes iniciar sesión para agregar productos al carrito", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userEmail = sharedPrefs.getUserEmail()
            if (userEmail != null) {
                // Agregar al carrito
                if (dbHelper.addToCart(userEmail, args.productId, 1)) {
                    Toast.makeText(context, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
                    // Navegar de vuelta a la pantalla anterior
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(context, "Error al agregar el producto al carrito", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Cámara", "Galería")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar imagen")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndLaunch()
                    1 -> checkStoragePermissionAndLaunch()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            PermissionUtils.hasCameraPermission(requireContext()) -> {
                launchCamera()
            }
            else -> {
                requestCameraPermissionLauncher.launch(PermissionUtils.getCameraPermissions())
            }
        }
    }

    private fun checkStoragePermissionAndLaunch() {
        when {
            PermissionUtils.hasStoragePermission(requireContext()) -> {
                launchGallery()
            }
            else -> {
                requestStoragePermissionLauncher.launch(PermissionUtils.getStoragePermissions())
            }
        }
    }

    private fun launchCamera() {
        try {
            takePictureLauncher.launch(imageCaptureManager.getCameraIntent())
        } catch (e: Exception) {
            Toast.makeText(context, "Error al abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchGallery() {
        try {
            pickImageLauncher.launch(imageCaptureManager.getGalleryIntent())
        } catch (e: Exception) {
            Toast.makeText(context, "Error al abrir la galería", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 