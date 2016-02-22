package com.bc.perfcomp.ex2;

import org.junit.Test;

import static java.lang.Double.NaN;
import static org.junit.Assert.assertArrayEquals;

/**
 * @author Norman Fomferra
 */
public class GapFiller2Test {

    public static final double F = NaN;

    @Test
    public void test1x2() throws Exception {
        assertArrayEquals(new double[]{3, 4},
                          GapFiller.fillGaps2(1, 2, new double[]{3, 4}, 1),
                          1e-8);
        assertArrayEquals(new double[]{3, 3},
                          GapFiller.fillGaps2(1, 2, new double[]{3, NaN}, 1),
                          1e-8);
        assertArrayEquals(new double[]{NaN, NaN},
                          GapFiller.fillGaps2(1, 2, new double[]{NaN, NaN}, 1),
                          1e-8);
    }

    @Test
    public void test3x1() throws Exception {
        assertArrayEquals(new double[]{2, 3, 4},
                          GapFiller.fillGaps2(3, 1, new double[]{2, 3, 4}, 1),
                          1e-8);
        assertArrayEquals(new double[]{2, 3, 3},
                          GapFiller.fillGaps2(3, 1, new double[]{2, 3, NaN}, 1),
                          1e-8);
        assertArrayEquals(new double[]{3, 3, 3},
                          GapFiller.fillGaps2(3, 1, new double[]{NaN, 3, NaN}, 1),
                          1e-8);
        assertArrayEquals(new double[]{NaN, NaN, NaN},
                          GapFiller.fillGaps2(3, 1, new double[]{NaN, NaN, NaN}, 1),
                          1e-8);
    }

    @Test
    public void test2x2() throws Exception {
        assertArrayEquals(new double[]{1, 2, 3, 4},
                          GapFiller.fillGaps2(2, 2, new double[]{1, 2, 3, 4}, 1),
                          1e-8);
        assertArrayEquals(new double[]{1, 2, 3, (1 + 2 + 3) / 3.0},
                          GapFiller.fillGaps2(2, 2, new double[]{1, 2, 3, NaN}, 1),
                          1e-8);
        assertArrayEquals(new double[]{1, (1 + 3) / 2.0, 3, (1 + 3) / 2.0},
                          GapFiller.fillGaps2(2, 2, new double[]{1, NaN, 3, NaN}, 1),
                          1e-8);

        assertArrayEquals(new double[]{3, 3, 3, 3},
                          GapFiller.fillGaps2(2, 2, new double[]{NaN, NaN, 3, NaN}, 1),
                          1e-8);

        assertArrayEquals(new double[]{NaN, NaN, NaN, NaN},
                          GapFiller.fillGaps2(2, 2, new double[]{NaN, NaN, NaN, NaN}, 1),
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
                          GapFiller.fillGaps2(2, 2, new double[]{
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
                          GapFiller.fillGaps2(4, 4, new double[]{
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
        double g1 = ((1 + 2 + 4 + f1) / 4 + (3 + 4 + f2 + f3) / 4 + (2 + f4 + 6 + f5) / 4) / 3;
        assertArrayEquals(new double[]{
                                  1., 2., 3., 4.,
                                  4., f1, f2, f3,
                                  2., f4, g1, g1,
                                  6., f5, g1, g1
                          },
                          GapFiller.fillGaps2(4, 4, new double[]{
                                  1, 2, 3, 4,
                                  4, F, F, F,
                                  2, F, F, F,
                                  6, F, F, F
                          }, 1),
                          1e-8);
    }

    @Test
    public void test8x8() throws Exception {

        double f1 = (1 + 1 + 1 + 2 + 3) / 5.;
        double f2 = (1 + 1 + 1) / 3.;
        double f3 = (2 + 3 + 4) / 3.;
        double f4 = (3 + 4 + 5) / 3.;
        double f5 = (4 + 5 + 6) / 3.;
        double f6 = (5 + 6 + 7) / 3.;
        double f7 = (6 + 7 + 8 + 8 + 8) / 5.;
        double f8 = (8 + 8 + 8) / 3.;
        double g5 = 0;
        double h1 = 0;
        final double ___________F___________ = F;
        double[][] ds = new double[][]{
                {(1. + 1. + 2. + f1) / 4, (1. + 1. + f2 + f2) / 4, (1. + 1. + f2 + f2) / 4, (1. + 1. + f1 + 2.) / 4},
                {(3. + f3 + 4. + f4) / 4, ___________F___________, ___________F___________, (f3 + 3. + f4 + 4.) / 4},
                {(5. + f5 + 6. + f6) / 4, ___________F___________, ___________F___________, (f5 + 5. + f6 + 6.) / 4},
                {(7. + f7 + 8. + 8.) / 4, (f8 + f8 + 8. + 8.) / 4, (f8 + f8 + 8. + 8.) / 4, (f7 + 7. + 8. + 8.) / 4}
        };

        double g1 = (ds[0][0] + ds[0][1] + ds[0][2] + ds[1][0] + ds[2][0]) / 5;
        double g2 = (ds[0][1] + ds[0][2] + ds[0][3] + ds[1][3] + ds[2][3]) / 5;
        double g3 = (ds[1][0] + ds[2][0] + ds[3][0] + ds[3][1] + ds[3][2]) / 5;
        double g4 = (ds[1][3] + ds[2][3] + ds[3][1] + ds[3][2] + ds[3][3]) / 5;


        double[] u = new double[]{
                1., 1., 1., 1., 1., 1., 1., 1.,
                2., f1, f2, f2, f2, f2, f1, 2.,
                3., f3, g1, g1, g2, g2, f3, 3.,
                4., f4, g1, g1, g2, g2, f4, 4.,
                5., f5, g3, g3, g4, g4, f5, 5.,
                6., f6, g3, g3, g4, g4, f6, 6.,
                7., f7, f8, f8, f8, f8, f7, 7.,
                8., 8., 8., 8., 8., 8., 8., 8.,
        };

        assertArrayEquals(new double[]{
                                  1., 1., 1., 1., 1., 1., 1., 1.,
                                  2., f1, f2, f2, f2, f2, f1, 2.,
                                  3., f3, g1, g1, g2, g2, f3, 3.,
                                  4., f4, g1, g1, g2, g2, f4, 4.,
                                  5., f5, g3, g3, g4, g4, f5, 5.,
                                  6., f6, g3, g3, g4, g4, f6, 6.,
                                  7., f7, f8, f8, f8, f8, f7, 7.,
                                  8., 8., 8., 8., 8., 8., 8., 8.,
                          },
                          GapFiller.fillGaps2(8, 8, new double[]{
                                  1, 1, 1, 1, 1, 1, 1, 1,
                                  2, F, F, F, F, F, F, 2,
                                  3, F, F, F, F, F, F, 3,
                                  4, F, F, F, F, F, F, 4,
                                  5, F, F, F, F, F, F, 5,
                                  6, F, F, F, F, F, F, 6,
                                  7, F, F, F, F, F, F, 7,
                                  8, 8, 8, 8, 8, 8, 8, 8,

                          }, 1),
                          1e-8);
    }
}
