package com.briolink.companyservice.common.util

import com.ibm.icu.text.Transliterator
import java.util.Locale

object StringUtil {
    fun slugify(word: String, isRandom: Boolean = true, length: Int = 50): String =
            transliterate(if (isRandom) word + randomString() else word)
                    .replace("[^\\p{ASCII}]".toRegex(), "")
                    .replace("[^a-zA-Z0-9\\s]+".toRegex(), "").trim()
                    .replace("\\s+".toRegex(), "-")
                    .lowercase(Locale.getDefault())
                    .let {
                        if (isRandom) word.take(length - 7) + '-' + randomString()
                        else it.take(length)
                    }

    private fun randomString(length: Int = 6): String {
        val alphabet: List<Char> = ('a'..'z') + ('0'..'9')
        return List(length) { alphabet.random() }.joinToString("")
    }

    private fun transliterate(input: String): String {
        val transliterator: Transliterator = Transliterator
                .getInstance("NFD; Any-Latin; NFC; NFD; [:Nonspacing Mark:] Remove; NFC")
        return transliterator.transliterate(input)
    }
}

