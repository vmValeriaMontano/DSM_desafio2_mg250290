package com.example.desafio2_dsm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    /**
     * Convierte una imagen seleccionada desde la galería
     * a Base64 para poder guardarla en Firebase Realtime Database.
     *
     * La imagen se reduce y comprime para evitar que sea
     * demasiado pesada.
     */
    fun uriToBase64(
        context: Context,
        uri: Uri
    ): String? {

        try {

            // Primero obtenemos solamente las dimensiones
            // de la imagen.
            val boundsOptions =
                BitmapFactory.Options()

            boundsOptions.inJustDecodeBounds = true

            context.contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->

                    BitmapFactory.decodeStream(
                        inputStream,
                        null,
                        boundsOptions
                    )
                }

            if (
                boundsOptions.outWidth <= 0 ||
                boundsOptions.outHeight <= 0
            ) {
                return null
            }

            // Reducir la resolución antes de cargar
            // completamente la imagen en memoria.
            var sampleSize = 1

            while (
                boundsOptions.outWidth / sampleSize > 1000 ||
                boundsOptions.outHeight / sampleSize > 1000
            ) {
                sampleSize *= 2
            }

            val options =
                BitmapFactory.Options()

            options.inSampleSize = sampleSize

            val bitmap =
                context.contentResolver
                    .openInputStream(uri)
                    ?.use { inputStream ->

                        BitmapFactory.decodeStream(
                            inputStream,
                            null,
                            options
                        )
                    }
                    ?: return null

            // Tamaño máximo final.
            val maxSize = 800

            val largestSide =
                maxOf(
                    bitmap.width,
                    bitmap.height
                )

            val finalBitmap: Bitmap

            if (largestSide > maxSize) {

                val ratio =
                    maxSize.toFloat() /
                            largestSide.toFloat()

                val newWidth =
                    (bitmap.width * ratio)
                        .toInt()

                val newHeight =
                    (bitmap.height * ratio)
                        .toInt()

                finalBitmap =
                    Bitmap.createScaledBitmap(
                        bitmap,
                        newWidth,
                        newHeight,
                        true
                    )

            } else {

                finalBitmap = bitmap
            }

            // Comprimir como JPEG.
            val outputStream =
                ByteArrayOutputStream()

            finalBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                65,
                outputStream
            )

            val imageBytes =
                outputStream.toByteArray()

            // Convertir los bytes a Base64.
            return Base64.encodeToString(
                imageBytes,
                Base64.NO_WRAP
            )

        } catch (exception: Exception) {

            exception.printStackTrace()

            return null
        }
    }

    /**
     * Convierte Base64 nuevamente en Bitmap
     * para mostrarlo en un ImageView.
     */
    fun base64ToBitmap(
        base64: String
    ): Bitmap? {

        return try {

            if (base64.isEmpty()) {
                return null
            }

            val imageBytes =
                Base64.decode(
                    base64,
                    Base64.DEFAULT
                )

            BitmapFactory.decodeByteArray(
                imageBytes,
                0,
                imageBytes.size
            )

        } catch (exception: Exception) {

            exception.printStackTrace()

            null
        }
    }
}
