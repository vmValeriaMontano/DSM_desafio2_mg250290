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
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class EditDestinationActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    private lateinit var nameEditText: EditText
    private lateinit var countrySpinner: Spinner
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var previewImageView: ImageView

    private var imageUri: Uri? = null

    private var currentImageUrl = ""

    private lateinit var destinationId: String

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
        setContentView(R.layout.activity_edit_destination)

        // Firebase Realtime Database
        database = FirebaseDatabase.getInstance()

        destinationId =
            intent.getStringExtra("destinationId")
                ?: run {
                    showError("No se encontró el ID del destino.")
                    finish()
                    return
                }

        nameEditText =
            findViewById(R.id.nameEditText)

        countrySpinner =
            findViewById(R.id.countrySpinner)

        priceEditText =
            findViewById(R.id.priceEditText)

        descriptionEditText =
            findViewById(R.id.descriptionEditText)

        previewImageView =
            findViewById(R.id.previewImageView)

        val selectImageButton =
            findViewById<Button>(
                R.id.selectImageButton
            )

        val updateButton =
            findViewById<Button>(
                R.id.updateButton
            )

        setupSpinner()

        loadDestination()

        selectImageButton.setOnClickListener {
            selectImage()
        }

        updateButton.setOnClickListener {
            updateDestination()
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

    private fun loadDestination() {

        database
            .getReference("destinations")
            .child(destinationId)
            .get()

            .addOnSuccessListener { snapshot ->

                if (!snapshot.exists()) {

                    showError(
                        "El destino ya no existe."
                    )

                    return@addOnSuccessListener
                }

                val destination =
                    snapshot.getValue(
                        Destination::class.java
                    )

                if (destination == null) {

                    showError(
                        "No se pudieron leer los datos del destino."
                    )

                    return@addOnSuccessListener
                }

                // Nombre
                nameEditText.setText(
                    destination.name
                )

                // Precio
                priceEditText.setText(
                    destination.price.toString()
                )

                // Descripción
                descriptionEditText.setText(
                    destination.description
                )

                // Imagen actual
                currentImageUrl =
                    destination.imageUrl

                if (currentImageUrl.isNotEmpty()) {

                    Glide.with(this)
                        .load(currentImageUrl)
                        .placeholder(
                            android.R.drawable.ic_menu_gallery
                        )
                        .error(
                            android.R.drawable.ic_menu_gallery
                        )
                        .into(previewImageView)
                }

                // País
                val countryIndex =
                    countries.indexOf(
                        destination.country
                    )

                if (countryIndex >= 0) {

                    countrySpinner.setSelection(
                        countryIndex
                    )
                }
            }

            .addOnFailureListener { exception ->

                showError(
                    exception.message
                        ?: "No se pudo cargar el destino."
                )
            }
    }

    private fun selectImage() {

        val intent =
            Intent(Intent.ACTION_PICK)

        intent.type = "image/*"

        startActivityForResult(
            intent,
            200
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
            requestCode == 200 &&
            resultCode == RESULT_OK
        ) {

            imageUri = data?.data

            if (imageUri != null) {

                previewImageView.setImageURI(
                    imageUri
                )
            }
        }
    }

    private fun updateDestination() {

        val name =
            nameEditText.text
                .toString()
                .trim()

        val country =
            countrySpinner
                .selectedItem
                .toString()

        val priceText =
            priceEditText.text
                .toString()
                .trim()

        val description =
            descriptionEditText.text
                .toString()
                .trim()

        // Validar campos
        if (
            name.isEmpty() ||
            priceText.isEmpty() ||
            description.isEmpty() ||
            country == countries[0]
        ) {

            showError(
                getString(
                    R.string.required_fields
                )
            )

            return
        }

        // Validar precio
        val price =
            priceText.toDoubleOrNull()

        if (
            price == null ||
            price <= 0
        ) {

            showError(
                getString(
                    R.string.invalid_price
                )
            )

            return
        }

        // Validar descripción
        if (description.length < 20) {

            showError(
                getString(
                    R.string.short_description
                )
            )

            return
        }

        /*
         * IMPORTANTE:
         *
         * No usamos Firebase Storage.
         *
         * Si el usuario NO seleccionó una imagen nueva,
         * conservamos la imagen que ya tenía.
         *
         * Si seleccionó una imagen nueva, guardamos
         * temporalmente su URI local.
         */

        val imageToSave =
            imageUri?.toString()
                ?: currentImageUrl

        saveChanges(
            name = name,
            country = country,
            price = price,
            description = description,
            imageUrl = imageToSave
        )
    }

    private fun saveChanges(
        name: String,
        country: String,
        price: Double,
        description: String,
        imageUrl: String
    ) {

        val updates =
            hashMapOf<String, Any>(
                "name" to name,
                "country" to country,
                "price" to price,
                "description" to description,
                "imageUrl" to imageUrl
            )

        database
            .getReference("destinations")
            .child(destinationId)
            .updateChildren(updates)

            .addOnSuccessListener {

                AlertDialog.Builder(this)

                    .setTitle(
                        getString(
                            R.string.success
                        )
                    )

                    .setMessage(
                        getString(
                            R.string.destination_updated
                        )
                    )

                    .setPositiveButton(
                        getString(
                            R.string.accept
                        )
                    ) { _, _ ->

                        finish()
                    }

                    .show()
            }

            .addOnFailureListener { exception ->

                showError(
                    exception.message
                        ?: "No se pudo actualizar el destino."
                )
            }
    }

    private fun showError(
        message: String
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                getString(
                    R.string.error
                )
            )

            .setMessage(message)

            .setPositiveButton(
                getString(
                    R.string.accept
                ),
                null
            )

            .show()
    }
}