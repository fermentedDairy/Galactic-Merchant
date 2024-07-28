package org.fermented.dairy.galactic.merchant.model;

/**
 * Parent interface for query data classes
 */
public sealed interface QueryData
        permits CompleteValueHintQueryData,
                GetValueQueryData,
                MapToRomanQueryData,
                SimpleTranslationData,
                UnknownQueryData {
}

