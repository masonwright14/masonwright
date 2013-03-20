package edu.vanderbilt.hw3.driver;

/**
 * A utility class.
 * 
 * @author Mason Wright
 */
public abstract class Util {

    /**
     * Copies a 2D array. Used to make defensive copies.
     * Assumes that the input array has the same number of items
     * in each sub-array.
     * 
     * @param input the input 2D array
     * @return a copy of the 2D array
     */
    public static boolean[][] copy2dArray(final boolean[][] input) {
        boolean[][] result = new boolean[input.length][];
        for (int outer = 0; outer < input.length; outer++) {
            result[outer] = new boolean[input[outer].length];
            for (int inner = 0; inner < input[outer].length; inner++) {
                result[outer][inner] = input[outer][inner];
            }
        }
        
        return result;
    }
}
