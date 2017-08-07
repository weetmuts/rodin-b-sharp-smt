Building CVC4 on MacOs for the SMT plug-in
------------------------------------------

Install homebrew (https://brew.sh/).

Clone the Git repository containing the formula
(https://github.com/CVC4/homebrew-cvc4):

  brew tap CVC4/cvc4

Then compile and install the binary:

  brew install cvc4

Finally, copy the binary and its dependencies in the plug-in:

  cd /path/to/SMT.git/org.eventb.smt.cvc4.macosx/os/macosx/x86_64
  cp /usr/local/Cellar/cvc4/1.5/bin/cvc4 .
  cp /usr/local/Cellar/cvc4/1.5/lib/libcvc4parser.4.dylib .
  cp /usr/local/opt/libantlr3c/lib/libantlr3c.dylib .
  cp /usr/local/Cellar/cvc4/1.5/lib/libcvc4.4.dylib .
  cp /usr/local/opt/gmp/lib/libgmp.10.dylib .

Fix the binary and the libraries to look for local library files:

  for f in cvc4 \
           libcvc4parser.4.dylib \
           libantlr3c.dylib \
           libcvc4.4.dylib \
           libgmp.10.dylib
  do
    install_name_tool \
      -change \
        /usr/local/Cellar/cvc4/1.5/lib/libcvc4parser.4.dylib \
        @executable_path/libcvc4parser.4.dylib \
      -change \
        /usr/local/opt/libantlr3c/lib/libantlr3c.dylib \
        @executable_path/libantlr3c.dylib \
      -change \
        /usr/local/Cellar/cvc4/1.5/lib/libcvc4.4.dylib \
        @executable_path/libcvc4.4.dylib \
      -change \
        /usr/local/opt/gmp/lib/libgmp.10.dylib \
        @executable_path/libgmp.10.dylib \
      $f
  done
  install_name_tool \
    -id @executable_path/libcvc4parser.4.dylib libcvc4parser.4.dylib
  install_name_tool \
    -id @executable_path/libantlr3c.dylib libantlr3c.dylib
  install_name_tool \
    -id @executable_path/libcvc4.4.dylib libcvc4.4.dylib
  install_name_tool \
    -id @executable_path/libgmp.10.dylib libgmp.10.dylib


This has been performed on OS X El Capitan (10.11.6) with the dependencies:

  % brew deps --include-build --include-requirements --full-name --annotate cvc4

    :arch
    :java
    antlr@3  [build]
    boost  [build]
    gmp
    libantlr3c

with the versions:

  % brew list --versions cvc4 $(brew deps --include-build --full-name cvc4) 

    cvc4 1.5
    antlr@3 3.5.2
    boost 1.65.1
    gmp 6.1.2
    libantlr3c 3.4_1
