package com.example.desafio2_dsm

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->

        if (isGranted) {
            selectImage()
        } else {
            Toast.makeText(
                this,
                "Necesitas permitir el acceso a las imágenes",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private var currentImageUrl = ""

    private var currentImageBase64 = ""

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

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_edit_destination
        )

        database =
            FirebaseDatabase.getInstance()

        destinationId =
            intent.getStringExtra(
                "destinationId"
            )
                ?: run {

                    showError(
                        "No se encontró el ID del destino."
                    )

                    finish()

                    return
                }

        nameEditText =
            findViewById(
                R.id.nameEditText
            )

        countrySpinner =
            findViewById(
                R.id.countrySpinner
            )

        priceEditText =
            findViewById(
                R.id.priceEditText
            )

        descriptionEditText =
            findViewById(
                R.id.descriptionEditText
            )

        previewImageView =
            findViewById(
                R.id.previewImageView
            )

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
            pedirPermisoGaleria()
        }

        updateButton.setOnClickListener {
            updateDestination()
        }
    }

    private fun pedirPermisoGaleria() {

        val permiso = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (
            ContextCompat.checkSelfPermission(
                this,
                permiso
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissionLauncher.launch(permiso)

        } else {

            selectImage()
        }
    }

    private fun setupSpinner() {

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                countries
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        countrySpinner.adapter =
            adapter
    }

    private fun loadDestination() {

        database
            .getReference(
                "destinations"
            )
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

                nameEditText.setText(
                    destination.name
                )

                priceEditText.setText(
                    destination.price.toString()
                )

                descriptionEditText.setText(
                    destination.description
                )

                currentImageUrl =
                    destination.imageUrl

                currentImageBase64 =
                    destination.imageBase64

                /*
                 * Primero intentamos mostrar la imagen
                 * Base64.
                 */
                if (
                    currentImageBase64.isNotEmpty()
                ) {

                    val bitmap =
                        ImageUtils.base64ToBitmap(
                            currentImageBase64
                        )

                    if (bitmap != null) {

                        previewImageView
                            .setImageBitmap(
                                bitmap
                            )
                    }
                }
                else if (
                    currentImageUrl.isNotEmpty()
                ) {

                    Glide.with(this)
                        .load(currentImageUrl)
                        .placeholder(
                            android.R.drawable.ic_menu_gallery
                        )
                        .error(
                            android.R.drawable.ic_menu_gallery
                        )
                        .into(
                            previewImageView
                        )
                }

                val countryIndex =
                    countries.indexOf(
                        destination.country
                    )

                if (countryIndex >= 0) {

                    countrySpinner
                        .setSelection(
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

            imageUri =
                data?.data

            if (imageUri != null) {

                previewImageView
                    .setImageURI(
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

        if (description.length < 20) {

            showError(
                getString(
                    R.string.short_description
                )
            )

            return
        }
        val newImageBase64: String

        if (imageUri != null) {

            val converted =
                ImageUtils.uriToBase64(
                    this,
                    imageUri!!
                )

            if (
                converted == null ||
                converted.isEmpty()
            ) {

                showError(
                    "No se pudo procesar la nueva imagen."
                )

                return
            }

            newImageBase64 =
                converted

        } else {

            newImageBase64 =
                currentImageBase64
        }

        saveChanges(
            name = name,
            country = country,
            price = price,
            description = description,
            imageBase64 = newImageBase64
        )
    }

    private fun saveChanges(
        name: String,
        country: String,
        price: Double,
        description: String,
        imageBase64: String
    ) {

        val updates =
            hashMapOf<String, Any>(
                "name" to name,
                "country" to country,
                "price" to price,
                "description" to description,
                "imageBase64" to imageBase64,
                "imageUrl" to ""
            )

        database
            .getReference(
                "destinations"
            )
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