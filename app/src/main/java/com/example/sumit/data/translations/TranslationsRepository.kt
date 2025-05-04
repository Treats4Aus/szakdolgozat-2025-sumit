package com.example.sumit.data.translations

import androidx.annotation.StringRes

interface TranslationsRepository {
    fun getTranslation(@StringRes resId: Int): String
}
