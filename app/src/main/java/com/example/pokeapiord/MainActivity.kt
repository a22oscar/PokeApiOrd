package com.example.pokeapiord

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearchPokemon = findViewById<Button>(R.id.btn_search_pokemon)
        btnSearchPokemon.setOnClickListener {
            val intent = Intent(this, SearchPokemonActivity::class.java)
            startActivity(intent)
        }

        val btnViewPokemonList = findViewById<Button>(R.id.btn_view_pokemon_list)
        btnViewPokemonList.setOnClickListener {
            val intent = Intent(this, ViewPokemonListActivity::class.java)
            startActivity(intent)
        }
    }
}

