from __future__ import division
import numpy as np
cimport numpy as np
from libc.math cimport sin, sqrt
#from math import sin, sqrt

#DTYPE_INT = np.int
DTYPE_DBL = np.float64

#ctypedef np.int_t DTYPE_INT_t
ctypedef np.float64_t DTYPE_DBL_t

def ex1(np.ndarray[DTYPE_DBL_t, ndim=1] a, np.ndarray[DTYPE_DBL_t, ndim=1] b):
    if a.shape[0] != b.shape[0]:
        raise ValueError("a and b must have same size")
    assert a.dtype == DTYPE_DBL and b.dtype == DTYPE_DBL
    cdef Py_ssize_t size = a.shape[0]
    cdef np.ndarray[DTYPE_DBL_t, ndim=1] c = np.zeros(size, dtype=DTYPE_DBL)
    cdef Py_ssize_t i
    for i in range(size):
        c[i] = sin(2.2*a[i] - 3.3*b[i]) / sqrt(4.4*a[i] + 5.5*b[i])
    return c
