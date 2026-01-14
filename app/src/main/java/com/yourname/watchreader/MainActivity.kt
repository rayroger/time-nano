package com.yourname.watchreader

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.genai.BitmapPart
import com.google.mlkit.vision.genai.GenerativeModelClient
import com.google.mlkit.vision.genai.TextPart
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var resultText: TextView

    private val modelClient by lazy {
        GenerativeModelClient.builder()
            .setModelName("gemini-nano")
            .build(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.resultText)
        val readButton = findViewById<Button>(R.id.readButton)

        readButton.setOnClickListener {
            val currentBitmap: Bitmap = obtainWatchBitmap()
            readTimeFromWatch(currentBitmap)
        }
    }

    private fun readTimeFromWatch(bitmap: Bitmap) {
        resultText.text = getString(R.string.status_thinking)

        val prompt = "Analyze this analog watch. What time is shown? Be precise. Return only HH:mm."
        val input = listOf(TextPart(prompt), BitmapPart(bitmap))

        lifecycleScope.launch {
            try {
                val response = modelClient.generateContent(input)
                resultText.text = getString(R.string.time_template, response.text)
            } catch (e: Exception) {
                resultText.text = getString(R.string.error_template, e.localizedMessage)
            }
        }
    }

    private fun obtainWatchBitmap(): Bitmap {
        throw NotImplementedError("Replace with camera capture implementation.")
    }
}
