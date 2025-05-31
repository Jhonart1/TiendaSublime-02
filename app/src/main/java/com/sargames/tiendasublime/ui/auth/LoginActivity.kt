package com.sargames.tiendasublime.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sargames.tiendasublime.MainActivity
import com.sargames.tiendasublime.R
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.data.SharedPreferencesManager
import com.sargames.tiendasublime.databinding.ActivityLoginBinding
import com.sargames.tiendasublime.utils.LocationManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPrefs: SharedPreferencesManager
    private lateinit var locationManager: LocationManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sharedPrefs = SharedPreferencesManager(this)
        locationManager = LocationManager(this)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Cerrar sesión de Google al iniciar la actividad
        signOutGoogle()

        requestLocationPermission()
        setupUI()
    }

    private fun signOutGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            // Limpiar datos de sesión local
            sharedPrefs.clearUserSession()
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Se requiere permiso de ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation() {
        locationManager.getCurrentLocation { location ->
            if (location != null) {
                // La ubicación ya se muestra en el log desde LocationManager
            }
        }
    }

    private fun setupUI() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.loginUser(email, password)) {
                sharedPrefs.saveUserEmail(email)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        // Asegurarse de que no hay sesión activa
        signOutGoogle()
        
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Inicio de sesión exitoso
            account?.let {
                // Guardar información del usuario
                val email = it.email ?: ""
                val name = it.displayName ?: "Usuario Google"
                
                // Verificar si el usuario ya existe en la base de datos
                if (!dbHelper.userExists(email)) {
                    // Crear nuevo usuario en la base de datos
                    dbHelper.registerUser(name, email, "google_password")
                }

                // Guardar sesión
                sharedPrefs.saveUserEmail(email)
                
                // Ir a la actividad principal
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Error al iniciar sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        // Verificar si el usuario ya ha iniciado sesión
        if (sharedPrefs.isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
} 