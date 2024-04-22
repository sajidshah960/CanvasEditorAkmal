package com.example.canvaseditorakmal


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.canvaseditorakmal.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding :ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.eraser.setOnClickListener {
            binding.canvas.setEraserEnabled(true)
        }

        binding.disableEraser.setOnClickListener {
            binding.canvas.setEraserEnabled(false)
        }

    }
}