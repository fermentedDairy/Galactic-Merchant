package org.fermented.dairy.galactic.merchant.model;

/**
 * Models a query providing a complete transaction
 *
 * @param alienPhrase The alien phrase
 * @param metal The metal being traded
 * @param amount The total value for the trade
 */
public record CompleteValueHintQueryData(String alienPhrase, String metal, int amount) implements QueryData{
}
