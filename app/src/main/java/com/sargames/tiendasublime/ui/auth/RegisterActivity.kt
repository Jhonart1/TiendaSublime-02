package com.sargames.tiendasublime.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        setupUI()
    }

    private fun setupUI() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (validateInputs(name, email, password, confirmPassword)) {
                val result = dbHelper.registerUser(name, email, password)
                when (result) {
                    -2L -> {
                        binding.emailLayout.error = "Este correo ya está registrado"
                        Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                    }
                    -1L -> {
                        Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }

        binding.loginTextView.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.nameLayout.error = "El nombre es requerido"
            isValid = false
        } else {
            binding.nameLayout.error = null
        }

        if (email.isEmpty()) {
            binding.emailLayout.error = "El email es requerido"
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = "La contraseña es requerida"
            isValid = false
        } else {
            binding.passwordLayout.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordLayout.error = "Confirma tu contraseña"
            isValid = false
        } else {
            binding.confirmPasswordLayout.error = null
        }

        if (password != confirmPassword) {
            binding.confirmPasswordLayout.error = "Las contraseñas no coinciden"
            isValid = false
        }

        return isValid
    }
} 