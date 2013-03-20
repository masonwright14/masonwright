package automata;

import java.util.List;
import java.util.Set;

public abstract class Util {

    public static void print(final int state, final int index, final int symbol) {
        System.out.println("State: " + state + "\tIndex: " + index + "\tSymbol: " + symbol);
    }
    
    public static void printArray(final int arr[]) {
        System.out.print('[');
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(" ");
            }
        }
        System.out.print("] ");
    }
    
    public static String stringifyArray(final int arr[]) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(" ");
            }
        }
        sb.append("] ");
        
        return sb.toString();
    }
    
    public static String stringifySet(final Set<Integer> set) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int currentItem: set) {
            sb.append(currentItem);
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static String stringifyArray(final List<Integer> arr[]) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(" ");
            }
        }
        sb.append("] ");
        
        return sb.toString();
    }
    
    public static String stringify2dArray(final int arr[][]) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < arr.length; i++) {
            sb.append("\t");
            sb.append(stringifyArray(arr[i]));
            sb.append("\n");
        }
        sb.append(" ] ");
        
        return sb.toString();
    }
    
    public static String stringify2dArray(final List<Integer> arr[][]) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < arr.length; i++) {
            sb.append("\t");
            sb.append(stringifyArray(arr[i]));
            sb.append("\n");
        }
        sb.append(" ] ");
        
        return sb.toString();
    }
}
