package com.sargames.tiendasublime.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sargames.tiendasublime.ui.auth.LoginActivity
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.data.SharedPreferencesManager
import com.sargames.tiendasublime.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPrefs: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        sharedPrefs = SharedPreferencesManager(requireContext())
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
        setupUI()
        loadUserData()
        
        binding.btnShowLocation.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profileFragment_to_mapFragment)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al navegar al mapa: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI() {
        binding.btnLogout.setOnClickListener {
            sharedPrefs.clearUserSession()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.btnAdmin.setOnClickListener {
            showDatabaseInfo()
        }
    }

    private fun showDatabaseInfo() {
        try {
            // Obtener información de usuarios
            val users = dbHelper.getAllUsers()
            Log.d("DB_INFO", "=== TABLA USUARIOS ===")
            users.forEach { user ->
                Log.d("DB_INFO", "ID: ${user.id}, Email: ${user.email}, Nombre: ${user.name}")
            }

            // Obtener información de productos
            val products = dbHelper.getAllProducts()
            Log.d("DB_INFO", "\n=== TABLA PRODUCTOS ===")
            products.forEach { product ->
                Log.d("DB_INFO", "ID: ${product.id}, Nombre: ${product.name}, Precio: ${product.price}, Categoría: ${product.category}")
            }

            // Obtener información del carrito
            val cartItems = dbHelper.getCartItems(sharedPrefs.getUserEmail() ?: "")
            Log.d("DB_INFO", "\n=== TABLA CARRITO ===")
            cartItems.forEach { item ->
                Log.d("DB_INFO", "Producto: ${item.name}, Cantidad: ${item.quantity}, Precio: ${item.price}")
            }

            Toast.makeText(requireContext(), "Información mostrada en el log", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("DB_INFO", "Error al obtener información de la base de datos", e)
            Toast.makeText(requireContext(), "Error al obtener información", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        if (sharedPrefs.isUserLoggedIn()) {
            val userEmail = sharedPrefs.getUserEmail()
            if (userEmail != null) {
                val userName = dbHelper.getUserName(userEmail)
                binding.tvUserName.text = userName ?: "Usuario"
                binding.tvUserEmail.text = userEmail
            }
        } else {
            // Si no hay usuario logueado, redirigir al login
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 