package com.example.sumit.data.translations

import androidx.annotation.StringRes

/**
 * Provides translations for every part of the application.
 */
interface TranslationsRepository {
    /**
     * Returns the translation in the current display language.
     * @param resId The resource id for the requested text
     * @return The corresponding translation
     */
    fun getTranslation(@StringRes resId: Int): String
}
