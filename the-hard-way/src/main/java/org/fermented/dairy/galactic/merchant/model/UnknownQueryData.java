package org.fermented.dairy.galactic.merchant.model;

/**
 * Record modelling unknown query data
 * @param query The query that could not be recognised
 */
public record UnknownQueryData(String query) implements QueryData{}
