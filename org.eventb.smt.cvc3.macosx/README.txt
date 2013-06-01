The cvc3 binary has been compiled with XCode 4.6.2 under Mac OS X Lion
(10.7.3).  In addition, the GMP librery 5.1.1 was installed with homebrew.

To make sure that the GMP dynamic library will not be used, disable it :

	cd /usr/local/lib
	mv libgmp.dylib libgmp.dylib.disabled

The following commands have been used:

	curl -O http://www.cs.nyu.edu/acsys/cvc3/releases/2.4.1/cvc3-2.4.1.tar.gz
	tar zxf cvc3-2.4.1.tar.gz
	cd cvc3-2.4.1
	rm src/sat/xchaff*
	./configure --disable-zchaff --build=x86_64-apple-darwin
	make

Nota: The build option passed to configure has been copied from the build
option devised by homebrew when configuring the GMP library.

The binary has been tested by issuing the following additional commands:

	cd test
	make
	bin/test
