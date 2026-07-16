package com.hciproject.exploresite

import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslationManager {
    private val translators = mutableMapOf<String, Translator>()

    suspend fun translate(
        text: String,
        targetLanguageCode: String,
        sourceLanguageCode: String = TranslateLanguage.ITALIAN
    ): String {
        if (targetLanguageCode == sourceLanguageCode || text.isBlank()) return text

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguageCode)
            .setTargetLanguage(targetLanguageCode)
            .build()

        val translator = translators.getOrPut("$sourceLanguageCode-$targetLanguageCode") {
            Translation.getClient(options)
        }

        return try {
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            // Wait for model download
            translator.downloadModelIfNeeded(conditions).await()
            // Perform translation
            translator.translate(text).await()
        } catch (e: Exception) {
            e.printStackTrace()
            text
        }
    }

    fun getLanguageName(code: String): String {
        return when (code) {
            TranslateLanguage.ITALIAN -> "Italiano"
            TranslateLanguage.ENGLISH -> "English"
            TranslateLanguage.FRENCH -> "Français"
            TranslateLanguage.SPANISH -> "Español"
            TranslateLanguage.GERMAN -> "Deutsch"
            else -> code
        }
    }

    val supportedLanguages = listOf(
        TranslateLanguage.ITALIAN,
        TranslateLanguage.ENGLISH,
        TranslateLanguage.FRENCH,
        TranslateLanguage.SPANISH,
        TranslateLanguage.GERMAN
    )
}
