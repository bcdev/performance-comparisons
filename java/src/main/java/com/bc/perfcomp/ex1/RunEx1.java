package com.bc.perfcomp.ex1;

import com.bc.perfcomp.util.TimeIt;

import java.util.Locale;

/**
 * Created by Norman on 23.02.2016.
 */
public class RunEx1 {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        int times = 100;
        int s = 2;
        System.out.printf("%s\t%s\t%s\t%s\t%s\n", "No", "Size", "Java", "JavaParallel", "Gain");
        for (int k = 0; k < 20; k++) {
            TimeIt.Code ex1 = new Ex1Plain(s);
            TimeIt.Code ex1Parallel = new Ex1Parallel(s);
            double t1 = TimeIt.timeit(ex1, times);
            double t2 = TimeIt.timeit(ex1Parallel, times);
            System.out.printf("%d\t%d\t%f\t%f\t%f\n", k + 1, s, t1, t2, t1 / t2);
            s *= 2;
        }
    }
}
