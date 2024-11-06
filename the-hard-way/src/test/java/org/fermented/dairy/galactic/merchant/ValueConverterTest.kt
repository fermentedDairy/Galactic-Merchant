package org.fermented.dairy.galactic.merchant

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection

class ValueConverterTest : StringSpec({

    "Successful translations" {
        checkAll(Exhaustive.collection(getSuccessValues())) { requestResponsePairs ->
            run {
                val translator = ValueConverter()
                requestResponsePairs.forEach { requestResponse ->
                    translator.acceptInput(requestResponse.request) shouldBe
                            requestResponse.response
                }
            }
        }
    }

    "Unsuccessful translations due to invalid queries" {
        checkAll(Exhaustive.collection(getUnsuccessfulValues_notValidQueries())) { value ->
            run {
                val translator = ValueConverter()
                translator.acceptInput(value) shouldBe "I have no idea what you are talking about"
            }
        }
    }

    "Unsuccessful translations due to insufficient data" {
        checkAll(Exhaustive.collection(getUnsuccessfulValues_noData())) { conversationAndRequestResponsePair ->
            run {
                val translator = ValueConverter()
                conversationAndRequestResponsePair.seedRequests.forEach {
                    request -> translator.acceptInput(request)
                }
                conversationAndRequestResponsePair.finalRequestResponse.response shouldBe
                        conversationAndRequestResponsePair.finalRequestResponse.response
            }
        }
    }
})

fun getSuccessValues(): List<List<RequestResponsePair>> {
    return listOf(
        listOf(//From examples
            RequestResponsePair(response = "", request = "glob is I"),
            RequestResponsePair(response = "", request = "prok is V"),
            RequestResponsePair(response = "", request = "pish is X"),
            RequestResponsePair(response = "", request = "tegj is L"),
            RequestResponsePair(response = "", request = "glob glob Silver is 34 Credits"),
            RequestResponsePair(response = "", request = "glob prok Gold is 57800 Credits"),
            RequestResponsePair(response = "", request = "pish pish Iron is 3910 Credits"),
            RequestResponsePair(response = "pish tegj glob glob is 42", request = "how much is pish tegj glob glob ?"),
            RequestResponsePair(response = "pish tegj glob glob is 42", request = "how much is pish tegj glob glob?"),
            RequestResponsePair(
                response = "glob prok Silver is 68.00 Credits",
                request = "how many Credits is glob prok Silver ?"
            ),
            RequestResponsePair(
                response = "glob prok Gold is 57800.00 Credits",
                request = "how many Credits is glob prok Gold ?"
            ),
            RequestResponsePair(
                response = "glob prok Iron is 782.00 Credits",
                request = "how many Credits is glob prok Iron ?"
            ),
            RequestResponsePair(
                response = "glob prok Silver is 68.00 Credits",
                request = "how many Credits is glob prok Silver?"
            ),  //Without the space before the '?'
            RequestResponsePair(
                response = "glob prok Gold is 57800.00 Credits",
                request = "how many Credits is glob prok Gold?"
            ),
            RequestResponsePair(
                response = "glob prok Iron is 782.00 Credits",
                request = "how many Credits is glob prok Iron?"
            )
        ),
        listOf(//longer alien words
            RequestResponsePair(response = "", request = "flippity is I"),
            RequestResponsePair(response = "", request = "floppity is V"),
            RequestResponsePair(response = "", request = "flappity is X"),
            RequestResponsePair(response = "", request = "fluppity is L"),
            RequestResponsePair(response = "", request = "flippity flippity Silver is 34 Credits"),
            RequestResponsePair(response = "", request = "flippity floppity Gold is 57800 Credits"),
            RequestResponsePair(response = "", request = "flappity flappity Iron is 3910 Credits"),
            RequestResponsePair(
                response = "flappity fluppity flippity flippity is 42",
                request = "how much is flappity fluppity flippity flippity ?"
            ),
            RequestResponsePair(
                response = "flappity fluppity flippity flippity is 42",
                request = "how much is flappity fluppity flippity flippity?"
            ),
            RequestResponsePair(
                response = "flippity floppity Silver is 68.00 Credits",
                request = "how many Credits is flippity floppity Silver ?"
            ),
            RequestResponsePair(
                response = "flippity floppity Gold is 57800.00 Credits",
                request = "how many Credits is flippity floppity Gold ?"
            ),
            RequestResponsePair(
                response = "flippity floppity Iron is 782.00 Credits",
                request = "how many Credits is flippity floppity Iron ?"
            ),
            RequestResponsePair(
                response = "flippity floppity Silver is 68.00 Credits",
                request = "how many Credits is flippity floppity Silver?"
            ),  //Without the space before the '?'
            RequestResponsePair(
                response = "flippity floppity Gold is 57800.00 Credits",
                request = "how many Credits is flippity floppity Gold?"
            ),
            RequestResponsePair(
                response = "flippity floppity Iron is 782.00 Credits",
                request = "how many Credits is flippity floppity Iron?"
            )
        ),
        listOf(//Lower Case Roman Numerals in query
            RequestResponsePair(response = "", request = "glob is i"),
            RequestResponsePair(response = "", request = "prok is v"),
            RequestResponsePair(response = "", request = "pish is x"),
            RequestResponsePair(response = "", request = "tegj is l"),
            RequestResponsePair(response = "", request = "glob glob Silver is 34 Credits"),
            RequestResponsePair(response = "", request = "glob prok Gold is 57800 Credits"),
            RequestResponsePair(response = "", request = "pish pish Iron is 3910 Credits"),
            RequestResponsePair(
                response = "pish tegj glob glob is 42",
                request = "how much is pish tegj glob glob ?"
            ),
            RequestResponsePair(
                response = "pish tegj glob glob is 42",
                request = "how much is pish tegj glob glob?"
            ),
            RequestResponsePair(
                response = "glob prok Silver is 68.00 Credits",
                request = "how many Credits is glob prok Silver ?"
            ),
            RequestResponsePair(
                response = "glob prok Gold is 57800.00 Credits",
                request = "how many Credits is glob prok Gold ?"
            ),
            RequestResponsePair(
                response = "glob prok Iron is 782.00 Credits",
                request = "how many Credits is glob prok Iron ?"
            ),
            RequestResponsePair(
                response = "glob prok Silver is 68.00 Credits",
                request = "how many Credits is glob prok Silver?"
            ),  //Without the space before the '?'
            RequestResponsePair(
                response = "glob prok Gold is 57800.00 Credits",
                request = "how many Credits is glob prok Gold?"
            ),
            RequestResponsePair(
                response = "glob prok Iron is 782.00 Credits",
                request = "how many Credits is glob prok Iron?"
            )
        ),
        listOf(//"Complete hints before Roman Numeral mapping"
            RequestResponsePair(response = "", request = "glob glob Silver is 34 Credits"),
            RequestResponsePair(response = "", request = "glob prok Gold is 57800 Credits"),
            RequestResponsePair(response = "", request = "pish pish Iron is 3910 Credits"),
            RequestResponsePair(response = "", request = "glob is I"),
            RequestResponsePair(response = "", request = "prok is V"),
            RequestResponsePair(response = "", request = "pish is X"),
            RequestResponsePair(response = "", request = "tegj is L"),
            RequestResponsePair(
                response = "pish tegj glob glob is 42",
                request = "how much is pish tegj glob glob ?"
            ),
            RequestResponsePair(
                response = "pish tegj glob glob is 42",
                request = "how much is pish tegj glob glob?"
            ),
            RequestResponsePair(
                response = "glob prok Silver is 68.00 Credits",
                request = "how many Credits is glob prok Silver ?"
            ),
            RequestResponsePair(
                response = "glob prok Gold is 57800.00 Credits",
                request = "how many Credits is glob prok Gold ?"
            ),
            RequestResponsePair(
                response = "glob prok Iron is 782.00 Credits",
                request = "how many Credits is glob prok Iron ?"
            ),
            RequestResponsePair(
                response = "glob prok Silver is 68.00 Credits",
                request = "how many Credits is glob prok Silver?"
            ),  //Without the space before the '?'
            RequestResponsePair(
                response = "glob prok Gold is 57800.00 Credits",
                request = "how many Credits is glob prok Gold?"
            ),
            RequestResponsePair(
                response = "glob prok Iron is 782.00 Credits",
                request = "how many Credits is glob prok Iron?"
            )
        )
    )
}

