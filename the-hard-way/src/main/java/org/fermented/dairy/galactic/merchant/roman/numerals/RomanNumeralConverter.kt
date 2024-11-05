package org.fermented.dairy.galactic.merchant.roman.numerals

import java.util.*
import java.util.regex.Pattern

/**
 * Converter converting Roman Numerals to Decimals
 * NOTE: The source doesn't go above M = 1000
 * but there are symbols for 5000 and up that follow the same pattern but can't be used in java files.
 * Upper limit for converter will be MMMCMXCIX = 3999
 */
object RomanNumeralConverter {
    //Mapping roman numeral to decimals
    private val SYMBOL_TO_VALUE_MAP = mapOf(
        "I" to 1,
        "IV" to 4,
        "V" to 5,
        "IX" to 9,
        "X" to 10,
        "XL" to 40,
        "L" to 50,  //Problem states that this is 250 but the number system doesn't work unless it's 50
        "XC" to 90,
        "C" to 100,
        "CD" to 400,
        "D" to 500,
        "CM" to 900,
        "M" to 1000
    )

    private val DESCENDING_ORDER_SYMBOLS = SYMBOL_TO_VALUE_MAP.asSequence()
        .sortedByDescending { it.value }
        .map{ it.key }
        .toList()

    /*
    https://www.oreilly.com/library/view/regular-expressions-cookbook/9780596802837/ch06s09.html
    but extended to support MM and MMM
     */
    private val ROMAN_NUMERAL_REGEX: Pattern = Pattern.compile(
        "^(?=[MDCLXVI])(M{0,3})*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$"
    )

    /**
     * Converts a roman numeral string to an integer
     *
     * @param roman The Roman numeral string
     * @return an integer representation of the roman numeral
     */
    @JvmStatic
    fun convert(roman: String?): Int {
        if (roman.isNullOrBlank()) return 0

        var tmpRoman = roman.trim { it <= ' ' }.uppercase(Locale.getDefault())

        require(ROMAN_NUMERAL_REGEX.matcher(tmpRoman).matches()) { "$roman is not a valid roman numeral number" }

        var decimal = 0

        for (value in DESCENDING_ORDER_SYMBOLS) {
            while (tmpRoman.startsWith(value)) {
                decimal += SYMBOL_TO_VALUE_MAP[value]!!
                tmpRoman = tmpRoman.replaceFirst(value.toRegex(), "")
            }
        }

        return decimal
    }
}
