package org.fermented.dairy.galactic.merchant

import org.fermented.dairy.galactic.merchant.exceptions.UnknownValueException
import org.fermented.dairy.galactic.merchant.extensions.orThrow
import org.fermented.dairy.galactic.merchant.functional.TriFunction
import org.fermented.dairy.galactic.merchant.functional.ValueFromSupplier
import org.fermented.dairy.galactic.merchant.model.QueryData
import org.fermented.dairy.galactic.merchant.model.QueryData.*
import org.fermented.dairy.galactic.merchant.roman.numerals.RomanNumeralConverter.convert
import java.util.*
import java.util.function.BiFunction
import java.util.function.Supplier
import java.util.regex.Matcher
import java.util.regex.Pattern

class ValueConverter {

    private val alienWordToRomanCharMap: MutableMap<String, Char> = HashMap.newHashMap(7)
    private val metalToMultiplierSupplierMap: MutableMap<String, Supplier<Double>> = HashMap()

    /**
     * Translates input query to response.
     *
     * @param input The input query
     * @return The response, empty string if query is accepted but no response is anticipated as in the case of a query that populates data
     */
    fun acceptInput(input: String): String {
        try {
            return when (val queryData = getQueryData(input)) {
                is MapToRomanQueryData -> {
                    //<editor-fold desc="Ingest Alien Word to Roman Numeral Value Hint Data" defaultstate="collapsed">
                    alienWordToRomanCharMap[queryData.alienWord] = queryData.romanNumeral
                    return ""
                    //</editor-fold>
                }
                is CompleteValueHintQueryData -> {
                    //<editor-fold desc="Ingest Complete Value Hint Data" defaultstate="collapsed">
                    val amountSupplier = Supplier<Double> {
                        val fromAlienPhrase: Int = convert(
                                getAlienToRoman(queryData.alienPhrase)
                        )

                        queryData.amount.toDouble() / fromAlienPhrase.toDouble()
                    }

                    if (areAllKnown(queryData.alienPhrase))
                        metalToMultiplierSupplierMap[queryData.metal] = ValueFromSupplier.value(amountSupplier.get())
                    else
                        metalToMultiplierSupplierMap[queryData.metal] = ValueFromSupplier.supplier(
                            amountSupplier
                        )  //If alien to roman numeral mapping is not known, map the supplier for later

                    return ""
                    //</editor-fold>
                }
                is GetValueQueryData -> translateAlienPhrase(queryData.alienWords, queryData.metal)
                is SimpleTranslationData -> GET_SIMPLE_QUERY_RESULT_FUNCTION.apply(
                        queryData.alienPhrase,
                        convert(getAlienToRoman(queryData.alienPhrase))
                )
                //noinspection unused unnamed lambda params only available in JDK 22
                is UnknownQueryData -> UNKNOWN_QUERY
            }
        } catch (tfe: UnknownValueException ) {
            return VALUE_UNKNOWN_TEMPLATE.format(tfe.unknownValue)
        }
    }