fun getUnsuccessfulValues_notValidQueries(): List<String> {
    return listOf(
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
    )
}

fun getUnsuccessfulValues_noData(): List<ConversationAndRequestResponsePair> {
    return listOf(
        ConversationAndRequestResponsePair(
            seedRequests = listOf(),
            RequestResponsePair(
                response = "how much is yippy dippy?",
                request = "I don't know what yippy is"
            )
        ),
        ConversationAndRequestResponsePair(
            seedRequests = listOf("yippy is I"),
            RequestResponsePair(
                response = "how much is yippy dippy?",
                request = "I don't know what dippy is"
            )
        ),
        ConversationAndRequestResponsePair(
            seedRequests = listOf(),
            RequestResponsePair(
                response = "how many Credits is yippy dippy Gold?",
                request = "I don't know what yippy is"
            )
        ),
        ConversationAndRequestResponsePair(
            seedRequests = listOf(
                "yippy is I"
            ),
            RequestResponsePair(
                response = "how many Credits is yippy dippy Gold?",
                request = "I don't know what dippy is"
            )
        ),
        ConversationAndRequestResponsePair(
            seedRequests = listOf(
                "yippy is I",
                "dippy is V"
            ),
            RequestResponsePair(
                response = "how many Credits is yippy dippy Gold?",
                request = "I don't know what Gold is"
            )
        )
    )
}

data class RequestResponsePair(val response: String, val request: String)
data class ConversationAndRequestResponsePair(
    val seedRequests: List<String>,
    val finalRequestResponse: RequestResponsePair
)