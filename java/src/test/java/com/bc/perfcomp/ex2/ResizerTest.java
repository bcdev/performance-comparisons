package com.bc.perfcomp.ex2;

import org.junit.Test;

import static java.lang.Double.NaN;
import static org.junit.Assert.assertArrayEquals;

/**
 * @author Norman Fomferra
 */
public class ResizerTest {

    public static final double F = NaN;

    @Test
    public void testNoOp() throws Exception {
        assertArrayEquals(new double[]{1, 2, 3, 4},
                Resizer.resize(2, 2, new double[]{1, 2, 3, 4}, 2, 2),
                1e-8);
    }

    @Test
    public void testInterpolation() throws Exception {
        assertArrayEquals(new double[]{
                        3. / 3, 4. / 3, 5. / 3, 6. / 3,
                        5. / 3, 6. / 3, 7. / 3, 8. / 3,
                        7. / 3, 8. / 3, 9. / 3, 10. / 3,
                        9. / 3, 10. / 3, 11. / 3, 12. / 3,
                },
                Resizer.resize(2, 2, new double[]{
                        1, 2,
                        3, 4
                }, 4, 4),
                1e-8);
    }

    @Test
    public void testAggregation() throws Exception {

        assertArrayEquals(new double[]{
                        (0.6 + 0.5 * 0.2 + 0.5 * 1.4 + 0.25 * 1.6) / (1.0 + 0.5 + 0.5 + 0.25),
                        (3.4 + 0.5 * 0.2 + 0.5 * 1.0 + 0.25 * 1.6) / (1.0 + 0.5 + 0.5 + 0.25),
                        (4.0 + 0.5 * 1.4 + 0.5 * 2.8 + 0.25 * 1.6) / (1.0 + 0.5 + 0.5 + 0.25),
                        (3.0 + 0.5 * 1.0 + 0.5 * 2.8 + 0.25 * 1.6) / (1.0 + 0.5 + 0.5 + 0.25),
                },
                Resizer.resize(3, 3, new double[]{
                        0.6, 0.2, 3.4,
                        1.4, 1.6, 1.0,
                        4.0, 2.8, 3.0,
                }, 2, 2),
                1e-8);

        assertArrayEquals(new double[]{
                        1.0, 2.5,
                        3.5, 3.0
                },
                Resizer.resize(4, 4, new double[]{
                        0.9, 0.5, 3.0, 4.0,
                        1.1, 1.5, 1.0, 2.0,
                        4.0, 2.1, 3.0, 5.0,
                        3.0, 4.9, 3.0, 1.0,
                }, 2, 2),
                1e-8);
    }

}
