The cvc4 binary has been compiled with XCode 5.1.1 under Mac OS X Mavericks
(10.9.4).  In addition, the GMP library 6.0.0 was installed with homebrew.

To make sure that the GMP dynamic library will not be used, disable it:

	cd /usr/local/lib
	mv libgmp.dylib libgmp.dylib.disabled
	mv libgmp.10.dylib libgmp.10.dylib.disabled

The following commands have been used:

	curl -O http://cvc4.cs.nyu.edu/builds/src/cvc4-1.4.tar.gz
	tar zxf cvc4-1.4.tar.gz
	cd cvc4-1.4
	./contrib/get-antlr-3.4
	./configure with the options printed by get-antlr-3.4
	make

The binary has been tested by issuing the following additional commands:

	make check