    private fun getQueryData(input: String): QueryData {
        val mapToRomanQueryData =
            MAP_TO_ROMAN_QUERY_PATTERNS.asSequence()
                .map { pattern: Pattern -> pattern.matcher(input) }
                .filter { obj: Matcher -> obj.matches() }
                .map { matcher: Matcher ->
                    MapToRomanQueryData(
                        matcher.group("alienWord"),
                        matcher.group("romanChar")
                            .uppercase(Locale.getDefault())
                            .toCharArray()[0]
                    )
                }
                .firstOrNull()

        if (mapToRomanQueryData != null) return mapToRomanQueryData

        val completeValueHintQueryData =
            COMPLETE_VALUE_HINT_QUERY_PATTERNS.asSequence()
                .map { pattern: Pattern -> pattern.matcher(input) }
                .filter { obj: Matcher -> obj.matches() }
                .map { matcher: Matcher ->
                    CompleteValueHintQueryData(
                        matcher.group("alienWords"),
                        matcher.group("metal"),
                        matcher.group("amount").toInt()
                    )
                }
                .firstOrNull()

        if (completeValueHintQueryData != null) return completeValueHintQueryData

        val getValueQueryData =
            GET_VALUE_QUERY_PATTERNS.asSequence()
                .map { pattern: Pattern -> pattern.matcher(input) }
                .filter { obj: Matcher -> obj.matches() }
                .map { matcher: Matcher ->
                    GetValueQueryData(
                        matcher.group("alienWords"),
                        matcher.group("metal")
                    )
                }
                .firstOrNull()

        if (getValueQueryData != null) return getValueQueryData

        val simpleTranslationData =
            SIMPLE_TRANSLATION_QUERY_PATTERNS.asSequence()
                .map { pattern: Pattern -> pattern.matcher(input) }
                .filter { obj: Matcher -> obj.matches() }
                .map { matcher: Matcher -> SimpleTranslationData(matcher.group("alienWords")) }
                .firstOrNull()

        if (simpleTranslationData != null) return simpleTranslationData

        return UnknownQueryData(input)
    }

    private fun translateAlienPhrase(alienPhrase: String, metal: String): String {
        val value = convert(getAlienToRoman(alienPhrase))
        val metalValue = metalToMultiplierSupplierMap
            .orThrow(metal) {throw UnknownValueException(metal)}
            .get()
        val amount = value * metalValue

        return GET_VALUE_QUERY_RESULT_FUNCTION.apply(alienPhrase, metal, amount)
    }

    private fun getAlienToRoman(alienPhrase: String): String {
        return alienPhrase.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().asSequence()
            .map { alienWord: String? -> alienWordToRomanCharMap[alienWord] ?: throw UnknownValueException(alienWord!!) }
            .map { char: Char ->  char.toString()}
            .joinToString (separator = "")
    }

    private fun areAllKnown(alienPhrase: String): Boolean {
        return alienPhrase.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().asSequence()
            .all { key: String? -> alienWordToRomanCharMap.containsKey(key) }
    }

    companion object {
        val MAP_TO_ROMAN_QUERY_PATTERNS: List<Pattern> =
            listOf("^(?<alienWord>\\w+) is (?<romanChar>M|m|D|d|C|c|L|l|X|x|V|v|I|i)$")
                .map { regex: String -> Pattern.compile(regex) }

        val COMPLETE_VALUE_HINT_QUERY_PATTERNS: List<Pattern> =
            listOf("^(?<alienWords>[\\w ]+) (?<metal>\\w+) is (?<amount>\\d+) Credits$")
                .map { regex: String -> Pattern.compile(regex) }

        val GET_VALUE_QUERY_PATTERNS: List<Pattern> = listOf(
            "how many Credits is (?<alienWords>[\\w ]+) (?<metal>\\w+) \\?$",
            "how many Credits is (?<alienWords>[\\w ]+) (?<metal>\\w+)\\?$"
        )
            .map { regex: String -> Pattern.compile(regex) }

        val SIMPLE_TRANSLATION_QUERY_PATTERNS: List<Pattern> = listOf(
            "how much is (?<alienWords>[\\w ]+) \\?",
            "how much is (?<alienWords>[\\w ]+)\\?"
        )
            .map { regex: String -> Pattern.compile(regex) }

        //Deliberate to allow for parameter order to change without using indexes in the template
        val GET_VALUE_QUERY_RESULT_FUNCTION: TriFunction<String, String, Double, String> =
            TriFunction { alienWords: String, metal: String?, value: Double? ->
                "%s %s is %.2f Credits".format(
                    alienWords,
                    metal,
                    value
                )
            }

        //Deliberate to allow for parameter order to change without using indexes in the template
        val GET_SIMPLE_QUERY_RESULT_FUNCTION: BiFunction<String, Int, String> =
            BiFunction { alienWords, value -> "$alienWords is $value"}

        const val VALUE_UNKNOWN_TEMPLATE = "I don't know what %s is"
        const val UNKNOWN_QUERY = "I have no idea what you are talking about"
    }
}