#!python
#cython: language_level=3, boundscheck=False, cdivision=True

# http://stackoverflow.com/questions/7075082/what-is-future-in-python-used-for-and-how-when-to-use-it-and-how-it-works
from __future__ import division
import numpy as np
cimport numpy as np
from libc.math cimport isnan, NAN

DTYPE_INT = np.int64
DTYPE_DBL = np.float64
ctypedef np.int64_t DTYPE_INT_t
ctypedef np.float64_t DTYPE_DBL_t
ctypedef Py_ssize_t SIZE_t

cdef DTYPE_DBL_t EPS = 1e-10


def upsample(np.ndarray[DTYPE_DBL_t, ndim=2] data, SIZE_t dstW, SIZE_t dstH):
    """
     Performs a linear interpolation.

     @param raster Source raster
     @param dstW   Target raster width
     @param dstH   Target raster height
     @return Upsampled (interpolated) target raster.
    """
    cdef SIZE_t srcW = data.shape[1]
    cdef SIZE_t srcH = data.shape[0]
    if srcW == dstW and srcH == dstH:
        return data

    if dstW < srcW or dstH < srcH:
        raise ValueError("Invalid target size")

    cdef DTYPE_DBL_t sx = (srcW - 1.0) / ((dstW - 1.0) if dstW > 1 else 1.0)
    cdef DTYPE_DBL_t sy = (srcH - 1.0) / ((dstH - 1.0) if dstH > 1 else 1.0)
    cdef np.ndarray[DTYPE_DBL_t, ndim=2] interpolated = np.zeros((dstH, dstW), dtype=DTYPE_DBL)
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
            v00 = data[srcY, srcX]
            v01 = data[srcY, srcX + 1] if withinSrcW else v00
            v10 = data[srcY + 1, srcX] if withinSrcH  else v00
            v11 = data[srcY + 1, srcX + 1] if withinSrcW and withinSrcH  else v00
            v0 = v00 + wx * (v01 - v00)
            v1 = v10 + wx * (v11 - v10)
            v = v0 + wy * (v1 - v0)
            interpolated[dstY, dstX] = v
    return interpolated


def downsample(np.ndarray[DTYPE_DBL_t, ndim=2] data, SIZE_t dstW, SIZE_t dstH):
    """
     * Performs an area-weighed average aggregation.
     *
     * @param raster Source raster
     * @param dstW   Target raster width
     * @param dstH   Target raster height
     * @return Downsampled (aggregated) target raster.
    """
    cdef SIZE_t srcW = data.shape[1]
    cdef SIZE_t srcH = data.shape[0]
    if srcW == dstW and srcH == dstH:
        return data

    if dstW > srcW or dstH > srcH:
        raise ValueError("Invalid target size")

    cdef DTYPE_DBL_t sx = <DTYPE_DBL_t>srcW / <DTYPE_DBL_t>dstW
    cdef DTYPE_DBL_t sy = <DTYPE_DBL_t>srcH / <DTYPE_DBL_t>dstH
    cdef np.ndarray[DTYPE_DBL_t, ndim=2] aggregated = np.zeros((dstH, dstW), dtype=DTYPE_DBL)
    cdef SIZE_t gapCount = 0
    cdef SIZE_t dstX, dstY, srcX, srcY, srcX0, srcX1, srcY0, srcY1
    cdef DTYPE_DBL_t srcXF0, srcXF1, srcYF0, srcYF1
    cdef DTYPE_DBL_t wx0, wx1, wy0, wy1, wx, wy, vSum, wSum, v, w
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
            for srcY in range(srcY0, srcY1 + 1):
                wy = wy0 if (srcY == srcY0) else wy1 if (srcY == srcY1) else 1.0
                for srcX in range(srcX0, srcX1 + 1):
                    wx = wx0 if (srcX == srcX0) else wx1 if (srcX == srcX1) else 1.0
                    v = data[srcY, srcX]
                    if not isnan(v):
                        w = wx * wy
                        vSum += w * v
                        wSum += w
            if isnan(vSum) or wSum < EPS:
                aggregated[dstY, dstX] = NAN
            else:
                aggregated[dstY, dstX] = vSum / wSum
    return aggregated


def resize(np.ndarray[DTYPE_DBL_t, ndim=2] data, SIZE_t wNew, SIZE_t hNew):
    """
     Performs raster resizing which may imply interpolation while upsampling or aggregation while downsampling.
     @author Norman Fomferra
    """
    cdef SIZE_t w = data.shape[1]
    cdef SIZE_t h = data.shape[0]

    if wNew < w and hNew < h:
        return downsample(data, wNew, hNew)
    elif wNew < w:
        downsampled = downsample(data, wNew, h)
        if hNew > h:
            return upsample(downsampled, wNew, hNew)
        else:
            return downsampled
    elif hNew < h:
        downsampled = downsample(data, w, hNew)
        if wNew > w:
            return upsample(downsampled, wNew, hNew)
        else:
            return downsampled
    elif wNew > w or hNew > h:
        return upsample(data, wNew, hNew)
    return data



