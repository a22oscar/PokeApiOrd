package com.example.pokeapiord

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pokeapiord.data.Pokemon
import kotlinx.android.synthetic.main.activity_search_pokemon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchPokemonActivity : AppCompatActivity() {

    private lateinit var db: PokemonDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pokemon)

        db = PokemonDatabase.getDatabase(this)

        btn_search.setOnClickListener {
            val name = et_pokemon_name.text.toString().trim().toLowerCase()
            if (name.isNotEmpty()) {
                searchPokemon(name)
            } else {
                Toast.makeText(this, "Please enter a Pokemon name", Toast.LENGTH_SHORT).show()
            }
        }

        btn_add_to_pokedex.setOnClickListener {
            val name = et_pokemon_name.text.toString().trim().toLowerCase()
            if (name.isNotEmpty()) {
                addPokemonToPokedex(name)
            } else {
                Toast.makeText(this, "Please enter a Pokemon name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchPokemon(name: String) {
        val apiService = PokemonApiService.create()
        apiService.getPokemon(name).enqueue(object : Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                if (response.isSuccessful && response.body() != null) {
                    val pokemon = response.body()!!
                    displayPokemonInfo(pokemon)
                } else {
                    Toast.makeText(this@SearchPokemonActivity, "Pokemon not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                Toast.makeText(this@SearchPokemonActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayPokemonInfo(pokemon: Pokemon) {
        iv_pokemon_image.visibility = android.view.View.VISIBLE
        tv_pokemon_info.visibility = android.view.View.VISIBLE
        btn_add_to_pokedex.visibility = android.view.View.VISIBLE

        tv_pokemon_info.text = "Name: ${pokemon.name}\nHeight: ${pokemon.height}\nWeight: ${pokemon.weight}"
        Glide.with(this).load(pokemon.sprites.frontDefault).into(iv_pokemon_image)
    }

    private fun addPokemonToPokedex(name: String) {
        val apiService = PokemonApiService.create()
        apiService.getPokemon(name).enqueue(object : Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                if (response.isSuccessful && response.body() != null) {
                    val pokemon = response.body()!!
                    db.pokemonDao().insertPokemon(pokemon)
                    Toast.makeText(this@SearchPokemonActivity, "Pokemon added to Pokedex", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SearchPokemonActivity, "Pokemon not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                Toast.makeText(this@SearchPokemonActivity, "Error adding to Pokedex", Toast.LENGTH_SHORT).show()
            }
        })
    }
}