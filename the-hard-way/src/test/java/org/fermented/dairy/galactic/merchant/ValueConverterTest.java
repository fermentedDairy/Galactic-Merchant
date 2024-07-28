package org.fermented.dairy.galactic.merchant;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ValueConverterTest {

    @ParameterizedTest(name = "{index}. {1}")
    @MethodSource("getSuccessValues")
    @DisplayName("Successful translations")
    void testSuccess(List<Pair<String, String>> value, String source) {
        final ValueConverter translator = new ValueConverter();
        value.forEach(pair ->
                        assertEquals(pair.getLeft(), translator.acceptInput(pair.getRight()), () -> pair.getRight() + " not Mapped")
                );
    }

    @ParameterizedTest(name = "{index}. \"{0}\" is not valid")
    @MethodSource("getUnsuccessfulValues_notValidQueries")
    @DisplayName("unsuccessful translations due to invalid queries")
    void testUnSuccessful_notValidQueries(String value) {
        final ValueConverter translator = new ValueConverter();
        assertEquals("I have no idea what you are talking about", translator.acceptInput(value));
    }

    @ParameterizedTest(name = "{index}. \"{1}\" missing Data should return \"{2}\"")
    @MethodSource("getUnsuccessfulValues_noData")
    @DisplayName("unsuccessful translations due to insufficient data")
    void testUnsuccessfulValues_noData(List<String> preQueries, String value, String expected) {
        final ValueConverter translator = new ValueConverter();
        preQueries.forEach(translator::acceptInput);
        assertEquals(expected, translator.acceptInput(value));
    }

    public static Stream<Arguments> getSuccessValues() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                Pair.of("", "glob is I"),
                                Pair.of("", "prok is V"),
                                Pair.of("", "pish is X"),
                                Pair.of("", "tegj is L"),
                                Pair.of("", "glob glob Silver is 34 Credits"),
                                Pair.of("", "glob prok Gold is 57800 Credits"),
                                Pair.of("", "pish pish Iron is 3910 Credits"),
                                Pair.of("pish tegj glob glob is 42", "how much is pish tegj glob glob ?"),
                                Pair.of("pish tegj glob glob is 42", "how much is pish tegj glob glob?"),
                                Pair.of("glob prok Silver is 68.00 Credits", "how many Credits is glob prok Silver ?"),
                                Pair.of("glob prok Gold is 57800.00 Credits", "how many Credits is glob prok Gold ?"),
                                Pair.of("glob prok Iron is 782.00 Credits", "how many Credits is glob prok Iron ?"),
                                Pair.of("glob prok Silver is 68.00 Credits", "how many Credits is glob prok Silver?"),//Without the space before the '?'
                                Pair.of("glob prok Gold is 57800.00 Credits", "how many Credits is glob prok Gold?"),
                                Pair.of("glob prok Iron is 782.00 Credits", "how many Credits is glob prok Iron?")
                        ),
                        "From examples"),
                Arguments.of(List.of(
                                Pair.of("", "flippity is I"),
                                Pair.of("", "floppity is V"),
                                Pair.of("", "flappity is X"),
                                Pair.of("", "fluppity is L"),
                                Pair.of("", "flippity flippity Silver is 34 Credits"),
                                Pair.of("", "flippity floppity Gold is 57800 Credits"),
                                Pair.of("", "flappity flappity Iron is 3910 Credits"),
                                Pair.of("flappity fluppity flippity flippity is 42", "how much is flappity fluppity flippity flippity ?"),
                                Pair.of("flappity fluppity flippity flippity is 42", "how much is flappity fluppity flippity flippity?"),
                                Pair.of("flippity floppity Silver is 68.00 Credits", "how many Credits is flippity floppity Silver ?"),
                                Pair.of("flippity floppity Gold is 57800.00 Credits", "how many Credits is flippity floppity Gold ?"),
                                Pair.of("flippity floppity Iron is 782.00 Credits", "how many Credits is flippity floppity Iron ?"),
                                Pair.of("flippity floppity Silver is 68.00 Credits", "how many Credits is flippity floppity Silver?"),//Without the space before the '?'
                                Pair.of("flippity floppity Gold is 57800.00 Credits", "how many Credits is flippity floppity Gold?"),
                                Pair.of("flippity floppity Iron is 782.00 Credits", "how many Credits is flippity floppity Iron?")
                        ),
                        "longer alien words"),
                Arguments.of(
                        List.of(
                                Pair.of("", "glob is i"),
                                Pair.of("", "prok is v"),
                                Pair.of("", "pish is x"),
                                Pair.of("", "tegj is l"),
                                Pair.of("", "glob glob Silver is 34 Credits"),
                                Pair.of("", "glob prok Gold is 57800 Credits"),
                                Pair.of("", "pish pish Iron is 3910 Credits"),
                                Pair.of("pish tegj glob glob is 42", "how much is pish tegj glob glob ?"),
                                Pair.of("pish tegj glob glob is 42", "how much is pish tegj glob glob?"),
                                Pair.of("glob prok Silver is 68.00 Credits", "how many Credits is glob prok Silver ?"),
                                Pair.of("glob prok Gold is 57800.00 Credits", "how many Credits is glob prok Gold ?"),
                                Pair.of("glob prok Iron is 782.00 Credits", "how many Credits is glob prok Iron ?"),
                                Pair.of("glob prok Silver is 68.00 Credits", "how many Credits is glob prok Silver?"),//Without the space before the '?'
                                Pair.of("glob prok Gold is 57800.00 Credits", "how many Credits is glob prok Gold?"),
                                Pair.of("glob prok Iron is 782.00 Credits", "how many Credits is glob prok Iron?")
                        ),
                        "Lower Case Roman Numerals in query")

        );
    }

    public static Stream<Arguments> getUnsuccessfulValues_notValidQueries() {
        return Stream.of(
                "my spoon is too big",
                "I said my spoon is too big",
                "I am a banana",
                "how much wood could a woodchuck chuck if a woodchuck could chuck wood?",
                "Sausages",
                "flim is ",//missing roman numeral
                "flom is g",//not a roman numeral
                "flam is adfgasdfg",//just garbage,
                " is I",//blank alien word
                "is I",//no alien word
                "glob prok Silver is  Credits",//no value
                "glob prok Silver is Credits",//no value
                "how many Credits is Silver?",
                "how many Credits is glod?",
                "how many Credits is ?"
        ).map(Arguments::of);
    }

    public static Stream<Arguments> getUnsuccessfulValues_noData() {
        return Stream.of(
                Arguments.of(
                        List.of(),
                        "how much is yippy dippy?",
                        "I don't know what yippy is"),
                Arguments.of(
                        List.of("yippy is I"),
                        "how much is yippy dippy?",
                        "I don't know what dippy is"),
                Arguments.of(
                        List.of(),
                        "how many Credits is yippy dippy Gold?",
                        "I don't know what yippy is"),
                Arguments.of(
                        List.of(
                                "yippy is I"),
                        "how many Credits is yippy dippy Gold?",
                        "I don't know what dippy is"),
                Arguments.of(
                        List.of(
                                "yippy is I",
                                "dippy is V"),
                        "how many Credits is yippy dippy Gold?",
                        "I don't know what Gold is")
        );
    }
}