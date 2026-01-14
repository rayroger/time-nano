# Watch Reader AI (Gemini Nano Edition)

A local, private Android app that uses on-device Generative AI to read the time from analog watches.

## 🚀 Features
- **100% Offline:** No data leaves the device.
- **Powered by Gemini Nano:** Uses Google's latest on-device LLM via AICore and ML Kit GenAI Prompt API.

## 📱 Requirements
This app requires a device with **Android AICore** support:
- Pixel 9, 9 Pro, 10+
- Samsung S24, S25 Ultra
- Android 15 or 16+
- Minimum SDK 31 with compile/target SDK 35 (Android 15/16).

## 🛠️ Setup (2026-ready)
The snippets below are the must-have files for an empty Views/Compose Activity project.

### 1) `app/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.yourname.watchreader"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yourname.watchreader"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // ML Kit GenAI for Gemini Nano Access
    implementation("com.google.mlkit:genai:1.0.0-beta01")

    // Standard UI and Lifecycle
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
}
```
> Note: ML Kit GenAI is currently published as `1.0.0-beta01`. Update to the stable release when available.

### 2) `AndroidManifest.xml`
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-feature android:name="android.hardware.camera" android:required="true" />

    <application
        android:label="Watch Reader AI"
        android:theme="@style/Theme.Material3.DayNight">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### 3) `MainActivity.kt`
```kotlin
package com.yourname.watchreader

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.genai.GenerativeModelClient
import com.google.mlkit.vision.genai.TextPart
import com.google.mlkit.vision.genai.BitmapPart
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var resultText: TextView

    // Initialize the Gemini Nano Client
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
            // Replace with the bitmap captured from camera
            val currentBitmap: Bitmap = obtainWatchBitmap()
            readTimeFromWatch(currentBitmap)
        }
    }

    private fun readTimeFromWatch(bitmap: Bitmap) {
        resultText.text = "Thinking..."

        val prompt = "Analyze this analog watch. What time is shown? Be precise. Return only HH:mm."
        val input = listOf(TextPart(prompt), BitmapPart(bitmap))

        lifecycleScope.launch {
            try {
                val response = modelClient.generateContent(input)
                resultText.text = "Time: ${response.text}"
            } catch (e: Exception) {
                resultText.text = "Error: ${e.localizedMessage}"
            }
        }
    }

    // TODO: Implement camera capture to provide the bitmap
    private fun obtainWatchBitmap(): Bitmap {
        throw NotImplementedError("Replace with camera capture implementation.")
    }
}
```

### 4) Layout note
Add `activity_main.xml` with at least a `Button` (`@+id/readButton`) and a `TextView` (`@+id/resultText`). Wire your camera preview/capture to supply the bitmap in `obtainWatchBitmap()`.

## 🧪 Build & Run
1. Clone the repo.
2. Open in Android Studio Ladybug+.
3. Sync Gradle, then Build & Run on a supported physical device.
4. On first run, AICore may take a moment to initialize the local Gemini Nano model.
