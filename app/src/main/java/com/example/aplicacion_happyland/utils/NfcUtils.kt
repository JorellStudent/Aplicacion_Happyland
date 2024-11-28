package com.example.aplicacion_happyland.utils

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

object NfcUtils {
    fun processNfcTag(
        intent: Intent,
        db: FirebaseFirestore,
        context: android.content.Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }

        if (tag == null) {
            onError("No se detectó ninguna etiqueta NFC")
            return
        }

        val ndef = Ndef.get(tag)
        if (ndef == null) {
            onError("La etiqueta NFC no contiene datos NDEF")
            return
        }

        try {
            val ndefMessage = ndef.cachedNdefMessage ?: run {
                onError("No se encontró ningún mensaje NDEF en la etiqueta")
                return
            }

            val record = ndefMessage.records.getOrNull(0) ?: run {
                onError("El mensaje NDEF está vacío o no contiene registros válidos")
                return
            }

            val payload = record.payload
            val tagData = String(payload, Charsets.UTF_8).trim()

            Toast.makeText(context, "Etiqueta NFC leída: $tagData", Toast.LENGTH_SHORT).show()

            // Buscar tarjeta en Firestore
            db.collection("tarjetas").whereEqualTo("codigo", tagData).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        onSuccess(tagData)
                    } else {
                        onError("La tarjeta no está registrada")
                    }
                }
                .addOnFailureListener {
                    onError("Error al buscar la tarjeta en Firestore")
                }
        } catch (e: Exception) {
            onError("Error al procesar la etiqueta NFC: ${e.message}")
        }
    }
}
