package org.fermented.dairy.galactic.merchant.extensions

import java.util.function.Supplier

fun <K, V, E : RuntimeException> Map<K, V>.orThrow(key: K, exceptionSupplier: Supplier<out E>): V {
    return this[key] ?: throw exceptionSupplier.get()
}