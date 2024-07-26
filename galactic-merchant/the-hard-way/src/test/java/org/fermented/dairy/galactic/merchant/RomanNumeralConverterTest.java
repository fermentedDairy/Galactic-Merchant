package org.fermented.dairy.galactic.merchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RomanNumeralConverterTest {

    @ParameterizedTest(name = "{index}. \"{0}\" equals {1}")
    @MethodSource("provideValidRomanNumerals")
    @DisplayName("Validate sample roman numeral conversions")
    void validateSampleRomanNumeralConversions(final String romanNumeral, final int expected) {
        assertAll("",
                () -> assertEquals(expected, RomanNumeralConverter.convert(romanNumeral)),
                () -> assertEquals(expected, RomanNumeralConverter.convert(romanNumeral.toLowerCase())),
                () -> assertEquals(expected, RomanNumeralConverter.convert(romanNumeral + " ")),
                () -> assertEquals(expected, RomanNumeralConverter.convert(" " + romanNumeral))
        );
    }

    @ParameterizedTest(name = "{index}. {0} is invalid")
    @MethodSource("provideInvalidRomanNumerals")
    @DisplayName("not a valid roman numeral")
    void notAValidRomanNumeral(final String romanNumeral) {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> RomanNumeralConverter.convert(romanNumeral));

        assertEquals(romanNumeral + " is not a valid roman numeral number",
                exception.getMessage());
    }

    @Test
    @DisplayName("Validate null converts to 0")
    void validateNullConvertsTo0() {
        assertEquals(0, RomanNumeralConverter.convert(null));
    }


    public static Stream<Arguments> provideInvalidRomanNumerals() {
        return Stream.of("No", "MNo", "_+{}").map(Arguments::of);
    }

    public static Stream<Arguments> provideValidRomanNumerals() {
        return Stream.of(
                Arguments.of("", 0),
                Arguments.of("I", 1),
                Arguments.of("II", 2),
                Arguments.of("III", 3),
                Arguments.of("IV", 4),
                Arguments.of("V", 5),
                Arguments.of("IX", 9),
                Arguments.of("X", 10),
                Arguments.of("XL", 40),
                Arguments.of("L", 50),
                Arguments.of("XC", 90),
                Arguments.of("C", 100),
                Arguments.of("CD", 400),
                Arguments.of("D", 500),
                Arguments.of("CM", 900),
                Arguments.of("M", 1000),
                Arguments.of("MCMLXXXIV", 1984),
                Arguments.of("MMXXIV", 2024),
                Arguments.of("CXXIX", 129)
        );
    }
}