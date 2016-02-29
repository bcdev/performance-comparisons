package com.bc.perfcomp.ex2;

import com.bc.perfcomp.util.TimeIt;

import java.util.Locale;

/**
 * Created by Norman on 23.02.2016.
 */
public class RunEx2 {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);

        int times = 100;
        int N = 7;

        System.out.print("\nUpsample:");
        System.out.printf("%s\t%s\n", "No", "Size", "Java");
        int s = 4;
        for (int k = 0; k < N; k++) {
            TimeIt.Code ex2 = new Ex2Upsample(s);
            double t1 = TimeIt.timeit(ex2, times);
            System.out.printf("%d\t%d\t%f\n", k + 1, s, t1);
            s *= 2;
        }

        System.out.print("\nDownsample:");
        System.out.printf("%s\t%s\n", "No", "Size", "Java");
        s = 8;
        for (int k = 0; k < N; k++) {
            TimeIt.Code ex2 = new Ex2Downsample(s);
            double t1 = TimeIt.timeit(ex2, times);
            System.out.printf("%d\t%d\t%f\n", k + 1, s, t1);
            s *= 2;
        }
    }
}
