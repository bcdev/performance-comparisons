SET MSVS14=C:\Program Files (x86)\Microsoft Visual Studio 14.0
SET VS140COMNTOOLS=%MSVS14%\Common7\Tools\
SET VS100COMNTOOLS=%VS140COMNTOOLS%

%MSVS14%\vcvarsall.bat amd64

python setup.py build_ext --inplace
