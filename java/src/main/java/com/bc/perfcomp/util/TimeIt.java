package com.bc.perfcomp.util;

/**
 * @author Norman Fomferra
 */
public class TimeIt {
    public static double timeit(Code code, int times) {
        code.setup();
        long t1 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            code.stmt();
        }
        long t2 = System.nanoTime();
        return (t2 - t1) * 1.0e-9;
    }

    public interface Code {
        void stmt();

        default void setup() {
        }
    }
}
