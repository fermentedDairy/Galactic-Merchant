package org.fermented.dairy.galactic.merchant.model;

/**
 * Models get data queries
 *
 * @param alienWords a set of space delimited alien words
 * @param metal The metal being traded
 */
public record GetValueQueryData(String alienWords, String metal) implements QueryData {
}
