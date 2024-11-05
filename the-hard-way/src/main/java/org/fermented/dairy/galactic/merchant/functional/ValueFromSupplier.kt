package org.fermented.dairy.galactic.merchant.functional;


import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Supplier that can supply a value or get from another supplier
 *
 * @param <T> the type being supplied
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")//This is deliberate as part of the class design
public class ValueFromSupplier<T> implements Supplier<T> {

    private Optional<T> value;

    private Optional<Supplier<T>> supplier;

    private ValueFromSupplier(final T value, final Supplier<T> supplier) {

        this.value = Optional.ofNullable(value);
        this.supplier = Optional.ofNullable(supplier);

        if (this.value.isEmpty() && this.supplier.isEmpty())
            throw new IllegalArgumentException("Supplier cannot be empty is value is empty");

        if (this.value.isPresent())
            this.supplier = Optional.empty();
    }


    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public T get() {

        if (value.isPresent())
            return value.get();

        //noinspection OptionalGetWithoutIsPresent Checked in constructor
        final T fromSupplier = supplier.get().get();
        value = Optional.ofNullable(fromSupplier);

        if (value.isPresent())
            supplier = Optional.empty();// allow supplier to be GCed

        return fromSupplier;
    }

    /**
     * Create a supplier for the value
     *
     * @param value the value
     * @return A supplier supplying the value provided
     *
     * @param <T> the type being supplied
     */
    public static <T> Supplier<T> value(T value) {
        Objects.requireNonNull(value, "'value' cannot be null");
        return new ValueFromSupplier<>(value, null);
    }

    /**
     * Create a supplier that transitively calls the provided supplier.
     * Once initially supplied, the supplier will retain the value internally and supply that value instead of getting it from the supplier again
     *
     * @param supplier the supplier being transitively called
     * @return A supplier transitively calling the provided supplier
     *
     * @param <T> the type being supplied
     */
    public static <T> Supplier<T> supplier(Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "'value' cannot be null");
        return new ValueFromSupplier<>(null, supplier);
    }
}
