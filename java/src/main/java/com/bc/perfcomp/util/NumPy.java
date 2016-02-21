package com.bc.perfcomp.util;

/**
 * @author Norman Fomferra
 */
public class NumPy {
    public static double[] arange(int x1, int x2) {
        int n = x2 - x1 + 1;
        double[] a = new double[n];
        for (int i = 0; i < n; i++) {
            a[i] = i + x1;
        }
        return a;
    }
}
