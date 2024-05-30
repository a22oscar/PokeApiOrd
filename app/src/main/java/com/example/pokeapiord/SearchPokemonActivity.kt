package com.example.pokeapiord

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
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
    private lateinit var textViewPokemonHeight: TextView
    private lateinit var textViewPokemonWeight: TextView
    private lateinit var checkBoxFavorite: CheckBox
    private lateinit var btnReturnMain: Button
    private val client = OkHttpClient()
    private lateinit var currentPokemonName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pokemon)

        editTextPokemonName = findViewById(R.id.editTextPokemonName)
        btnSearch = findViewById(R.id.btnSearch)
        imageViewPokemon = findViewById(R.id.imageViewPokemon)
        textViewPokemonName = findViewById(R.id.textViewPokemonName)
        textViewPokemonHeight = findViewById(R.id.textViewPokemonHeight)
        textViewPokemonWeight = findViewById(R.id.textViewPokemonWeight)
        checkBoxFavorite = findViewById(R.id.checkBoxFavorite)
        btnReturnMain = findViewById(R.id.btnReturnMain)

        btnSearch.setOnClickListener {
            val pokemonName = editTextPokemonName.text.toString().trim().lowercase()
            if (pokemonName.isNotEmpty()) {
                fetchPokemonData(pokemonName)
            }
        }

        btnReturnMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        checkBoxFavorite.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addPokemonToFavorites(currentPokemonName)
            } else {
                removePokemonFromFavorites(currentPokemonName)
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
                    textViewPokemonHeight.visibility = TextView.GONE
                    textViewPokemonWeight.visibility = TextView.GONE
                    checkBoxFavorite.visibility = CheckBox.GONE
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
                            checkBoxFavorite.visibility = CheckBox.GONE
                        }
                        return@use
                    }

                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    val pokemonName = jsonResponse.getString("name").capitalize()
                    val imageUrl = jsonResponse.getJSONObject("sprites").getString("front_default")
                    val height = jsonResponse.getInt("height")
                    val weight = jsonResponse.getInt("weight")

                    runOnUiThread {
                        currentPokemonName = pokemonName
                        textViewPokemonName.text = pokemonName
                        textViewPokemonName.visibility = TextView.VISIBLE
                        textViewPokemonHeight.text = "Height: $height"
                        textViewPokemonHeight.visibility = TextView.VISIBLE
                        textViewPokemonWeight.text = "Weight: $weight"
                        textViewPokemonWeight.visibility = TextView.VISIBLE
                        imageViewPokemon.visibility = ImageView.VISIBLE
                        checkBoxFavorite.visibility = CheckBox.VISIBLE
                        Glide.with(this@SearchPokemonActivity).load(imageUrl).into(imageViewPokemon)

                        // Comprobar si el Pokémon está en favoritos
                        checkBoxFavorite.isChecked = isPokemonFavorite(pokemonName)
                    }
                }
            }
        })
    }

    private fun saveNamesList(names: List<String>) {
        val namesSet = names.toSet()
        val sharedPreferences = getSharedPreferences("PokeAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("names", namesSet)
        editor.apply()
    }

    private fun loadNamesList(): List<String> {
        val sharedPreferences = getSharedPreferences("PokeAppPrefs", MODE_PRIVATE)
        val namesSet = sharedPreferences.getStringSet("names", setOf())
        return namesSet?.toList() ?: listOf()
    }

    private fun addPokemonToFavorites(name: String) {
        val names = loadNamesList().toMutableList()
        if (!names.contains(name)) {
            names.add(name)
            saveNamesList(names)
        }
    }

    private fun removePokemonFromFavorites(name: String) {
        val names = loadNamesList().toMutableList()
        if (names.contains(name)) {
            names.remove(name)
            saveNamesList(names)
        }
    }

    private fun isPokemonFavorite(name: String): Boolean {
        val names = loadNamesList()
        return names.contains(name)
    }
}
