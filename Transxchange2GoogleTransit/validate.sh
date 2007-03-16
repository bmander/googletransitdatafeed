java Transxchange2GoogleTransit ValidatorFiles/linear.xml www.aagency.org Europe/London 3 ValidatorFiles/val1
diff --strip-trailing-cr -s -q ValidatorFiles/val1 ValidatorFiles/ref1
java Transxchange2GoogleTransit ValidatorFiles/express.xml www.aagency.org Europe/London 3 ValidatorFiles/val2
diff --strip-trailing-cr -s -q ValidatorFiles/val2 ValidatorFiles/ref2
java Transxchange2GoogleTransit ValidatorFiles/circular.xml www.aagency.org Europe/London 3 ValidatorFiles/val3
diff --strip-trailing-cr -s -q ValidatorFiles/val3 ValidatorFiles/ref3
java Transxchange2GoogleTransit ValidatorFiles/cloverleaf.xml www.aagency.org Europe/London 3 ValidatorFiles/val4
diff --strip-trailing-cr -s -q ValidatorFiles/val4 ValidatorFiles/ref4
java Transxchange2GoogleTransit ValidatorFiles/eye.xml www.aagency.org Europe/London 3 ValidatorFiles/val5
diff --strip-trailing-cr -s -q ValidatorFiles/val5 ValidatorFiles/ref5
java Transxchange2GoogleTransit ValidatorFiles/large.xml www.aagency.org Europe/London 3 ValidatorFiles/val6
diff --strip-trailing-cr -s -q ValidatorFiles/val6 ValidatorFiles/ref6
