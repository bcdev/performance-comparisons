# http://stackoverflow.com/questions/7075082/what-is-future-in-python-used-for-and-how-when-to-use-it-and-how-it-works
from __future__ import division
from ex2.raster import Raster
import numpy as np
cimport numpy as np
from libc.math cimport sin, sqrt
from libc.math cimport isnan, NAN

DTYPE_INT = np.int64
DTYPE_DBL = np.float64
ctypedef np.int64_t DTYPE_INT_t
ctypedef np.float64_t DTYPE_DBL_t
ctypedef Py_ssize_t SIZE_t

cdef DTYPE_DBL_t EPS = 1e-10


def upsample(raster, SIZE_t dstW, SIZE_t dstH):
    """
     Performs a linear interpolation.

     @param raster Source raster
     @param dstW   Target raster width
     @param dstH   Target raster height
     @return Upsampled (interpolated) target raster.
    """
    cdef SIZE_t srcW = raster.w
    cdef SIZE_t srcH = raster.h
    if srcW == dstW and srcH == dstH:
        return raster

    if dstW < srcW or dstH < srcH:
        raise ValueError("Invalid target size")

    cdef DTYPE_DBL_t sx = (srcW - 1.0) / ((dstW - 1.0) if dstW > 1 else 1.0)
    cdef DTYPE_DBL_t sy = (srcH - 1.0) / ((dstH - 1.0) if dstH > 1 else 1.0)
    cdef np.ndarray[DTYPE_DBL_t, ndim=1] data = raster.data;
    cdef np.ndarray[DTYPE_DBL_t, ndim=1] interpolated = np.zeros(dstW * dstH, dtype=DTYPE_DBL)
    cdef SIZE_t gapCount = 0
    cdef SIZE_t dstX, dstY, srcX, srcY, withinSrcW, withinSrcH
    cdef DTYPE_DBL_t srcXF, srcYF, wx, wy
    cdef DTYPE_DBL_t v00, v01, v10, v11, v0, v1, v
    for dstY in range(dstH):
        srcYF = sy * dstY
        srcY = <SIZE_t>srcYF
        wy = srcYF - srcY
        withinSrcH = srcY + 1 < srcH
        for dstX in range(dstW):
            srcXF = sx * dstX
            srcX = <SIZE_t>srcXF
            wx = srcXF - srcX
            withinSrcW = srcX + 1 < srcW
            v00 = data[srcY * srcW + srcX]
            v01 = data[srcY * srcW + srcX + 1] if withinSrcW else v00
            v10 = data[(srcY + 1) * srcW + srcX] if withinSrcH  else v00
            v11 = data[(srcY + 1) * srcW + srcX + 1] if withinSrcW and withinSrcH  else v00
            v0 = v00 + wx * (v01 - v00)
            v1 = v10 + wx * (v11 - v10)
            v = v0 + wy * (v1 - v0)
            if isnan(v):
                gapCount += 1
            interpolated[dstY * dstW + dstX] = v
    return Raster(dstW, dstH, interpolated, gapCount)


def downsample(raster, SIZE_t dstW, SIZE_t dstH):
    """
     * Performs an area-weighed average aggregation.
     *
     * @param raster Source raster
     * @param dstW   Target raster width
     * @param dstH   Target raster height
     * @return Downsampled (aggregated) target raster.
    """
    cdef SIZE_t srcW = raster.w
    cdef SIZE_t srcH = raster.h
    if srcW == dstW and srcH == dstH:
        return raster

    if dstW > srcW or dstH > srcH:
        raise ValueError("Invalid target size")

    cdef DTYPE_DBL_t sx = srcW / dstW
    cdef DTYPE_DBL_t sy = srcH / dstH
    cdef np.ndarray[DTYPE_DBL_t, ndim=1] aggregated = np.zeros(dstW * dstH, dtype=DTYPE_DBL)
    cdef SIZE_t gapCount = 0
    cdef SIZE_t dstX, dstY, srcX0, srcX1, srcY0, srcY1
    cdef DTYPE_DBL_t srcXF0, srcXF1,srcYF0,srcYF1, wx, wy
    cdef DTYPE_DBL_t vSum, wSum, v, w
    for dstY in range(dstH):
        srcYF0 = sy * dstY
        srcYF1 = srcYF0 + sy
        srcY0 = <SIZE_t>srcYF0
        srcY1 = <SIZE_t>srcYF1
        wy0 = 1.0 - (srcYF0 - srcY0)
        wy1 = srcYF1 - srcY1
        if wy1 < EPS:
            wy1 = 1.0
            if srcY1 > srcY0:
                srcY1 -= 1
        for dstX in range(dstW):
            srcXF0 = sx * dstX
            srcXF1 = srcXF0 + sx
            srcX0 = <SIZE_t>srcXF0
            srcX1 = <SIZE_t>srcXF1
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
                    v = raster.data[srcY * srcW + srcX]
                    if not isnan(v):
                        w = wx * wy
                        vSum += w * v
                        wSum += w
            if isnan(vSum) or wSum < EPS:
                aggregated[dstY * dstW + dstX] = NAN
                gapCount += 1
            else:
                aggregated[dstY * dstW + dstX] = vSum / wSum
    return Raster(dstW, dstH, aggregated, gapCount)


def resize(SIZE_t w, SIZE_t h, np.ndarray[DTYPE_DBL_t, ndim=1] data, SIZE_t wNew, SIZE_t hNew):
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



