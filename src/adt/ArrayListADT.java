package adt;

import java.util.Comparator;

public class ArrayListADT<T> implements ListInterface<T> {
    private final T[] array;
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 50;

    public ArrayListADT() {
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Object[DEFAULT_CAPACITY];
        array = temp;
        numberOfEntries = 0;
    }

    @Override
    public boolean add(T newEntry) {
        if (newEntry == null) return false;
        array[numberOfEntries] = newEntry;
        numberOfEntries++;
        return true;
    }

    @Override
    public boolean add(int newPosition, T newEntry) {
        if (newPosition < 1 || newPosition > numberOfEntries + 1) return false;

        for (int i = numberOfEntries; i >= newPosition; i--) {
            array[i] = array[i - 1];
        }

        array[newPosition - 1] = newEntry;
        numberOfEntries++;
        return true;
    }

    @Override
    public boolean remove(T anEntry) {
        int index = indexOf(anEntry);
        if (index != -1) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public T remove(int givenPosition) {
        if (givenPosition < 1 || givenPosition > numberOfEntries) return null;

        T result = array[givenPosition - 1];

        for (int i = givenPosition - 1; i < numberOfEntries - 1; i++) {
            array[i] = array[i + 1];
        }

        numberOfEntries--;
        return result;
    }

    @Override
    public boolean replace(int givenPosition, T newEntry) {
        if (givenPosition < 1 || givenPosition > numberOfEntries) return false;
        array[givenPosition - 1] = newEntry;
        return true;
    }

    @Override
    public T getEntry(int givenPosition) {
        if (givenPosition < 1 || givenPosition > numberOfEntries) return null;
        return array[givenPosition - 1];
    }

    @Override
    public int indexOf(T anEntry) {
        for (int i = 0; i < numberOfEntries; i++) {
            if (array[i].equals(anEntry)) {
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(T anEntry) {
        return indexOf(anEntry) != -1;
    }

    @Override
    public void clear() {
        numberOfEntries = 0;
    }

    @Override
    public int getLength() {
        return numberOfEntries;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public T[] toArray() {
        return array;
    }

    @Override
    public void sort(Comparator<T> comparator) {
        for (int i = 0; i < numberOfEntries - 1; i++) {
            for (int j = 0; j < numberOfEntries - i - 1; j++) {
                if (comparator.compare(array[j], array[j + 1]) > 0) {
                    T temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }
}
