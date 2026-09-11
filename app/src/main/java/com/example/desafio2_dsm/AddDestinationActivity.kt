
package com.example.desafio2_dsm

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class AddDestinationActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    private lateinit var nameEditText: EditText
    private lateinit var countrySpinner: Spinner
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var previewImageView: ImageView

    private var imageUri: Uri? = null

    private val countries = listOf(
        "Selecciona un país",
        "México",
        "El Salvador",
        "Guatemala",
        "Costa Rica",
        "Colombia",
        "España",
        "Francia",
        "Italia",
        "Japón",
        "Estados Unidos"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_add_destination)

        // Realtime Database
        database = FirebaseDatabase.getInstance()

        nameEditText = findViewById(R.id.nameEditText)
        countrySpinner = findViewById(R.id.countrySpinner)
        priceEditText = findViewById(R.id.priceEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        previewImageView = findViewById(R.id.previewImageView)

        val selectImageButton =
            findViewById<Button>(R.id.selectImageButton)

        val saveButton =
            findViewById<Button>(R.id.saveButton)

        setupSpinner()

        selectImageButton.setOnClickListener {
            selectImage()
        }

        saveButton.setOnClickListener {
            saveDestination()
        }
    }

    private fun setupSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            countries
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        countrySpinner.adapter = adapter
    }

    private fun selectImage() {

        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"

        startActivityForResult(
            intent,
            100
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == 100 &&
            resultCode == RESULT_OK
        ) {

            imageUri = data?.data

            if (imageUri != null) {
                previewImageView.setImageURI(imageUri)
            }
        }
    }

    private fun saveDestination() {

        val name =
            nameEditText.text.toString().trim()

        val country =
            countrySpinner.selectedItem.toString()

        val priceText =
            priceEditText.text.toString().trim()

        val description =
            descriptionEditText.text.toString().trim()

        // Validar campos
        if (
            name.isEmpty() ||
            priceText.isEmpty() ||
            description.isEmpty() ||
            country == countries[0]
        ) {

            showError(
                getString(R.string.required_fields)
            )

            return
        }

        // Validar precio
        val price = priceText.toDoubleOrNull()

        if (price == null || price <= 0) {

            showError(
                getString(R.string.invalid_price)
            )

            return
        }

        // Validar descripción
        if (description.length < 20) {

            showError(
                getString(R.string.short_description)
            )

            return
        }

        // Validar imagen
        if (imageUri == null) {

            showError(
                getString(R.string.required_image)
            )

            return
        }

        saveToRealtimeDatabase(
            name = name,
            country = country,
            price = price,
            description = description,
            imageUrl = imageUri.toString()
        )
    }

    private fun saveToRealtimeDatabase(
        name: String,
        country: String,
        price: Double,
        description: String,
        imageUrl: String
    ) {

        // Referencia:
        // Realtime Database
        // destinations/
        val destinationsRef =
            database.getReference("destinations")

        // Generar ID automáticamente
        val id =
            destinationsRef.push().key

        if (id == null) {

            showError(
                "No se pudo generar el ID del destino."
            )

            return
        }

        // Crear objeto Destination
        val destination = Destination(
            id = id,
            name = name,
            country = country,
            price = price,
            description = description,
            imageUrl = imageUrl
        )

        // Guardar en Realtime Database
        destinationsRef
            .child(id)
            .setValue(destination)
            .addOnSuccessListener {

                AlertDialog.Builder(this)
                    .setTitle(
                        getString(R.string.success)
                    )
                    .setMessage(
                        getString(
                            R.string.destination_saved
                        )
                    )
                    .setPositiveButton(
                        getString(R.string.accept)
                    ) { _, _ ->
                        finish()
                    }
                    .show()
            }
            .addOnFailureListener { exception ->

                showError(
                    exception.message
                        ?: getString(R.string.error)
                )
            }
    }

    private fun showError(
        message: String
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                getString(R.string.error)
            )
            .setMessage(message)
            .setPositiveButton(
                getString(R.string.accept),
                null
            )
            .show()
    }
}
