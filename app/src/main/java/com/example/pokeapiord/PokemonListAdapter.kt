package com.example.pokeapiord

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class Pokemon(val name: String, val imageUrl: String?)

class PokemonListAdapter(private val pokemonList: List<Pokemon>) :
    RecyclerView.Adapter<PokemonListAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pokemonName: TextView = itemView.findViewById(R.id.pokemon_name)
        val pokemonImage: ImageView = itemView.findViewById(R.id.pokemon_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val currentPokemon = pokemonList[position]
        holder.pokemonName.text = currentPokemon.name
        if (currentPokemon.imageUrl != null) {
            Log.d("PokeAPI", "Loading image for ${currentPokemon.name} from URL: ${currentPokemon.imageUrl}")
            Glide.with(holder.itemView.context)
                .load(currentPokemon.imageUrl)
                .placeholder(R.drawable.default_pokemon_image_background)
                .error(R.drawable.default_pokemon_image_background)
                .into(holder.pokemonImage)
        } else {
            holder.pokemonImage.setImageResource(R.drawable.default_pokemon_image_background)
        }
    }

    override fun getItemCount() = pokemonList.size
}
