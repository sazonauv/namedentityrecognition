package io.dlminer.ner.utils;

import java.util.Comparator;

/**
 * @author Slava Sazonau
 *
 * The class is used as a comparator to receive indices of array elements
 * after sorting. It does not change an input array and only produces indices.
 */
public class ArrayIndexComparator implements Comparator<Integer> {

    private boolean ascending;

    private Double[] array;

    public ArrayIndexComparator(double[] array, boolean ascending) {
        this.ascending = ascending;
        toObjectArray(array);
    }

    private void toObjectArray(double[] array) {
        this.array = new Double[array.length];
        for (int i=0; i<array.length; i++) {
            this.array[i] = new Double(array[i]);
        }
    }

    public Integer[] createIndexArray() {
        Integer[] indexes = new Integer[array.length];
        for (int i=0; i<array.length; i++) {
            indexes[i] = i;
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2) {
        if (ascending) {
            return array[index1].compareTo(array[index2]);
        } else {
            return - array[index1].compareTo(array[index2]);
        }
    }
}
