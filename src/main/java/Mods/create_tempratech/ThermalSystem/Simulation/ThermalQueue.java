package Mods.create_tempratech.ThermalSystem.Simulation;

import java.util.Arrays;

public final class ThermalQueue {

    private long[] data;

    private int head;
    private int tail;

    public ThermalQueue() {
        this(256);
    }

    public ThermalQueue(int initialCapacity) {
        data = new long[Math.max(16, initialCapacity)];
    }

    /**
     * Adds a block position to the queue.
     */
    public void add(long value) {
        if (tail >= data.length) {
            compactOrResize();
        }

        data[tail++] = value;
    }

    /**
     * Removes and returns the next block position.
     */
    public long poll() {
        if (isEmpty()) {
            throw new IllegalStateException("Thermal queue is empty");
        }

        return data[head++];
    }

    public boolean isEmpty() {
        return head >= tail;
    }

    public int size() {
        return tail - head;
    }

    public void clear() {
        head = 0;
        tail = 0;
    }

    private void compactOrResize() {

        /*
         * If we've consumed the beginning of the array,
         * move the remaining entries back to the beginning.
         */
        if (head > 0) {

            int remaining = tail - head;

            System.arraycopy(
                    data,
                    head,
                    data,
                    0,
                    remaining
            );

            head = 0;
            tail = remaining;

            return;
        }

        /*
         * Queue is completely full.
         * Double its capacity.
         */
        data = Arrays.copyOf(
                data,
                data.length * 2
        );
    }
}