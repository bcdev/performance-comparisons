import timeit

import numpy as np
import numexpr as ne
import ex1.python_impl as pyimpl
import ex1.cython_impl as cyimpl
from numpy import sin, sqrt


MAIN = 'from __main__ import np, ne, pyimpl, cyimpl, sin, sqrt, a, b'
ex1 = 'sin(2.2*a - 3.3*b) / sqrt(4.4*a + 5.5*b)'
times = 100

s = 2
print('No\tSize\tPython\tCython\tNumPy\tNumExpr\tGain1\tGain2\tGain3')
for i in range(20):
    a = np.arange(1, s, dtype=np.float64)
    b = np.arange(1, s, dtype=np.float64)
    t1 = timeit.timeit(setup=MAIN, number=times, stmt='pyimpl.ex1(a, b)')
    t2 = timeit.timeit(setup=MAIN, number=times, stmt='cyimpl.ex1(a, b)')
    t3 = timeit.timeit(setup=MAIN, number=times, stmt=ex1)
    t4 = timeit.timeit(setup=MAIN, number=times, stmt='ne.evaluate("%s")' % ex1)
    print('%d\t%d\t%f\t%f\t%f\t%f\t%f\t%f\t%f' % (i + 1, s, t1, t2, t3, t4, t1 / t2, t1 / t3, t1 / t4))
    s *= 2

