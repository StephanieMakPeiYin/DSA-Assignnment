package adt;

import java.util.Comparator;

public interface ListInterface<T> {
    boolean add(T newEntry);
    boolean add(int newPosition, T newEntry);
    boolean remove(T anEntry);
    T remove(int givenPosition);
    boolean replace(int givenPosition, T newEntry);
    T getEntry(int givenPosition);
    int indexOf(T anEntry);
    boolean contains(T anEntry);
    void clear();
    int getLength();
    boolean isEmpty();
    T[] toArray();
    void sort(Comparator<T> comparator);
}
