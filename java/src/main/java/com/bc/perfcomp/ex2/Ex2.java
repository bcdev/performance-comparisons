package com.bc.perfcomp.ex2;

import com.bc.perfcomp.util.NumPy;
import com.bc.perfcomp.util.TimeIt;

import java.util.Locale;

import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * @author Norman Fomferra
 */
public class Ex2 implements TimeIt.Code {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);

        int times = 100;
        int s = 2;
        for (int k = 0; k < 20; k++) {
            TimeIt.Code ex1 = new Ex2(s);
            double time = TimeIt.timeit(ex1, times);
            System.out.printf("%d\t%d\t%f\n", k + 1, s, time);
            s *= 2;
        }
    }

    private final int s;
    double[] a, b;

    public Ex2(int s) {
        this.s = s;
    }

    @Override
    public void setup() {
        this.a = NumPy.arange(1, s);
        this.b = NumPy.arange(1, s);
    }

    @Override
    public void stmt() {
        double[] c = ex1(this.a, this.b);
    }

    private static double[] ex1(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int i = 0; i < c.length; i++) {
            c[i] = sin(2.2 * a[i] - 3.3 * b[i]) / sqrt(4.4 * a[i] + 5.5 * b[i]);
        }
        return c;
    }
}
