package com.bc.perfcomp.ex2;

/**
 * @author Norman Fomferra
 */
public class GapFiller {

    private final int validCountMin;

    public GapFiller(int validCountMin) {
        this.validCountMin = validCountMin;
    }

    public static double[] fillGaps1(int w, int h, double[] data, int validCountMin) {
        if (w == 1 && h == 1) {
            return data;
        }
        Raster raster = new Raster(w, h, data);
        if (raster.isFullOfGaps()) {
            return data;
        }

        GapFiller gapFiller = new GapFiller(validCountMin);
        while (!raster.isFreeOfGaps()) {
            raster = gapFiller.fillGapsBySurroundings(raster);
        }
        return raster.data;
    }

    public static double[] fillGaps2(int w, int h, double[] data, int validCountMin) {
        if (w == 1 && h == 1) {
            return data;
        }
        GapFiller gapFiller = new GapFiller(validCountMin);
        Raster raster = gapFiller.fillGaps2(new Raster(w, h, data));
        return raster.data;
    }


    private Raster fillGaps2(Raster raster) {
        if (raster.isSingular() || raster.isFullOfGaps() || raster.isFreeOfGaps()) {
            return raster;
        }

        // Note, new raster is a copy
        raster = fillGapsBySurroundings(raster);
        if (raster.isFreeOfGaps()) {
            return raster;
        }

        Raster downsampled = Resizer.downsample(raster, (raster.w + 1) / 2, (raster.h + 1) / 2);
        downsampled = fillGaps2(downsampled);
        raster = fillGapsFromDownsampled(raster, downsampled);

        return raster;
    }

    /**
     * Fills gap pixels by taking over values from a downsampled version of the raster.
     *
     * @param raster            The raster to gap-fill
     * @param downsampledRaster the downsampled version.
     * @return a new raster using  the old raster data array instance, but gap-filled
     */
    private Raster fillGapsFromDownsampled(Raster raster, Raster downsampledRaster) {
        int dstW = raster.w;
        int dstH = raster.h;
        double[] dstData = raster.data;
        int srcW = downsampledRaster.w;
        int srcH = downsampledRaster.h;
        double[] srcData = downsampledRaster.data;
        int filledCount = 0;
        for (int dstY = 0; dstY < dstH; dstY++) {
            for (int dstX = 0; dstX < dstW; dstX++) {
                if (Raster.isGap(dstData[dstY * dstW + dstX])) {
                    int srcX = dstX / 2;
                    int srcY = dstY / 2;
                    double v = srcData[srcY * srcW + srcX];
                    if (!Raster.isGap(v)) {
                        dstData[dstY * dstW + dstX] = v;
                        filledCount++;
                    }
                }
            }
        }
        return new Raster(dstW, dstH, dstData.clone(), raster.gapCount - filledCount);
    }

    /**
     * Fills gap pixels by averaging the surrounding pixels, if any.
     *
     * @param raster source raster
     * @return gap-filled raster, always uses a new array instance
     */
    private Raster fillGapsBySurroundings(Raster raster) {
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
