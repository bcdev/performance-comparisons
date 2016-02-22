package com.bc.perfcomp.ex2;

/**
 * @author Norman Fomferra
 */
final class Raster implements Cloneable {
    final int w, h;
    final double[] data;
    final int gapCount;

    public Raster(int w, int h, double[] data) {
        this(w, h, data, countGaps(data));
    }

    public Raster(int w, int h, double[] data, int gapCount) {
        this.w = w;
        this.h = h;
        this.data = data;
        this.gapCount = gapCount;
    }

    public boolean isFreeOfGaps() {
        return gapCount == 0;
    }

    public boolean isFullOfGaps() {
        return gapCount == w * h;
    }

    public boolean isSingular() {
        return w == 1 && h == 1;
    }

    public static boolean isGap(double v) {
        return Double.isNaN(v);
    }

    public static int countGaps(double[] data) {
        int gapCount = 0;
        for (double v : data) {
            if (isGap(v)) {
                gapCount++;
            }
        }
        return gapCount;
    }

    @Override
    public Raster clone() {
        return new Raster(w, h, data.clone(), gapCount);
    }

}
