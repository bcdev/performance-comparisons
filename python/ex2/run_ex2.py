import timeit

import numpy as np
import ex2.resizer_python_impl as pyimpl
import ex2.resizer_cython_impl as cyimpl

MAIN = 'from __main__ import np, pyimpl, cyimpl, a'
times = 100
N = 7

print('\nUpsampling:')
print('No\tSize\tPython\tCython\tGain')
s = 4
for i in range(N):
    a = np.arange(0, s * s, dtype=np.float64)
    a.shape = s, s
    t1 = timeit.timeit(setup=MAIN, number=times, stmt='pyimpl.resize(%d, %d, a, %d, %d)' % (s, s, int(s * 2.5), int(s * 2.1)))
    t2 = timeit.timeit(setup=MAIN, number=times, stmt='cyimpl.resize(%d, %d, a, %d, %d)' % (s, s, int(s * 2.5), int(s * 2.1)))
    print('%d\t%d\t%f\t%f\t%f' % (i + 1, s, t1, t2, t1 / t2))
    s *= 2

print('\nDownsampling:')
print('No\tSize\tPython\tCython\tGain')
s = 4
for i in range(N):
    a = np.arange(0, s * s, dtype=np.float64)
    a.shape = s, s
    t1 = timeit.timeit(setup=MAIN, number=times, stmt='pyimpl.resize(%d, %d, a, %d, %d)' % (s, s, int(s / 2.5), int(s / 2.1)))
    t2 = timeit.timeit(setup=MAIN, number=times, stmt='cyimpl.resize(%d, %d, a, %d, %d)' % (s, s, int(s / 2.5), int(s / 2.1)))
    print('%d\t%d\t%f\t%f\t%f' % (i + 1, s, t1, t2, t1 / t2))
    s *= 2
