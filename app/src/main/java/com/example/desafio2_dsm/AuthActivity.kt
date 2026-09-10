package com.example.desafio2_dsm

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        //Botones
        val btnSingUp = findViewById<Button>(R.id.singUpButton)
        val btnLogin = findViewById<Button>(R.id.loginButton)

        //Textos
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        auth = FirebaseAuth.getInstance()

        //setUp
        setUp(emailEditText, passwordEditText, btnSingUp, btnLogin)
    }

    private fun setUp(
        emailField: EditText,
        passwordField: EditText,
        btnSingUp: Button,
        btnLogin: Button
    ){
        title = "Autenticación"

        btnSingUp.setOnClickListener {
            if (emailField.text.isNotEmpty() && passwordField.text.isNotEmpty()){
                auth.createUserWithEmailAndPassword(emailField.text.toString(), passwordField.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        //vamos a la pantalla Homa
                        showHome(it.result?.user?.email?: "", ProviderType.BASIC)
                    }else{
                        showAlert()
                    }
                }
            }
        }

        btnLogin.setOnClickListener {
            if (emailField.text.isNotEmpty() && passwordField.text.isNotEmpty()){
                auth.signInWithEmailAndPassword(emailField.text.toString(), passwordField.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        //vamos a la pantalla Homa
                        showHome(it.result?.user?.email?: "", ProviderType.BASIC)
                    }else{
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("A ocurrido un error al autenticar usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(emailField: String, provider: ProviderType){

        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", emailField)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
    }