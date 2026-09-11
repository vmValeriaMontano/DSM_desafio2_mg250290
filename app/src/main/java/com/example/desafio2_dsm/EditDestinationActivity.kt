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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditDestinationActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

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

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        destinationId =
            intent.getStringExtra("destinationId")
                ?: run {
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
            findViewById<Button>(R.id.selectImageButton)

        val updateButton =
            findViewById<Button>(R.id.updateButton)

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

        db.collection("destinations")
            .document(destinationId)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    finish()
                    return@addOnSuccessListener
                }

                val destination =
                    document.toObject(
                        Destination::class.java
                    )

                if (destination != null) {

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

                    Glide.with(this)
                        .load(destination.imageUrl)
                        .into(previewImageView)

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
            }
            .addOnFailureListener { exception ->

                showError(
                    exception.message
                        ?: getString(R.string.error)
                )
            }
    }

    private fun selectImage() {

        val intent = Intent(
            Intent.ACTION_PICK
        )

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

            previewImageView.setImageURI(
                imageUri
            )
        }
    }

    private fun updateDestination() {

        val name =
            nameEditText.text.toString().trim()

        val country =
            countrySpinner.selectedItem.toString()

        val priceText =
            priceEditText.text.toString().trim()

        val description =
            descriptionEditText.text.toString().trim()

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

        val price =
            priceText.toDoubleOrNull()

        if (price == null || price <= 0) {

            showError(
                getString(R.string.invalid_price)
            )

            return
        }

        if (description.length < 20) {

            showError(
                getString(R.string.short_description)
            )

            return
        }

        if (imageUri != null) {

            uploadNewImage(
                name,
                country,
                price,
                description
            )

        } else {

            updateFirestore(
                name,
                country,
                price,
                description,
                currentImageUrl
            )
        }
    }

    private fun uploadNewImage(
        name: String,
        country: String,
        price: Double,
        description: String
    ) {

        val uri = imageUri ?: return

        val imageRef = storage.reference
            .child(
                "destinations/${System.currentTimeMillis()}.jpg"
            )

        imageRef.putFile(uri)
            .addOnSuccessListener {

                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->

                        updateFirestore(
                            name,
                            country,
                            price,
                            description,
                            downloadUri.toString()
                        )
                    }
            }
            .addOnFailureListener { exception ->

                showError(
                    exception.message
                        ?: getString(R.string.error)
                )
            }
    }

    private fun updateFirestore(
        name: String,
        country: String,
        price: Double,
        description: String,
        imageUrl: String
    ) {

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "country" to country,
            "price" to price,
            "description" to description,
            "imageUrl" to imageUrl
        )

        db.collection("destinations")
            .document(destinationId)
            .update(updates)
            .addOnSuccessListener {

                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.success))
                    .setMessage(
                        getString(
                            R.string.destination_updated
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
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(
                getString(R.string.accept),
                null
            )
            .show()
    }
}