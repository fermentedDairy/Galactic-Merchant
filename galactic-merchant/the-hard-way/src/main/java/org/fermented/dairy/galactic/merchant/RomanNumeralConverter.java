package org.fermented.dairy.galactic.merchant;

import java.util.List;
import java.util.Map;

/**
 * Converter converting Roman Numerals to Decimals
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
            Map.entry("MMM", 3000)//Source doesn't go above this so an artificial limit is placed here. Is 4000 == XXXX or X<something>
    );

    private static final List<String> DESCENDING_ORDER_SYMBOLS =
            SYMBOL_TO_VALUE_MAP.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .toList().reversed();

    public static int convert(final String roman) {

        if (roman == null || roman.isBlank())
            return 0;

        int decimal = 0;
        String tmpString = roman.trim().toUpperCase();
        for (final String value : DESCENDING_ORDER_SYMBOLS) {
            System.out.println("Checking " + value + " for " + tmpString);
            if (tmpString.startsWith(value)) {
                decimal += SYMBOL_TO_VALUE_MAP.get(value);
                tmpString = tmpString.replaceFirst(value, "");
            }
        }
        if (!tmpString.isEmpty())
            throw new IllegalArgumentException(roman + " is not a valid roman numeral number");

        return decimal;
    }
}
