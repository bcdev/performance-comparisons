from distutils.core import setup
from Cython.Build import cythonize

import numpy

# See:
#   http://docs.cython.org/src/tutorial/cython_tutorial.html
#   http://docs.cython.org/src/userguide/numpy_tutorial.html
#   http://docs.cython.org/src/reference/compilation.html

setup(ext_modules = cythonize(["ex1/*.pyx", "ex2/*.pyx"]), include_dirs=[numpy.get_include()])

'''
>>> import numpy as np
>>> import convolve
>>> a = np.arange(0,100)
>>> a.shape = 10,10
>>> k = np.arange(0,9)
>>> k.shape = 3,3
>>> convolve.naive_convolve(a, k)
'''