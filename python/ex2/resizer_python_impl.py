# http://stackoverflow.com/questions/7075082/what-is-future-in-python-used-for-and-how-when-to-use-it-and-how-it-works
from __future__ import division
from ex2.raster import Raster
import numpy as np
from math import sin, sqrt


EPS = 1e-10
DTYPE_DBL  = np.float64

def upsample(raster, dstW, dstH):
    """
     Performs a linear interpolation.

     @param raster Source raster
     @param dstW   Target raster width
     @param dstH   Target raster height
     @return Upsampled (interpolated) target raster.
    """
    srcW = raster.w
    srcH = raster.h
    if srcW == dstW and srcH == dstH:
        return raster

    if dstW < srcW or dstH < srcH:
        raise ValueError("Invalid target size")

    sx = (srcW - 1.0) / ((dstW - 1.0) if dstW > 1 else 1.0)
    sy = (srcH - 1.0) / ((dstH - 1.0) if dstH > 1 else 1.0)
    data = raster.data;
    interpolated = np.zeros(dstW * dstH, dtype=DTYPE_DBL)
    gapCount = 0
    for dstY in range(dstH):
        srcYF = sy * dstY
        srcY = int(srcYF)
        wy = srcYF - srcY
        withinSrcH = srcY + 1 < srcH
        for dstX in range(dstW):
            srcXF = sx * dstX
            srcX = int(srcXF)
            wx = srcXF - srcX
            withinSrcW = srcX + 1 < srcW
            v00 = data[srcY * srcW + srcX]
            v01 = data[srcY * srcW + srcX + 1] if withinSrcW else v00
            v10 = data[(srcY + 1) * srcW + srcX] if withinSrcH  else v00
            v11 = data[(srcY + 1) * srcW + srcX + 1] if withinSrcW and withinSrcH  else v00
            v0 = v00 + wx * (v01 - v00)
            v1 = v10 + wx * (v11 - v10)
            v = v0 + wy * (v1 - v0)
            if np.isnan(v):
                gapCount += 1
            interpolated[dstY * dstW + dstX] = v
    return Raster(dstW, dstH, interpolated, gapCount)


def downsample(raster, dstW, dstH):
    """
     * Performs an area-weighed average aggregation.
     *
     * @param raster Source raster
     * @param dstW   Target raster width
     * @param dstH   Target raster height
     * @return Downsampled (aggregated) target raster.
    """
    srcW = raster.w
    srcH = raster.h
    if srcW == dstW and srcH == dstH:
        return raster

    if dstW > srcW or dstH > srcH:
        raise ValueError("Invalid target size")

    sx = srcW / dstW
    sy = srcH / dstH
    aggregated = np.zeros(dstW * dstH, dtype=DTYPE_DBL)
    data = raster.data
    gapCount = 0
    for dstY in range(dstH):
        srcYF0 = sy * dstY
        srcYF1 = srcYF0 + sy
        srcY0 = int(srcYF0)
        srcY1 = int(srcYF1)
        wy0 = 1.0 - (srcYF0 - srcY0)
        wy1 = srcYF1 - srcY1
        if wy1 < EPS:
            wy1 = 1.0
            if srcY1 > srcY0:
                srcY1 -= 1
        for dstX in range(dstW):
            srcXF0 = sx * dstX
            srcXF1 = srcXF0 + sx
            srcX0 = int(srcXF0)
            srcX1 = int(srcXF1)
            wx0 = 1.0 - (srcXF0 - srcX0)
            wx1 = srcXF1 - srcX1
            if wx1 < EPS:
                wx1 = 1.0
                if srcX1 > srcX0:
                    srcX1 -= 1
            vSum = 0.0
            wSum = 0.0
            for srcY in range(srcY0, srcY1+1):
                wy = wy0 if (srcY == srcY0) else wy1 if (srcY == srcY1) else 1.0
                for srcX in range(srcX0, srcX1+1):
                    wx = wx0 if (srcX == srcX0) else wx1 if (srcX == srcX1) else 1.0
                    v = data[srcY * srcW + srcX]
                    if not np.isnan(v):
                        w = wx * wy
                        vSum += w * v
                        wSum += w
            if np.isnan(vSum) or wSum < EPS:
                aggregated[dstY * dstW + dstX] = np.nan
                gapCount += 1
            else:
                aggregated[dstY * dstW + dstX] = vSum / wSum
    return Raster(dstW, dstH, aggregated, gapCount)


def resize(w, h, data, wNew, hNew):
    """
     Performs raster resizing which may imply interpolation while upsampling or aggregation while downsampling.
     @author Norman Fomferra
    """

    if wNew < w and hNew < h:
        downsampled = downsample(Raster(w, h, data), wNew, hNew)
        return downsampled.data
    elif wNew < w:
        downsampled = downsample(Raster(w, h, data), wNew, h)
        if hNew > h:
            upsampled = upsample(downsampled, wNew, hNew)
            return upsampled.data
        else:
            return downsampled.data
    elif hNew < h:
        downsampled = downsample(Raster(w, h, data), w, hNew)
        if wNew > w:
            upsampled = upsample(downsampled, wNew, hNew)
            return upsampled.data
        else:
            return downsampled.data
    elif wNew > w or hNew > h:
        upsampled = upsample(Raster(w, h, data), wNew, hNew)
        return upsampled.data
    return data



