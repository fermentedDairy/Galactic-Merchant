package org.fermented.dairy.galactic.merchant.model

/**
 * Parent interface for query data classes
 */
sealed interface QueryData {
    /**
     * Models a query providing a complete transaction
     *
     * @param alienPhrase The alien phrase
     * @param metal The metal being traded
     * @param amount The total value for the trade
     */
    @JvmRecord
    data class CompleteValueHintQueryData(val alienPhrase: String, val metal: String, val amount: Int) : QueryData

    /**
     * Models get data queries
     *
     * @param alienWords a set of space delimited alien words
     * @param metal The metal being traded
     */
    @JvmRecord
    data class GetValueQueryData(val alienWords: String, val metal: String) : QueryData

    /**
     * Models query data for queries that provide alien word to roman numeral mapping data.
     *
     * @param alienWord The alien word
     * @param romanNumeral The roman numeral
     */
    @JvmRecord
    data class MapToRomanQueryData(val alienWord: String, val romanNumeral: Char) : QueryData

    /**
     * Models simple translation query data
     *
     * @param alienPhrase the alien phrase in the query to translate
     */
    @JvmRecord
    data class SimpleTranslationData(val alienPhrase: String) : QueryData

    /**
     * Record modelling unknown query data
     * @param query The query that could not be recognised
     */
    @JvmRecord
    data class UnknownQueryData(val query: String) : QueryData
}

