package Mods.create_tempratech.Client;

import java.util.Arrays;
import java.util.function.BiConsumer;

public final class IntDoubleHashTable {

    private static final byte EMPTY = 0;
    private static final byte USED = 1;
    private static final byte DELETED = 2;

    private static final float LOAD_FACTOR = 0.65f;

    private int[] keys;
    private double[] values;
    private byte[] states;

    private int size;
    private int usedSlots;
    private int resizeThreshold;

    public IntDoubleHashTable() {
        this(16);
    }

    public IntDoubleHashTable(int initialCapacity) {
        int capacity = nextPowerOfTwo(Math.max(2, initialCapacity));

        keys = new int[capacity];
        values = new double[capacity];
        states = new byte[capacity];

        resizeThreshold = (int) (capacity * LOAD_FACTOR);
    }

    public double get(int key) {
        int index = findIndex(key);

        return index >= 0 ? values[index] : 0.0;
    }

    public boolean containsKey(int key) {
        return findIndex(key) >= 0;
    }

    public void put(int key, double value) {
        if (usedSlots >= resizeThreshold) {
            resize(keys.length << 1);
        }

        int index = findSlot(key);

        if (states[index] != USED) {
            if (states[index] == EMPTY) {
                usedSlots++;
            }

            states[index] = USED;
            keys[index] = key;
            values[index] = value;
            size++;
        } else {
            values[index] = value;
        }
    }

    public void add(int key, double amount) {
        if (usedSlots >= resizeThreshold) {
            resize(keys.length << 1);
        }

        int index = findSlot(key);

        if (states[index] != USED) {
            if (states[index] == EMPTY) {
                usedSlots++;
            }

            states[index] = USED;
            keys[index] = key;
            values[index] = amount;
            size++;
        } else {
            values[index] += amount;
        }
    }

    public boolean remove(int key) {
        int index = findIndex(key);

        if (index < 0) {
            return false;
        }

        states[index] = DELETED;
        values[index] = 0.0;
        size--;

        return true;
    }

    public void clear() {
        Arrays.fill(states, EMPTY);
        Arrays.fill(values, 0.0);

        size = 0;
        usedSlots = 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return keys.length;
    }

    public void forEach(BiConsumer<Integer, Double> consumer) {
        for (int i = 0; i < keys.length; i++) {
            if (states[i] == USED) {
                consumer.accept(keys[i], values[i]);
            }
        }
    }

    private int findIndex(int key) {
        int mask = keys.length - 1;
        int index = hash(key) & mask;

        while (states[index] != EMPTY) {
            if (states[index] == USED && keys[index] == key) {
                return index;
            }

            index = (index + 1) & mask;
        }

        return -1;
    }

    private int findSlot(int key) {
        int mask = keys.length - 1;
        int index = hash(key) & mask;

        int firstDeleted = -1;

        while (true) {
            byte state = states[index];

            if (state == EMPTY) {
                return firstDeleted >= 0
                        ? firstDeleted
                        : index;
            }

            if (state == USED && keys[index] == key) {
                return index;
            }

            if (state == DELETED && firstDeleted < 0) {
                firstDeleted = index;
            }

            index = (index + 1) & mask;
        }
    }

    private void resize(int newCapacity) {
        int[] oldKeys = keys;
        double[] oldValues = values;
        byte[] oldStates = states;

        keys = new int[newCapacity];
        values = new double[newCapacity];
        states = new byte[newCapacity];

        size = 0;
        usedSlots = 0;
        resizeThreshold = (int) (newCapacity * LOAD_FACTOR);

        for (int i = 0; i < oldKeys.length; i++) {
            if (oldStates[i] == USED) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }

    private static int hash(int key) {
        key ^= key >>> 16;
        key *= 0x7feb352d;
        key ^= key >>> 15;
        key *= 0x846ca68b;
        key ^= key >>> 16;

        return key;
    }

    private static int nextPowerOfTwo(int value) {
        int result = 1;

        while (result < value) {
            result <<= 1;
        }

        return result;
    }
}