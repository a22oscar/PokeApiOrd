package com.example.pokeapiord

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ViewPokemonListActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pokemon_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPokemonList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Cargar la lista de Pokémon en segundo plano
        loadPokemonList(recyclerView)
    }

    private fun loadPokemonList(recyclerView: RecyclerView) {
        val sharedPreferences = getSharedPreferences("PokeAppPrefs", MODE_PRIVATE)
        val namesSet = sharedPreferences.getStringSet("names", setOf())
        val namesList = namesSet?.toList() ?: listOf()

        val pokemonList = mutableListOf<Pokemon>()
        val remainingRequests = namesList.size

        for (name in namesList) {
            fetchPokemonData(name) { pokemon ->
                val pokemonToAdd = pokemon ?: Pokemon(name.capitalize(), null)
                pokemonList.add(pokemonToAdd)
                if (pokemonList.size == remainingRequests) {
                    runOnUiThread {
                        val adapter = PokemonListAdapter(pokemonList)
                        recyclerView.adapter = adapter
                    }
                }
            }
        }
    }

    private fun fetchPokemonData(pokemonName: String, callback: (Pokemon?) -> Unit) {
        val url = "https://pokeapi.co/api/v2/pokemon/$pokemonName"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback(null)
                        return
                    }

                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val imageUrl = jsonResponse.getJSONObject("sprites").getString("front_default")
                            val pokemon = Pokemon(pokemonName.capitalize(), imageUrl)
                            callback(pokemon)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
            }
        })
    }
}

