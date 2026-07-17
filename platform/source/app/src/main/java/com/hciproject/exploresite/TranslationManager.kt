package com.hciproject.exploresite

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslationManager {
    private val translators = mutableMapOf<String, Translator>()
    private val translationCache = mutableMapOf<String, String>()

    suspend fun translate(
        text: String,
        targetLanguageCode: String,
        sourceLanguageCode: String = TranslateLanguage.ITALIAN
    ): String {
        if (targetLanguageCode == sourceLanguageCode || text.isBlank()) return text

        // Optimization: Cache check to avoid calling ML Kit engine repeatedly for same text
        val cacheKey = "$targetLanguageCode:$text"
        translationCache[cacheKey]?.let { return it }

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
            translator.downloadModelIfNeeded(conditions).await()
            val result = translator.translate(text).await()
            
            // Store result in cache
            translationCache[cacheKey] = result
            result
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
