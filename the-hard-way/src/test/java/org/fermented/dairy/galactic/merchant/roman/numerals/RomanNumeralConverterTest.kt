package org.fermented.dairy.galactic.merchant.roman.numerals

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import java.util.*

class RomanNumeralConverterTest : StringSpec({
    "Validate sample roman numeral conversions" {
        checkAll(
            Exhaustive.collection(
                provideValidRomanNumerals()
            )
        ) { pair ->
            run {
                RomanNumeralConverter.convert(pair.romanNumerals) shouldBe pair.expected
                RomanNumeralConverter.convert(pair.romanNumerals.lowercase(Locale.getDefault())) shouldBe pair.expected
                RomanNumeralConverter.convert(pair.romanNumerals+" ") shouldBe pair.expected
                RomanNumeralConverter.convert(" " +pair.romanNumerals) shouldBe pair.expected
            }
        }
    }

    "not a valid roman numeral"{
        checkAll(  Exhaustive.collection(provideInvalidRomanNumerals())) { romanNumeral ->
            val exception = shouldThrowExactly<IllegalArgumentException>{
                RomanNumeralConverter.convert(romanNumeral)
            }
            "$romanNumeral is not a valid roman numeral number" shouldBe exception.message
        }
    }

    "Validate null converts to 0"{
        RomanNumeralConverter.convert(null) shouldBe 0
    }

})

fun provideValidRomanNumerals(): List<ParamExpectPair> {
    return listOf(
        ParamExpectPair(romanNumerals = "", expected = 0),
        ParamExpectPair(romanNumerals = "I", expected = 1),
        ParamExpectPair(romanNumerals = "II", expected = 2),
        ParamExpectPair(romanNumerals = "III", expected = 3),
        ParamExpectPair(romanNumerals = "IV", expected = 4),
        ParamExpectPair(romanNumerals = "V", expected = 5),
        ParamExpectPair(romanNumerals = "VI", expected = 6),
        ParamExpectPair(romanNumerals = "VII", expected = 7),
        ParamExpectPair(romanNumerals = "VIII", expected = 8),
        ParamExpectPair(romanNumerals = "IX", expected = 9),
        ParamExpectPair(romanNumerals = "X", expected = 10),
        ParamExpectPair(romanNumerals = "XL", expected = 40),
        ParamExpectPair(romanNumerals = "L", expected = 50),
        ParamExpectPair(romanNumerals = "XC", expected = 90),
        ParamExpectPair(romanNumerals = "C", expected = 100),
        ParamExpectPair(romanNumerals = "CD", expected = 400),
        ParamExpectPair(romanNumerals = "D", expected = 500),
        ParamExpectPair(romanNumerals = "CM", expected = 900),
        ParamExpectPair(romanNumerals = "M", expected = 1000),
        ParamExpectPair(romanNumerals = "MCMLXXXIV", expected = 1984),
        ParamExpectPair(romanNumerals = "MMXXIV", expected = 2024),
        ParamExpectPair(romanNumerals = "CXXIX", expected = 129),
        ParamExpectPair(romanNumerals = "MMMCMXCIX", expected = 3999)
    )
}

fun provideInvalidRomanNumerals(): List<String> {
    return listOf("No", "MNo", "_+{}", "VX", "IIII", "XXXX", "CCCC", "VIV")
}

data class ParamExpectPair(val romanNumerals: String, val expected: Int )