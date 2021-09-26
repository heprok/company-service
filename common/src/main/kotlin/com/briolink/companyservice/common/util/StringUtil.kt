package com.briolink.companyservice.common.util

import com.ibm.icu.text.Transliterator
import java.util.Locale

object StringUtil {
    fun slugify(word: String): String {
        return transliterate(word)
                .replace("[^\\p{ASCII}]".toRegex(), "")
                .replace("[^a-zA-Z0-9\\s]+".toRegex(), "").trim()
                .replace("\\s+".toRegex(), "-")
                .lowercase(Locale.getDefault())
    }

    private fun transliterate(input: String): String {
        val transliterator: Transliterator = Transliterator
                .getInstance("NFD; Any-Latin; NFC; NFD; [:Nonspacing Mark:] Remove; NFC")
        return transliterator.transliterate(input)
    }
}

