package com.bc.perfcomp.ex2;

import org.junit.Test;

import static java.lang.Double.NaN;
import static org.junit.Assert.assertArrayEquals;

/**
 * @author Norman Fomferra
 */
public class GapFiller1Test {

    public static final double F = NaN;

    @Test
    public void test1x2() throws Exception {
        assertArrayEquals(new double[]{3, 4},
                          GapFiller.fillGaps1(1, 2, new double[]{3, 4}, 1),
                          1e-8);
        assertArrayEquals(new double[]{3, 3},
                          GapFiller.fillGaps1(1, 2, new double[]{3, NaN}, 1),
                          1e-8);
        assertArrayEquals(new double[]{NaN, NaN},
                          GapFiller.fillGaps1(1, 2, new double[]{NaN, NaN}, 1),
                          1e-8);
    }

    @Test
    public void test3x1() throws Exception {
        assertArrayEquals(new double[]{2, 3, 4},
                          GapFiller.fillGaps1(3, 1, new double[]{2, 3, 4}, 1),
                          1e-8);
        assertArrayEquals(new double[]{2, 3, 3},
                          GapFiller.fillGaps1(3, 1, new double[]{2, 3, NaN}, 1),
                          1e-8);
        assertArrayEquals(new double[]{3, 3, 3},
                          GapFiller.fillGaps1(3, 1, new double[]{NaN, 3, NaN}, 1),
                          1e-8);
        assertArrayEquals(new double[]{NaN, NaN, NaN},
                          GapFiller.fillGaps1(3, 1, new double[]{NaN, NaN, NaN}, 1),
                          1e-8);
    }

    @Test
    public void test2x2() throws Exception {
        assertArrayEquals(new double[]{1, 2, 3, 4},
                          GapFiller.fillGaps1(2, 2, new double[]{1, 2, 3, 4}, 1),
                          1e-8);
        assertArrayEquals(new double[]{1, 2, 3, (1 + 2 + 3) / 3.0},
                          GapFiller.fillGaps1(2, 2, new double[]{1, 2, 3, NaN}, 1),
                          1e-8);
        assertArrayEquals(new double[]{1, (1 + 3) / 2.0, 3, (1 + 3) / 2.0},
                          GapFiller.fillGaps1(2, 2, new double[]{1, NaN, 3, NaN}, 1),
                          1e-8);

        assertArrayEquals(new double[]{3, 3, 3, 3},
                          GapFiller.fillGaps1(2, 2, new double[]{NaN, NaN, 3, NaN}, 1),
                          1e-8);

        assertArrayEquals(new double[]{NaN, NaN, NaN, NaN},
                          GapFiller.fillGaps1(2, 2, new double[]{NaN, NaN, NaN, NaN}, 1),
                          1e-8);
    }

    @Test
    public void test4x4() throws Exception {
        assertArrayEquals(new double[]{
                                  1, 2, 3, 4,
                                  4, 3, 2, 1,
                                  2, 3, 4, 5,
                                  6, 5, 4, 3
                          },
                          GapFiller.fillGaps1(2, 2, new double[]{
                                  1, 2, 3, 4,
                                  4, 3, 2, 1,
                                  2, 3, 4, 5,
                                  6, 5, 4, 3
                          }, 1),
                          1e-8);

        double f = (1 + 2 + 3 + 4 + 2 + 2 + 3 + 4) / 8.;
        assertArrayEquals(new double[]{
                                  1, 2, 3, 4,
                                  4, f, 2, 1,
                                  2, 3, 4, 5,
                                  6, 5, 4, 3
                          },
                          GapFiller.fillGaps1(4, 4, new double[]{
                                  1, 2, 3, 4,
                                  4, F, 2, 1,
                                  2, 3, 4, 5,
                                  6, 5, 4, 3
                          }, 1),
                          1e-8);

        double f1 = (1 + 2 + 3 + 4 + 2) / 5.;
        double f2 = (2 + 3 + 4) / 3.;
        double f3 = (3 + 4) / 2.;
        double f4 = (4 + 2 + 6) / 3.;
        double f5 = (2 + 6) / 2.;
        double g1 = (f1 + f2 + f3 + f4 + f5) / 5;
        double g2 = (f2 + f3) / 2;
        double g3 = (f4 + f5) / 2;
        double h1 = (g1 + g2 + g3) / 3;
        assertArrayEquals(new double[]{
                                  1., 2., 3., 4.,
                                  4., f1, f2, f3,
                                  2., f4, g1, g2,
                                  6., f5, g3, h1
                          },
                          GapFiller.fillGaps1(4, 4, new double[]{
                                  1, 2, 3, 4,
                                  4, F, F, F,
                                  2, F, F, F,
                                  6, F, F, F
                          }, 1),
                          1e-8);
    }
}
