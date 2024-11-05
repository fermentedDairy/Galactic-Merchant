package org.fermented.dairy.galactic.merchant;

import org.fermented.dairy.galactic.merchant.exceptions.UnknownValueException;
import org.fermented.dairy.galactic.merchant.functional.TriFunction;
import org.fermented.dairy.galactic.merchant.functional.ValueFromSupplier;
import org.fermented.dairy.galactic.merchant.model.QueryData;
import org.fermented.dairy.galactic.merchant.model.QueryData.CompleteValueHintQueryData;
import org.fermented.dairy.galactic.merchant.model.QueryData.GetValueQueryData;
import org.fermented.dairy.galactic.merchant.model.QueryData.MapToRomanQueryData;
import org.fermented.dairy.galactic.merchant.model.QueryData.SimpleTranslationData;
import org.fermented.dairy.galactic.merchant.model.QueryData.UnknownQueryData;
import org.fermented.dairy.galactic.merchant.roman.numerals.RomanNumeralConverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that accepts input and routes to appropriate router.
 */
public class ValueConverter {

    private static final List<Pattern> MAP_TO_ROMAN_QUERY_PATTERNS =
            Stream.of("^(?<alienWord>\\w+) is (?<romanChar>M|m|D|d|C|c|L|l|X|x|V|v|I|i)$")
                    .map(Pattern::compile).toList();

    private static final List<Pattern> COMPLETE_VALUE_HINT_QUERY_PATTERNS =
            Stream.of("^(?<alienWords>[\\w ]+) (?<metal>\\w+) is (?<amount>\\d+) Credits$")
                    .map(Pattern::compile).toList();

    private static final List<Pattern> GET_VALUE_QUERY_PATTERNS =
            Stream.of("how many Credits is (?<alienWords>[\\w ]+) (?<metal>\\w+) \\?$",
                            "how many Credits is (?<alienWords>[\\w ]+) (?<metal>\\w+)\\?$")
                    .map(Pattern::compile).toList();

    private static final List<Pattern> SIMPLE_TRANSLATION_QUERY_PATTERNS =
            Stream.of("how much is (?<alienWords>[\\w ]+) \\?",
                            "how much is (?<alienWords>[\\w ]+)\\?")
                    .map(Pattern::compile).toList();

    @SuppressWarnings("Convert2MethodRef")
//Deliberate to allow for parameter order to change without using indexes in the template
    private static final TriFunction<String, String, Double, String> GET_VALUE_QUERY_RESULT_FUNCTION =
            (alienWords, metal, value) -> "%s %s is %.2f Credits".formatted(alienWords, metal, value);

    @SuppressWarnings("Convert2MethodRef")
//Deliberate to allow for parameter order to change without using indexes in the template
    private static final BiFunction<String, Integer, String> GET_SIMPLE_QUERY_RESULT_FUNCTION =
            (alienWords, value) -> "%s is %d".formatted(alienWords, value);

    private static final String VALUE_UNKNOWN_TEMPLATE = "I don't know what %s is";
    private static final String UNKNOWN_QUERY = "I have no idea what you are talking about";

    private final Map<String, Character> alienWordToRomanCharMap = HashMap.newHashMap(7);
    private final Map<String, Supplier<Double>> metalToMultiplierSupplierMap = new HashMap<>();

