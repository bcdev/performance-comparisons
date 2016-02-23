package com.bc.perfcomp.ex1;

import com.bc.perfcomp.util.TimeIt;

import java.util.Arrays;
import java.util.Locale;

import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * @author Marco Zühlke
 */
public class Ex1Parallel implements TimeIt.Code {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);

        int times = 100;
        int s = 2;
        for (int k = 0; k < 20; k++) {
            TimeIt.Code ex1 = new Ex1Parallel(s);
            double time = TimeIt.timeit(ex1, times);
            System.out.printf("%d\t%d\t%f\n", k + 1, s, time);
            s *= 2;
        }
    }

    private final int s;
    double[] a, b;

    public Ex1Parallel(int s) {
        this.s = s;
    }

    @Override
    public void setup() {
        this.a = arangeParallel(1, s);
        this.b = arangeParallel(1, s);
    }

    @Override
    public void stmt() {
        double[] c = ex1Parallel(this.a, this.b);
    }

    private static double[] ex1Parallel(double[] a, double[] b) {
        double[] c = new double[a.length];
        Arrays.parallelSetAll(c, i -> sin(2.2 * a[i] - 3.3 * b[i]) / sqrt(4.4 * a[i] + 5.5 * b[i]));
        return c;
    }

    public static double[] arangeParallel(int x1, int x2) {
        int n = x2 - x1 + 1;
        double[] a = new double[n];
        Arrays.parallelSetAll(a, i -> i + x1);
        return a;
    }

}
