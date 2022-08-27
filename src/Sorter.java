public class Sorter {
    private final SortingMode mode;

    public Sorter(SortingMode mode) {
        this.mode = mode;
    }

    public String[] sort(String[] array) {
        array = mergeSortString(array);
        return array;
    }

    public int[] sort(int[] array) {
        array = mergeSortInteger(array);
        return array;
    }

    public int[] mergeSortInteger(int[] array) {
        int[] tmp;
        int[] currentSrc = array;
        int[] currentDest = new int[array.length];

        int size = 1;
        while (size < array.length) {
            for (int i = 0; i < array.length; i += 2 * size) {
                mergeInteger(currentSrc, i, currentSrc, i + size, currentDest, i, size);
            }
            tmp = currentSrc;
            currentSrc = currentDest;
            currentDest = tmp;
            size = size * 2;
        }
        return currentSrc;
    }

    public void mergeInteger(int[] src1, int src1Start, int[] src2, int src2Start,
                             int[] dest, int destStart, int size) {
        int index1 = src1Start;
        int index2 = src2Start;

        int src1End = Math.min(src1Start + size, src1.length);
        int src2End = Math.min(src2Start + size, src2.length);

        if (src1Start + size > src1.length) {
            if (src1End - src1Start >= 0) System.arraycopy(src1, src1Start, dest, src1Start, src1End - src1Start);
            return;
        }

        int iterationCount = src1End - src1Start + src2End - src2Start;

        for (int i = destStart; i < destStart + iterationCount; i++) {
            if (mode.equals(SortingMode.ASC)) {
                if (index1 < src1End && (index2 >= src2End || src1[index1] < src2[index2])) {
                    dest[i] = src1[index1];
                    index1++;
                } else {
                    dest[i] = src2[index2];
                    index2++;
                }
            } else {
                if (index1 < src1End && (index2 >= src2End || src1[index1] > src2[index2])) {
                    dest[i] = src1[index1];
                    index1++;
                } else {
                    dest[i] = src2[index2];
                    index2++;
                }
            }
        }
    }

    public String[] mergeSortString(String[] array) {
        String[] tmp;
        String[] currentSrc = array;
        String[] currentDest = new String[array.length];

        int size = 1;
        while (size < array.length) {
            for (int i = 0; i < array.length; i += 2 * size) {
                mergeString(currentSrc, i, currentSrc, i + size, currentDest, i, size);
            }
            tmp = currentSrc;
            currentSrc = currentDest;
            currentDest = tmp;
            size = size * 2;
        }
        return currentSrc;
    }

    public void mergeString(String[] src1, int src1Start, String[] src2, int src2Start,
                            String[] dest, int destStart, int size) {
        int index1 = src1Start;
        int index2 = src2Start;

        int src1End = Math.min(src1Start + size, src1.length);
        int src2End = Math.min(src2Start + size, src2.length);

        if (src1Start + size > src1.length) {
            if (src1End - src1Start >= 0) System.arraycopy(src1, src1Start, dest, src1Start, src1End - src1Start);
            return;
        }

        int iterationCount = src1End - src1Start + src2End - src2Start;

        for (int i = destStart; i < destStart + iterationCount; i++) {
            if (mode.equals(SortingMode.ASC)) {
                if (index1 < src1End && (index2 >= src2End || src1[index1].compareTo(src2[index2]) <= 0)) {
                    dest[i] = src1[index1];
                    index1++;
                } else {
                    dest[i] = src2[index2];
                    index2++;
                }
            } else {
                if (index1 < src1End && (index2 >= src2End || src1[index1].compareTo(src2[index2]) > 0)) {
                    dest[i] = src1[index1];
                    index1++;
                } else {
                    dest[i] = src2[index2];
                    index2++;
                }
            }
        }
    }

    public void mergeArraysInteger(int[] array1, int[] array2, int[] array3, SortingMode mode) {
        boolean comparison = mode.equals(SortingMode.ASC);
        int i = 0, j = 0;
        for (int k = 0; k < array3.length; k++) {
            if (i > array1.length - 1) {
                int array = array2[j];
                array3[k] = array;
                j++;
            } else if (j > array2.length - 1) {
                int array = array1[i];
                array3[k] = array;
                i++;
            } else if ((array1[i] < array2[j]) == comparison) {
                int a = array1[i];
                array3[k] = a;
                i++;
            } else {
                int b = array2[j];
                array3[k] = b;
                j++;
            }
        }
    }

    public void mergeArraysString(String[] array1, String[] array2, String[] array3, SortingMode mode) {
        boolean comparison = mode.equals(SortingMode.ASC);
        int i = 0, j = 0;
        for (int k = 0; k < array3.length; k++) {
            if (i > array1.length - 1) {
                String array = array2[j];
                array3[k] = array;
                j++;
            } else if (j > array2.length - 1) {
                String array = array1[i];
                array3[k] = array;
                i++;
            } else if ((array1[i].compareTo(array2[j]) <= 0) == comparison) {
                String a = array1[i];
                array3[k] = a;
                i++;
            } else {
                String b = array2[j];
                array3[k] = b;
                j++;
            }
        }
    }
}
