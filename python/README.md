# Python Performance Comparisons

To build the Cython code on Windows 10 first install Visual Studio Community from

* https://www.visualstudio.com/products/free-developer-offers-vs.aspx

Then open a console command window and type

    SET MSVS14=C:\Program Files (x86)\Microsoft Visual Studio 14.0
    SET VS140COMNTOOLS=%MSVS14%\Common7\Tools\
    SET VS100COMNTOOLS=%VS140COMNTOOLS%

    call "%MSVS14%\VC\vcvarsall.bat" amd64

    python setup.py build_ext --inplace

