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
    // ML Kit GenAI Prompt API for Gemini Nano Access
    implementation("com.google.mlkit:genai-prompt:1.0.0-alpha1")

    // CameraX for camera capture
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // Standard UI and Lifecycle
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
}
```
> Note: ML Kit GenAI Prompt API is currently published as `1.0.0-alpha1`. Update to the stable release when available.

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

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.genai.*
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var resultText: TextView
    private lateinit var previewView: PreviewView
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imageCapture: ImageCapture? = null

    private val generativeModel by lazy { Generation.getClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.resultText)
        previewView = findViewById(R.id.previewView)
        val readButton = findViewById<Button>(R.id.readButton)

        readButton.setOnClickListener { captureAndAnalyze() }

        // Request camera permission and start camera
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED -> startCamera()
            else -> requestCameraPermission()
        }
    }

    private fun captureAndAnalyze() {
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = convertImageToBitmap(image)
                    image.close()
                    readTimeFromWatch(bitmap)
                }
            }
        )
    }

    private fun readTimeFromWatch(bitmap: Bitmap) {
        resultText.text = "Thinking..."

        val prompt = "Analyze this analog watch. What time is shown? Be precise. Return only HH:mm."

        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(
                    generateContentRequest(ImagePart(bitmap), TextPart(prompt)) {
                        temperature = 0.2f
                        topK = 10
                    }
                )
                resultText.text = "Time: ${response.text}"
            } catch (e: Exception) {
                resultText.text = "Error: ${e.localizedMessage}"
            }
        }
    }
}
```

### 4) Layout note
The layout includes a `PreviewView` for camera preview and a button to capture and analyze. The app uses CameraX for camera functionality and ML Kit GenAI Prompt API for on-device image analysis with Gemini Nano.

## 🧪 Build & Run
1. Clone the repo.
2. Open in Android Studio Ladybug+.
3. Sync Gradle, then Build & Run on a supported physical device.
4. On first run, AICore may take a moment to initialize the local Gemini Nano model.
