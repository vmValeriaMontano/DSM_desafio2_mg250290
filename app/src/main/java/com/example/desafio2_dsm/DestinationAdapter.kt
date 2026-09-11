package com.example.desafio2_dsm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DestinationAdapter(
    private var destinations: MutableList<Destination>,
    private val onEditClick: (Destination) -> Unit,
    private val onDeleteClick: (Destination) -> Unit
) : RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

    class DestinationViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView =
            itemView.findViewById(R.id.destinationImageView)

        val name: TextView =
            itemView.findViewById(R.id.destinationNameTextView)

        val country: TextView =
            itemView.findViewById(R.id.countryTextView)

        val price: TextView =
            itemView.findViewById(R.id.priceTextView)

        val description: TextView =
            itemView.findViewById(R.id.descriptionTextView)

        val editButton: Button =
            itemView.findViewById(R.id.btnEditar)

        val deleteButton: Button =
            itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinationViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_destination,
                parent,
                false
            )

        return DestinationViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DestinationViewHolder,
        position: Int
    ) {

        val destination = destinations[position]

        holder.name.text = destination.name
        holder.country.text = destination.country
        holder.price.text =
            "$${String.format("%.2f", destination.price)}"

        holder.description.text =
            destination.description

        Glide.with(holder.itemView.context)
            .load(destination.imageUrl)
            .into(holder.image)

        holder.editButton.setOnClickListener {
            onEditClick(destination)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(destination)
        }
    }

    override fun getItemCount(): Int {
        return destinations.size
    }

    fun updateList(
        newList: List<Destination>
    ) {

        destinations.clear()
        destinations.addAll(newList)
        notifyDataSetChanged()
    }
}