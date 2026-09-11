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
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DestinationAdapter

    private val destinations = mutableListOf<Destination>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val email = intent.getStringExtra("email")
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

            startActivity(
                Intent(
                    this,
                    AddDestinationActivity::class.java
                )
            )
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
        loadDestinations()
    }

    private fun setupRecyclerView() {

        adapter = DestinationAdapter(
            destinations,
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

        recyclerView.adapter = adapter
    }

    private fun loadDestinations() {

        db.collection("destinations")
            .get()
            .addOnSuccessListener { result ->

                destinations.clear()

                for (document in result) {

                    val destination =
                        document.toObject(Destination::class.java)

                    destination.id = document.id

                    destinations.add(destination)
                }

                adapter.updateList(destinations)
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun confirmDelete(
        destination: Destination
    ) {

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.delete_message))
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

        db.collection("destinations")
            .document(destination.id)
            .delete()
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    getString(R.string.destination_deleted),
                    Toast.LENGTH_SHORT
                ).show()

                loadDestinations()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}