    /**
     * Translates input query to response.
     *
     * @param input The input query
     * @return The response, empty string if query is accepted but no response is anticipated as in the case of a query that populates data
     */
    public String acceptInput(String input) {
        try {
            return switch (getQueryData(input)) {
                case MapToRomanQueryData queryData-> {
                    //<editor-fold desc="Ingest Alien Word to Roman Numeral Value Hint Data" defaultstate="collapsed">
                    alienWordToRomanCharMap.put(queryData.alienWord(), queryData.romanNumeral());
                    yield "";
                    //</editor-fold>
                }
                case CompleteValueHintQueryData queryData -> {
                    //<editor-fold desc="Ingest Complete Value Hint Data" defaultstate="collapsed">
                    Supplier<Double> amountSupplier = () -> {
                        int fromAlienPhrase = RomanNumeralConverter.convert(
                                getAlienToRoman(queryData.alienPhrase())
                        );
                        return (double) queryData.amount() / fromAlienPhrase;
                    };

                    if (areAllKnown(queryData.alienPhrase()))
                        metalToMultiplierSupplierMap.put(queryData.metal(), ValueFromSupplier.Companion.value(amountSupplier.get())); //TODO: WTF is a companion and why can't I have static factories?
                    else
                        metalToMultiplierSupplierMap.put(queryData.metal(), ValueFromSupplier.Companion.supplier(amountSupplier));  //If alien to roman numeral mapping is not known, map the supplier for later

                    yield "";
                    //</editor-fold>
                }
                case GetValueQueryData queryData -> translateAlienPhrase(queryData.alienWords(), queryData.metal());
                case SimpleTranslationData queryData -> GET_SIMPLE_QUERY_RESULT_FUNCTION.apply(
                        queryData.alienPhrase(),
                        RomanNumeralConverter.convert(getAlienToRoman(queryData.alienPhrase()))
                );
                //noinspection unused unnamed lambda params only available in JDK 22
                case UnknownQueryData unknown -> UNKNOWN_QUERY;
                default -> throw new IllegalStateException("Unexpected value: " + getQueryData(input));
            };
        } catch (final UnknownValueException tfe) {
            return VALUE_UNKNOWN_TEMPLATE.formatted(tfe.unknownValue);
        }
    }

    private QueryData getQueryData(final String input) {
        Optional<MapToRomanQueryData> optionalMapToRomanQueryData =
                MAP_TO_ROMAN_QUERY_PATTERNS.stream()
                        .map(pattern -> pattern.matcher(input))
                        .filter(Matcher::matches)
                        .map(matcher -> new MapToRomanQueryData(
                                matcher.group("alienWord"),
                                matcher.group("romanChar")
                                        .toUpperCase()
                                        .toCharArray()[0]))
                        .findFirst();
        if (optionalMapToRomanQueryData.isPresent())
            return optionalMapToRomanQueryData.get();

        Optional<CompleteValueHintQueryData> optionalCompleteValueHintQueryData =
                COMPLETE_VALUE_HINT_QUERY_PATTERNS.stream()
                        .map(pattern -> pattern.matcher(input))
                        .filter(Matcher::matches)
                        .map(matcher -> new CompleteValueHintQueryData(matcher.group("alienWords"),
                                matcher.group("metal"),
                                Integer.parseInt(matcher.group("amount"))))
                        .findFirst();

        if (optionalCompleteValueHintQueryData.isPresent())
            return optionalCompleteValueHintQueryData.get();

        Optional<GetValueQueryData> optionalGetValueQueryData =
                GET_VALUE_QUERY_PATTERNS.stream()
                        .map(pattern -> pattern.matcher(input))
                        .filter(Matcher::matches)
                        .map(matcher -> new GetValueQueryData(
                                matcher.group("alienWords"),
                                matcher.group("metal")
                        ))
                        .findFirst();

        if (optionalGetValueQueryData.isPresent())
            return optionalGetValueQueryData.get();

        Optional<SimpleTranslationData> optionalSimpleTranslationData =
                SIMPLE_TRANSLATION_QUERY_PATTERNS.stream()
                        .map(pattern -> pattern.matcher(input))
                        .filter(Matcher::matches)
                        .map(matcher -> new SimpleTranslationData(matcher.group("alienWords")))
                        .findFirst();

        if (optionalSimpleTranslationData.isPresent())
            return optionalSimpleTranslationData.get();

        return new UnknownQueryData(input);
    }

    private String translateAlienPhrase(final String alienPhrase, final String metal) {
        final int value = RomanNumeralConverter.convert(getAlienToRoman(alienPhrase));
        final Double metalValue = metalToMultiplierSupplierMap
                .getOrDefault(metal, () -> {
                    throw new UnknownValueException(metal);
                })//Workaround for missing orThrow method
                .get();
        final double amount = value * metalValue;

        return GET_VALUE_QUERY_RESULT_FUNCTION.apply(alienPhrase, metal, amount);

    }

    private String getAlienToRoman(final String alienPhrase) {
        return Arrays.stream(alienPhrase.split(" "))
                .map(alienWord -> {
                    Character romanChar = alienWordToRomanCharMap.get(alienWord);
                    if (romanChar == null)
                        throw new UnknownValueException(alienWord);
                    return romanChar;
                })
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    private boolean areAllKnown(final String alienPhrase) {
        return Arrays.stream(alienPhrase.split(" "))
                .allMatch(alienWordToRomanCharMap::containsKey);
    }

}
