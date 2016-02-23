package com.bc.perfcomp.ex2;

/**
 * Performs raster resizing which may imply interpolation while upsampling or aggregation while downsampling.
 *
 * @author Norman Fomferra
 */
public class Resizer {

    public static final double EPS = 1e-10;

    public static double[] resize(int w, int h, double[] data, int wNew, int hNew) {
        if (wNew < w && hNew < h) {
            Raster downsampled = downsample(new Raster(w, h, data), wNew, hNew);
            return downsampled.data;
        } else if (wNew < w) {
            Raster downsampled = downsample(new Raster(w, h, data), wNew, h);
            if (hNew > h) {
                Raster upsampled = upsample(downsampled, wNew, hNew);
                return upsampled.data;
            } else {
                return downsampled.data;
            }
        } else if (hNew < h) {
            Raster downsampled = downsample(new Raster(w, h, data), w, hNew);
            if (wNew > w) {
                Raster upsampled = upsample(downsampled, wNew, hNew);
                return upsampled.data;
            } else {
                return downsampled.data;
            }
        } else if (wNew > w || hNew > h) {
            Raster upsampled = upsample(new Raster(w, h, data), wNew, hNew);
            return upsampled.data;
        }
        return data.clone();
    }

    /**
     * Performs a linear interpolation.
     *
     * @param raster Source raster
     * @param dstW   Target raster width
     * @param dstH   Target raster height
     * @return Upsampled (interpolated) target raster.
     */
    static Raster upsample(Raster raster, int dstW, int dstH) {
        int srcW = raster.w;
        int srcH = raster.h;
        if (srcW == dstW && srcH == dstH) {
            return raster;
        }
        if (dstW < srcW || dstH < srcH) {
            throw new IllegalArgumentException("Invalid target size");
        }
        double sx = (srcW - 1.0) / (dstW > 1 ? (dstW - 1.0) : 1.0);
        double sy = (srcH - 1.0) / (dstH > 1 ? (dstH - 1.0) : 1.0);
        double[] interpolated = new double[dstW * dstH];
        double[] data = raster.data;
        int gapCount = 0;
        for (int dstY = 0; dstY < dstH; dstY++) {
            double srcYF = sy * dstY;
            int srcY = (int) srcYF;
            double wy = srcYF - srcY;
            boolean withinSrcH = srcY + 1 < srcH;
            for (int dstX = 0; dstX < dstW; dstX++) {
                double srcXF = sx * dstX;
                int srcX = (int) srcXF;
                double wx = srcXF - srcX;
                boolean withinSrcW = srcX + 1 < srcW;
                double v00 = data[srcY * srcW + srcX];
                double v01 = withinSrcW ? data[srcY * srcW + srcX + 1] : v00;
                double v10 = withinSrcH ? data[(srcY + 1) * srcW + srcX] : v00;
                double v11 = withinSrcW && withinSrcH ? data[(srcY + 1) * srcW + srcX + 1] : v00;
                double v0 = v00 + wx * (v01 - v00);
                double v1 = v10 + wx * (v11 - v10);
                double v = v0 + wy * (v1 - v0);
                if (Raster.isGap(v)) {
                    interpolated[dstY * dstW + dstX] = Double.NaN;
                    gapCount++;
                } else {
                    interpolated[dstY * dstW + dstX] = v;
                }
            }
        }
        return new Raster(dstW, dstH, interpolated, gapCount);
    }

    /**
     * Performs an area-weighed average aggregation.
     *
     * @param raster Source raster
     * @param dstW   Target raster width
     * @param dstH   Target raster height
     * @return Downsampled (aggregated) target raster.
     */
    static Raster downsample(Raster raster, int dstW, int dstH) {
        int srcW = raster.w;
        int srcH = raster.h;
        if (srcW == dstW && srcH == dstH) {
            return raster;
        }
        if (dstW > srcW || dstH > srcH) {
            throw new IllegalArgumentException("Invalid target size");
        }
        double sx = srcW / (double) dstW;
        double sy = srcH / (double) dstH;
        double[] aggregated = new double[dstW * dstH];
        double[] data = raster.data;
        int gapCount = 0;
        for (int dstY = 0; dstY < dstH; dstY++) {
            double srcYF0 = sy * dstY;
            double srcYF1 = srcYF0 + sy;
            int srcY0 = (int) srcYF0;
            int srcY1 = (int) srcYF1;
            double wy0 = 1.0 - (srcYF0 - srcY0);
            double wy1 = srcYF1 - srcY1;
            if (wy1 < EPS) {
                wy1 = 1.0;
                if (srcY1 > srcY0) {
                    srcY1--;
                }
            }
            for (int dstX = 0; dstX < dstW; dstX++) {
                double srcXF0 = sx * dstX;
                double srcXF1 = srcXF0 + sx;
                int srcX0 = (int) srcXF0;
                int srcX1 = (int) srcXF1;
                double wx0 = 1.0 - (srcXF0 - srcX0);
                double wx1 = srcXF1 - srcX1;
                if (wx1 < EPS) {
                    wx1 = 1.0;
                    if (srcX1 > srcX0) {
                        srcX1--;
                    }
                }
                double vSum = 0.0;
                double wSum = 0.0;
                for (int srcY = srcY0; srcY <= srcY1; srcY++) {
                    double wy = srcY == srcY0 ? wy0 : srcY == srcY1 ? wy1 : 1;
                    for (int srcX = srcX0; srcX <= srcX1; srcX++) {
                        double wx = srcX == srcX0 ? wx0 : srcX == srcX1 ? wx1 : 1;
                        double v = data[srcY * srcW + srcX];
                        if (!Raster.isGap(v)) {
                            double w = wx * wy;
                            vSum += w * v;
                            wSum += w;
                        }
                    }
                }
                if (Raster.isGap(vSum) || wSum == 0.0) {
                    aggregated[dstY * dstW + dstX] = Double.NaN;
                    gapCount++;
                } else {
                    aggregated[dstY * dstW + dstX] = vSum / wSum;
                }
            }
        }
        return new Raster(dstW, dstH, aggregated, gapCount);
    }
}
