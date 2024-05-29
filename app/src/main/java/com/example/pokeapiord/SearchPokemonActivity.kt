package com.example.pokeapiord

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SearchPokemonActivity : AppCompatActivity() {

    private lateinit var editTextPokemonName: EditText
    private lateinit var btnSearch: Button
    private lateinit var imageViewPokemon: ImageView
    private lateinit var textViewPokemonName: TextView
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pokemon)

        editTextPokemonName = findViewById(R.id.editTextPokemonName)
        btnSearch = findViewById(R.id.btnSearch)
        imageViewPokemon = findViewById(R.id.imageViewPokemon)
        textViewPokemonName = findViewById(R.id.textViewPokemonName)

        btnSearch.setOnClickListener {
            val pokemonName = editTextPokemonName.text.toString().trim().lowercase()
            if (pokemonName.isNotEmpty()) {
                fetchPokemonData(pokemonName)
            }
        }
    }

    private fun fetchPokemonData(pokemonName: String) {
        val url = "https://pokeapi.co/api/v2/pokemon/$pokemonName"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    textViewPokemonName.text = "Failed to load data"
                    textViewPokemonName.visibility = TextView.VISIBLE
                    imageViewPokemon.visibility = ImageView.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            textViewPokemonName.text = "Pokemon not found"
                            textViewPokemonName.visibility = TextView.VISIBLE
                            imageViewPokemon.visibility = ImageView.GONE
                        }
                        return@use
                    }

                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    val pokemonName = jsonResponse.getString("name").capitalize()
                    val imageUrl = jsonResponse.getJSONObject("sprites").getString("front_default")

                    runOnUiThread {
                        textViewPokemonName.text = pokemonName
                        textViewPokemonName.visibility = TextView.VISIBLE
                        imageViewPokemon.visibility = ImageView.VISIBLE
                        Glide.with(this@SearchPokemonActivity).load(imageUrl).into(imageViewPokemon)
                    }
                }
            }
        })
    }
}
