package com.trust.home.security.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.echodev.resizer.Resizer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

private const val FILE_CACHE_PATH = "HomeSecurity/Users/Avatars/"
object AppUtils {
    fun compressImage(context: Context,
                      uri: Uri,
                      userId: Long,
                      onCompressCompleted: (path: String?) -> Unit) = CoroutineScope(Dispatchers.IO).launch {
        getFile(context, uri)?.let {
            val compressedImageFile = Resizer(context)
                .setTargetLength(300)
                .setSourceImage(it)
                .resizedFile

            val path = copyFileToCache(context, compressedImageFile, userId)
            withContext(Dispatchers.Main) {
                onCompressCompleted.invoke(path)
            }
        } ?: withContext(Dispatchers.Main) {
            onCompressCompleted.invoke(null)
        }
    }

    private fun copyFileToCache(context: Context, sourceFile: File, userId: Long): String? {
        try {
            val inputStream: InputStream = FileInputStream(sourceFile)
            val outputFile = createFile(context, userId)
            val outputStream: OutputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            inputStream.close()
            outputStream.close()
            return outputFile.path
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun createFile(context: Context, userId: Long): File {
        val file = File(context.cacheDir, "$FILE_CACHE_PATH${userId}_${now()}.jpg")

        if(!file.exists()) {
            file.parentFile?.mkdirs()
        }

        return file
    }

    private fun now() = System.currentTimeMillis()

    private fun fileToBase64(file: File): String? {
        try {
            FileInputStream(file).use { fileInputStream ->
                val bytes = ByteArray(file.length().toInt())
                fileInputStream.read(bytes)
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getEncoder().encodeToString(bytes)
                } else null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getFile(context: Context, uri: Uri): File? {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        return try {
            context.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(
                    ins!!,
                    destinationFilename
                )
            }
            destinationFilename
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            null
        }
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
}