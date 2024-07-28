package org.fermented.dairy.galactic.merchant.model;

/**
 * Models query data for queries that provide alien word to roman numeral mapping data.
 *
 * @param alienWord The alien word
 * @param romanNumeral The roman numeral
 */
public record MapToRomanQueryData(String alienWord, char romanNumeral) implements QueryData{
}
