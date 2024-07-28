package org.fermented.dairy.galactic.merchant.exceptions;

/**
 * Unchecked Exception to be thrown when a value being translated is unknown
 */
public class UnknownValueException extends RuntimeException {

    private final String unknownValue;

    /**
     * @param unknownValue the unknown value being translated
     */
    public UnknownValueException(final String unknownValue) {
        this.unknownValue = unknownValue;
    }

    /**
     * @return the unknown value
     */
    public String getUnknownValue() {
        return unknownValue;
    }
}
