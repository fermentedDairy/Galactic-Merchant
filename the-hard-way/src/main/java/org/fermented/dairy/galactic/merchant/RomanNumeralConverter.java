package org.fermented.dairy.galactic.merchant;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converter converting Roman Numerals to Decimals
 * NOTE: The source doesn't go above M = 1000
 * but there are symbols for 5000 and up that follow the same pattern but can't be used in java files.
 * Upper limit for converter will be MMMCMXCIX = 3999
 */
public final class RomanNumeralConverter {
    //Mapping roman numeral to decimal, some are fundamental and provided. others are pre-calculated
    private static final Map<String, Integer> SYMBOL_TO_VALUE_MAP = Map.ofEntries(
            Map.entry("I", 1),
            Map.entry("II", 2),
            Map.entry("III", 3),
            Map.entry("IV", 4),
            Map.entry("V", 5),
            Map.entry("IX", 9),
            Map.entry("X", 10),
            Map.entry("XX", 20),
            Map.entry("XXX", 30),
            Map.entry("XL", 40),
            Map.entry("L", 50),//Problem states that this is 250 but the number system doesn't work unless it's 50
            Map.entry("XC", 90),
            Map.entry("C", 100),
            Map.entry("CC", 200),
            Map.entry("CCC", 300),
            Map.entry("CD", 400),
            Map.entry("D", 500),
            Map.entry("CM", 900),
            Map.entry("M", 1000),
            Map.entry("MM", 2000),
            Map.entry("MMM", 3000)
    );

    private static final List<String> DESCENDING_ORDER_SYMBOLS =
            SYMBOL_TO_VALUE_MAP.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .toList().reversed();

    /*
    https://www.oreilly.com/library/view/regular-expressions-cookbook/9780596802837/ch06s09.html
    but extended to support MM and MMM
     */
    private static final Pattern ROMAN_NUMERAL_REGEX = Pattern.compile(
            "^(?=[MDCLXVI])(M{0,3})*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    public static int convert(final String roman) {

        if (roman == null || roman.isBlank())
            return 0;

        String tmpRoman = roman.trim().toUpperCase();

        if(!ROMAN_NUMERAL_REGEX.matcher(tmpRoman).matches())
            throw new IllegalArgumentException(roman + " is not a valid roman numeral number");

        int decimal = 0;

        for (final String value : DESCENDING_ORDER_SYMBOLS) {
            if (tmpRoman.startsWith(value)) {
                decimal += SYMBOL_TO_VALUE_MAP.get(value);
                tmpRoman = tmpRoman.replaceFirst(value, "");
            }
        }

        return decimal;
    }
}
