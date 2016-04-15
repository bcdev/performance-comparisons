from math import sin, sqrt

import numpy as np
import numba as nb

@nb.jit(nopython=True)
def ex1(a, b):
    if a.shape[0] != b.shape[0]:
        raise ValueError("a and b must have same size")
    #if not (np.float64 == a.dtype or np.float64 == b.dtype):
    #    raise ValueError("a and b must have float64 type")
    size = a.shape[0]
    c = np.zeros(size, dtype=np.float64)
    for i in range(size):
        c[i] = sin(2.2 * a[i] - 3.3 * b[i]) / sqrt(4.4 * a[i] + 5.5 * b[i])
    return c
