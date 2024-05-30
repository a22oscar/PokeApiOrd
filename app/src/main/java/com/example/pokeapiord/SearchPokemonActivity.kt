package com.example.pokeapiord

import android.content.Intent
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

    // Declarar las variables para los elementos de la interfaz
    private lateinit var editTextPokemonName: EditText
    private lateinit var btnSearch: Button
    private lateinit var imageViewPokemon: ImageView
    private lateinit var textViewPokemonName: TextView
    private lateinit var textViewPokemonHeight: TextView
    private lateinit var textViewPokemonWeight: TextView
    private lateinit var btnReturnMain: Button
    private val client = OkHttpClient()

    private lateinit var pokemonName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pokemon)

        // Enlazar las variables con los elementos de la interfaz
        editTextPokemonName = findViewById(R.id.editTextPokemonName)
        btnSearch = findViewById(R.id.btnSearch)
        imageViewPokemon = findViewById(R.id.imageViewPokemon)
        textViewPokemonName = findViewById(R.id.textViewPokemonName)
        textViewPokemonHeight = findViewById(R.id.textViewPokemonHeight)
        textViewPokemonWeight = findViewById(R.id.textViewPokemonWeight)
        btnReturnMain = findViewById(R.id.btnReturnMain)

        // Configurar el botón de búsqueda
        btnSearch.setOnClickListener {
            pokemonName = editTextPokemonName.text.toString().trim().lowercase()
            if (pokemonName.isNotEmpty()) {
                fetchPokemonData(pokemonName) // Buscar los datos del Pokémon
            }
        }

        // Configurar el botón para volver a la actividad principal
        btnReturnMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Función para buscar los datos del Pokémon
    private fun fetchPokemonData(pokemonName: String) {
        val url = "https://pokeapi.co/api/v2/pokemon/$pokemonName"
        val request = Request.Builder().url(url).build()

        // Hacer la solicitud a la API
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    textViewPokemonName.text = "Failed to load data"
                    textViewPokemonName.visibility = TextView.VISIBLE
                    imageViewPokemon.visibility = ImageView.GONE
                    textViewPokemonHeight.visibility = TextView.GONE
                    textViewPokemonWeight.visibility = TextView.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            textViewPokemonName.text = "Pokemon not found"
                            textViewPokemonName.visibility = TextView.VISIBLE
                            imageViewPokemon.visibility = ImageView.GONE
                            textViewPokemonHeight.visibility = TextView.GONE
                            textViewPokemonWeight.visibility = TextView.GONE
                        }
                        return@use
                    }

                    // Obtener los datos del Pokémon de la respuesta
                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    val pokemonName = jsonResponse.getString("name").capitalize()
                    val imageUrl = jsonResponse.getJSONObject("sprites").getString("front_default")
                    val height = jsonResponse.getInt("height")
                    val weight = jsonResponse.getInt("weight")

                    // Actualizar la interfaz con los datos del Pokémon
                    runOnUiThread {
                        textViewPokemonName.text = pokemonName
                        textViewPokemonName.visibility = TextView.VISIBLE
                        textViewPokemonHeight.text = "Height: $height"
                        textViewPokemonHeight.visibility = TextView.VISIBLE
                        textViewPokemonWeight.text = "Weight: $weight"
                        textViewPokemonWeight.visibility = TextView.VISIBLE
                        imageViewPokemon.visibility = ImageView.VISIBLE
                        Glide.with(this@SearchPokemonActivity).load(imageUrl).into(imageViewPokemon)
                    }
                }
            }
        })
    }
}
