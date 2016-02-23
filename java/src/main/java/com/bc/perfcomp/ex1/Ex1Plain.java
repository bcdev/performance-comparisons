package com.bc.perfcomp.ex1;

import com.bc.perfcomp.util.NumPy;
import com.bc.perfcomp.util.TimeIt;

import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * @author Norman Fomferra
 */
public class Ex1Plain implements TimeIt.Code {

    private final int s;
    double[] a, b;

    public Ex1Plain(int s) {
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
