package com.example.sumit.data.translations

import android.content.Context

class LocalTranslationsRepository(private val context: Context) : TranslationsRepository {
    override fun getTranslation(resId: Int): String = context.resources.getString(resId)
}
