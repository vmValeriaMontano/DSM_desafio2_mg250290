package com.example.desafio2_dsm

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()

        val btnSignUp = findViewById<Button>(R.id.singUpButton)
        val btnLogin = findViewById<Button>(R.id.loginButton)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        setup(
            emailEditText,
            passwordEditText,
            btnSignUp,
            btnLogin
        )
    }

    private fun setup(
        emailField: EditText,
        passwordField: EditText,
        btnSignUp: Button,
        btnLogin: Button
    ) {

        title = getString(R.string.login)

        btnSignUp.setOnClickListener {

            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (!validateFields(email, password)) {
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        showHome(
                            task.result?.user?.email ?: email,
                            ProviderType.BASIC
                        )

                    } else {

                        showAlert(
                            task.exception?.message
                                ?: getString(R.string.error)
                        )
                    }
                }
        }

        btnLogin.setOnClickListener {

            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (!validateFields(email, password)) {
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        showHome(
                            task.result?.user?.email ?: email,
                            ProviderType.BASIC
                        )

                    } else {

                        showAlert(
                            task.exception?.message
                                ?: getString(R.string.error)
                        )
                    }
                }
        }
    }

    private fun validateFields(
        email: String,
        password: String
    ): Boolean {

        if (email.isEmpty() || password.isEmpty()) {

            showAlert(getString(R.string.required_fields))
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            showAlert(getString(R.string.invalid_email))
            return false
        }

        if (password.length < 6) {

            showAlert(getString(R.string.invalid_password))
            return false
        }

        return true
    }

    private fun showAlert(message: String) {

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(getString(R.string.accept), null)
            .show()
    }

    private fun showHome(
        email: String,
        provider: ProviderType
    ) {

        val homeIntent = Intent(
            this,
            HomeActivity::class.java
        ).apply {

            putExtra("email", email)
            putExtra("provider", provider.name)
        }

        startActivity(homeIntent)
        finish()
    }
}

enum class ProviderType {
    BASIC
}