package com.example.desafio2_dsm

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
enum class ProviderType {
    BASIC
}

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        val bundle = intent.extras
        val email = bundle?.getString("email") ?: "Invitado"
        val provider = bundle?.getString("provider") ?: "Desconocido"

        val emailTV = findViewById<TextView>(R.id.emailTextView)
        val providerTV = findViewById<TextView>(R.id.providerTextView)

        // CORREGIDO: Ahora busca el ID único de esta pantalla
        val logOutButton = findViewById<Button>(R.id.btnCerrarSesion)

        setup(emailTV, providerTV, logOutButton, email, provider)
    }

    private fun setup(
        emailTV: TextView,
        providerTV: TextView,
        logOutButton: Button,
        email: String,
        provider: String
    ) {
        title = "Inicio"

        emailTV.text = email
        providerTV.text = provider

        logOutButton.setOnClickListener {
            auth.signOut()
            finish()
        }
    }
}
