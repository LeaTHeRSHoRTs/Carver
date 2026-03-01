package com.leathershorts.carver;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PopulatedList<T> extends ArrayList<T> {
    private final int min;

    public PopulatedList(Collection<? extends T> source, int min) {
        super(source);
        this.min = min;
        validate();
    }

    public PopulatedList(Collection<T> source) {
        this(source, 1);
    }

    private void validate() {
        if (this.min < 1) throw new IllegalArgumentException("Minimum cannot be less than 1.");
    }

    @Override
    public boolean isEmpty() {
        return this.size() < this.min;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        if (this.size() < this.min) {
            throw new IteratorListTooSmallException(String.format("List underflow: found %d elements, but a minimum of %d is required.", this.size(), this.min));
        }
        return super.iterator();
    }

    @Override
    public boolean remove(Object o) {
        if (this.size() == this.min) throw new RuntimeException("PopulatedList must have at least " + this.min + " value(s)");
        return super.remove(o);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean modified = super.removeAll(c);
        if (this.size() < this.min) {
            throw new RuntimeException("Bulk remove dropped list size to " + this.size() + ". Min required: " + this.min);
        }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean modified = super.retainAll(c);
        if (this.size() < this.min) {
            throw new RuntimeException("Bulk retain dropped list size to " + this.size() + ". Min required: " + this.min);
        }
        return modified;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Method clear on PopulatedList has no implementation, as if it did then the PopulatedList would have 0 elements when executed.");
    }

    @Override
    public T remove(int index) {
        if (this.size() == this.min) throw new RuntimeException(String.format("PopulatedList must have at least %d value(s)", this.min));
        return super.remove(index);
    }

    @Override
    public @NotNull ListIterator<T> listIterator() {
        if (this.size() < this.min) {
            throw new IteratorListTooSmallException(String.format("List underflow: found %d elements, but a minimum of %d is required.", this.size(), this.min));
        }
        return super.listIterator();
    }

    @Override
    public @NotNull ListIterator<T> listIterator(int index) {
        if (this.size() < this.min) {
            throw new IteratorListTooSmallException(String.format("List underflow: found %d elements, but a minimum of %d is required.", this.size(), this.min));
        }
        return super.listIterator(index);
    }

    public static class IteratorListTooSmallException extends RuntimeException {
        public IteratorListTooSmallException(String s) {
            super(s);
        }
    }
}
