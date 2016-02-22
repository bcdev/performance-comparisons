package com.bc.perfcomp.ex2;

/**
 * @author Norman Fomferra
 */
public class GapFiller {

    public static double[] fillGaps(int w, int h, double[] data) {
        if (w == 1 && h == 1) {
            return data;
        }
        Raster raster = new Raster(w, h, data);
        if (raster.isFullOfGaps()) {
            return data;
        }
        while (!raster.isFreeOfGaps()) {
            raster = fillGaps(raster);
        }
        return raster.data;
    }

    public static double[] fillGaps2(int w, int h, double[] data) {
        if (w == 1 && h == 1) {
            return data;
        }
        Raster raster = new Raster(w, h, data);
        if (raster.isFullOfGaps()) {
            return data;
        }
        while (!raster.isFreeOfGaps()) {
            raster = fillGaps(raster);
        }
        return raster.data;
    }

    public static Raster fillGaps2(Raster raster) {
        if (raster.isSingular()) {
            return raster.clone();
        }

        while (!raster.isFullOfGaps() && !raster.isFreeOfGaps()) {
            raster = fillGaps(raster);
            if (raster.isFreeOfGaps() || raster.isSingular()) {
                return raster;
            }
            Raster raster2 = Resizer.aggregate(raster, (raster.w + 1) / 2, (raster.h + 1) / 2);

            for (int dstY = 0; dstY < raster.h; dstY++) {
                for (int dstX = 0; dstX < raster.w; dstX++) {
                    int srcX = dstX / 2;
                    int srcY = dstY / 2;

                }
            }

        }
        return raster;
    }

    private static Raster fillGaps(Raster raster) {
        //final int validCountMin = 1;
        //final int validCountMin = 2;
        final int validCountMin = 3;
        int w = raster.w;
        int h = raster.h;
        double[] data = raster.data;
        double[] gapFilledData = data.clone();
        int filledCount = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (Raster.isGap(data[y * w + x])) {
                    int x1 = x > 0 ? x - 1 : x;
                    int y1 = y > 0 ? y - 1 : y;
                    int x2 = x < w - 1 ? x + 1 : x;
                    int y2 = y < h - 1 ? y + 1 : y;
                    double vSum = 0;
                    int validCount = 0;
                    for (int yy = y1; yy <= y2; yy++) {
                        for (int xx = x1; xx <= x2; xx++) {
                            double v = data[yy * w + xx];
                            if (!Raster.isGap(v)) {
                                vSum += v;
                                validCount++;
                            }
                        }
                    }
                    if (validCount >= validCountMin) {
                        gapFilledData[y * w + x] = vSum / validCount;
                        filledCount++;
                    }
                }
            }
        }
        return new Raster(w, h, gapFilledData, raster.gapCount - filledCount);
    }
}
