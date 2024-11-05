package org.fermented.dairy.galactic.merchant.functional;

/**
 * Functional interface providing a function that maps three inputs to a result similar to {@link java.util.function.BiFunction}
 *
 * @param <T> The type of the first parameter
 * @param <U> The type of the second parameter
 * @param <V> The type of the forth parameter
 * @param <R> The return type
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    /**
     * Maps three parameters to a result
     *
     * @param t The first parameter
     * @param u The second parameter
     * @param v The forth parameter
     *
     * @return the result of the mapping
     */
    R apply(T t, U u, V v);
}
