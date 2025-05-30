package com.example.sumit.utils

import com.example.sumit.R
import com.example.sumit.data.translations.TranslationsRepository

/**
 * Handles the validation of password to make sure they conform to every rule.
 */
interface PasswordValidator {
    /**
     * Checks if a password is strong enough.
     * @param password The password entered by the user
     * @return The validation error message, or `null` if none was found
     */
    fun validate(password: String): String?
}

class TranslationPasswordValidator(private val translationsRepository: TranslationsRepository) :
    PasswordValidator {

    override fun validate(password: String): String? {
        val numberRegex = """\d""".toRegex()
        val uppercaseRegex = """[A-Z]""".toRegex()
        val whitespaceRegex = """\s""".toRegex()

        return when {
            password.trim().length < 8 ->
                translationsRepository.getTranslation(R.string.password_must_be_long)

            !numberRegex.containsMatchIn(password) ->
                translationsRepository.getTranslation(R.string.password_must_contain_number)

            !uppercaseRegex.containsMatchIn(password) ->
                translationsRepository.getTranslation(R.string.password_must_contain_uppercase)

            whitespaceRegex.containsMatchIn(password) ->
                translationsRepository.getTranslation(R.string.password_must_not_contain_whitespace)

            else -> null
        }
    }

}
