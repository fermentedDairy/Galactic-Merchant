package org.fermented.dairy.galactic.merchant.exceptions

/**
 * Unchecked Exception to be thrown when a value being translated is unknown
 * @param unknownValue the unknown value being translated
 */
class UnknownValueException ( @JvmField val unknownValue: String ) : RuntimeException()
