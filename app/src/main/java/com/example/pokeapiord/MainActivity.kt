package com.example.pokeapiord

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnSearchPokemon = findViewById<Button>(R.id.btn_search_pokemon)
        btnSearchPokemon.setOnClickListener {
            val intent = Intent(
                this@MainActivity,
                SearchPokemonActivity::class.java
            )
            startActivity(intent)
        }
    }
}

