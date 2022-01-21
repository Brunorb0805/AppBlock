package br.com.gps.gpshub.other

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object FileUtil {

    fun saveOnFile(context: Context, bmp: Bitmap, fileName: String?): String? {
        val location = fileName?.let {
            "${context.filesDir}/$fileName.jpg"
        } ?: "${context.filesDir}/foto${Date().time}.jpg"

        return try {
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            bmp.recycle()

            FileOutputStream(location).use { out ->
                out.write(byteArray)
                out.close()
                location
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}