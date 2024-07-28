package org.fermented.dairy.galactic.merchant.model;

/**
 * Models simple translation query data
 *
 * @param alienPhrase the alien phrase in the query to translate
 */
public record SimpleTranslationData(String alienPhrase) implements QueryData {
}
