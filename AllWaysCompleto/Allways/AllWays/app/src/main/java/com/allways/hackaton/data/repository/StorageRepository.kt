package com.allways.hackaton.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class StorageRepository {
    private val client = OkHttpClient()
    private val cloudName = "doy2yw2yz"
    private val uploadPreset = "ml_default"

    suspend fun uploadImage(uri: Uri, context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("Cloudinary", "Starting upload for uri: $uri")

                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    Log.e("Cloudinary", "Could not open input stream from uri")
                    return@withContext null
                }

                val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
                FileOutputStream(tempFile).use { output -> inputStream.copyTo(output) }
                Log.d("Cloudinary", "Temp file created: ${tempFile.length()} bytes")

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        tempFile.name,
                        tempFile.asRequestBody("image/*".toMediaType())
                    )
                    .addFormDataPart("upload_preset", uploadPreset)
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                    .post(requestBody)
                    .build()

                Log.d("Cloudinary", "Sending request...")
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""
                Log.d("Cloudinary", "Response code: ${response.code}")
                Log.d("Cloudinary", "Response body: $body")

                if (!response.isSuccessful) {
                    Log.e("Cloudinary", "Upload failed with code: ${response.code}")
                    return@withContext null
                }

                val json = JSONObject(body)
                val url = json.getString("secure_url")
                Log.d("Cloudinary", "Upload successful! URL: $url")
                url
            } catch (e: Exception) {
                Log.e("Cloudinary", "Exception during upload: ${e.message}", e)
                null
            }
        }
    }
}