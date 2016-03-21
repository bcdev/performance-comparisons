# Python Performance Comparisons

To build the Cython code on Windows 10 first install Visual Studio Community from

* https://www.visualstudio.com/products/free-developer-offers-vs.aspx

Make sure to select **Programming languages / Visual C++**
and **Windows and web development / Developer tools for universal Windows-Apps**
(Windows SDK).


Then open a console command window and type

    SET MSVS14=C:\Program Files (x86)\Microsoft Visual Studio 14.0
    SET VS140COMNTOOLS=%MSVS14%\Common7\Tools\
    SET VS100COMNTOOLS=%VS140COMNTOOLS%

    call "%MSVS14%\VC\vcvarsall.bat" amd64

    python setup.py build_ext --inplace

For more information see section "Compiling with distutils" from the Cython documentation at

* http://docs.cython.org/src/reference/compilation.html#compilation-reference