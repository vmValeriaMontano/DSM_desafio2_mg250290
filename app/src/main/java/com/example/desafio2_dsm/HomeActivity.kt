package com.example.desafio2_dsm

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DestinationAdapter

    private val destinations = mutableListOf<Destination>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Firebase Realtime Database
        database = FirebaseDatabase.getInstance()

        val email =
            intent.getStringExtra("email")
                ?: auth.currentUser?.email
                ?: ""

        val emailTextView =
            findViewById<TextView>(R.id.emailTextView)

        val addButton =
            findViewById<Button>(R.id.btnAgregarDestino)

        val logoutButton =
            findViewById<Button>(R.id.btnCerrarSesion)

        recyclerView =
            findViewById(R.id.recyclerViewDestinos)

        emailTextView.text = email

        setupRecyclerView()

        addButton.setOnClickListener {

            val intent = Intent(
                this,
                AddDestinationActivity::class.java
            )

            startActivity(intent)
        }

        logoutButton.setOnClickListener {

            auth.signOut()

            val intent = Intent(
                this,
                AuthActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Cada vez que regresamos de agregar/editar,
        // volvemos a consultar Firebase.
        loadDestinations()
    }

    private fun setupRecyclerView() {

        adapter = DestinationAdapter(
            destinations = destinations,

            onEditClick = { destination ->

                val intent = Intent(
                    this,
                    EditDestinationActivity::class.java
                )

                intent.putExtra(
                    "destinationId",
                    destination.id
                )

                startActivity(intent)
            },

            onDeleteClick = { destination ->

                confirmDelete(destination)
            }
        )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.setHasFixedSize(false)

        recyclerView.adapter = adapter
    }

    private fun loadDestinations() {

        val destinationsRef =
            database.getReference("destinations")

        destinationsRef.get()

            .addOnSuccessListener { snapshot ->

                // Mensaje temporal para comprobar
                // cuántos destinos encontró Firebase.
                Toast.makeText(
                    this,
                    "Destinos encontrados: ${snapshot.childrenCount}",
                    Toast.LENGTH_SHORT
                ).show()

                destinations.clear()

                for (childSnapshot in snapshot.children) {

                    try {

                        val destination =
                            childSnapshot.getValue(
                                Destination::class.java
                            )

                        if (destination != null) {

                            // Si el ID viene vacío,
                            // usamos la key de Firebase.
                            if (destination.id.isEmpty()) {

                                destination.id =
                                    childSnapshot.key ?: ""
                            }

                            destinations.add(destination)
                        }

                    } catch (exception: Exception) {

                        Toast.makeText(
                            this,
                            "Error leyendo destino: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                adapter.updateList(destinations)
            }

            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Error al cargar destinos:\n${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun confirmDelete(
        destination: Destination
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                getString(R.string.confirm_delete)
            )

            .setMessage(
                getString(R.string.delete_message)
            )

            .setNegativeButton(
                getString(R.string.cancel),
                null
            )

            .setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->

                deleteDestination(destination)
            }

            .show()
    }

    private fun deleteDestination(
        destination: Destination
    ) {

        if (destination.id.isEmpty()) {

            Toast.makeText(
                this,
                "El destino no tiene un ID válido.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        database
            .getReference("destinations")
            .child(destination.id)
            .removeValue()

            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    getString(
                        R.string.destination_deleted
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                loadDestinations()
            }

            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "No se pudo eliminar:\n${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
