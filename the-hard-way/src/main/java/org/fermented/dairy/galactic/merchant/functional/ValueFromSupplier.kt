package org.fermented.dairy.galactic.merchant.functional

import java.util.function.Supplier


/**
 * Supplier that can supply a value or get from another supplier
 *
 * @param <T> the type being supplied
 */
//This is deliberate as part of the class design
class ValueFromSupplier<T> private constructor(value: T?, supplier: Supplier<T>?) : Supplier<T> {
    private var value: T?
    private var supplier: Supplier<T>?

    init {
        this.value = value
        this.supplier = supplier

        require(!(this.value == null && this.supplier == null)) { "Supplier cannot be empty is value is empty" }

        if (this.value != null) this.supplier = null
    }


    /**
     * Gets a result.
     *
     * @return a result
     */
    override fun get(): T {
        return value?.let { return it }
            ?: fromSupplier()
    }

    private fun fromSupplier(): T {
        val supplied = supplier!!.get()
        value = supplied
        supplier = null
        return supplied
    }

    companion object {
        /**
         * Create a supplier for the value
         *
         * @param value the value
         * @return A supplier supplying the value provided
         *
         * @param <T> the type being supplied
        </T> */
        fun <T> value(value: T): ValueFromSupplier<T> {
            require(value != null) { "'value' cannot be null" }
            return ValueFromSupplier(value, null)
        }

        /**
         * Create a supplier that transitively calls the provided supplier.
         * Once initially supplied, the supplier will retain the value internally and supply that value instead of getting it from the supplier again
         *
         * @param amountSupplier the supplier being transitively called
         * @return A supplier transitively calling the provided supplier
         *
         * @param <T> the type being supplied
        </T> */
        fun <T> supplier(amountSupplier: Supplier<T>): ValueFromSupplier<T> {
            return ValueFromSupplier(null, amountSupplier)
        }

    }
}
