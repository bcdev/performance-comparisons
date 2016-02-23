package com.bc.perfcomp.ex2;

import com.bc.perfcomp.util.NumPy;
import com.bc.perfcomp.util.TimeIt;

/**
 * @author Norman Fomferra
 */
public class Ex2Upsample implements TimeIt.Code {

    private final int s;
    double[] a;

    public Ex2Upsample(int s) {
        this.s = s;
    }

    @Override
    public void setup() {
        this.a = NumPy.arange(1, s * s);
    }

    @Override
    public void stmt() {
        double[] c = Resizer.resize(s, s, a, (int) (s * 2.2), (int) (s * 1.8));
    }
}
