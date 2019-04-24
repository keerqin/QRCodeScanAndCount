package com.keerqin.springboot02.testKeerqin;

public class testBiSearch {
    public static void main(String[] args) {
        int[] arr = {-30,-10,0,2,5,7,10,45};
        System.out.println(BiSearch(arr,2));
    }

    public static int BiSearch(int[] arr, int key) {

        int low = 0;
        int middle = 0;
        int high = arr.length - 1;

        while (low <= high) {
            middle = (low + high) / 2;
            if (arr[middle] < key) {
                //在右半边
                low = middle + 1;
            } else if (arr[middle] > key) {
                //在左半边
                high = middle - 1;
            } else {
                return middle;
            }
        }
        return -1;
    }
}
