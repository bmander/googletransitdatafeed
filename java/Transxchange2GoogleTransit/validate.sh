java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/linear.xml www.bagency.org Europe/London 3 ValidatorFiles/val1
diff --strip-trailing-cr -s -q ValidatorFiles/val1/20040922 ValidatorFiles/ref1/20040922
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/large.xml www.bagency.org Europe/London 3 ValidatorFiles/val2
diff --strip-trailing-cr -s -q ValidatorFiles/val2/20040922 ValidatorFiles/ref2/20040922
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/eye.xml www.bagency.org Europe/London 3 ValidatorFiles/val3
diff --strip-trailing-cr -s -q ValidatorFiles/val3/20040301 ValidatorFiles/ref3/20040301
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/express.xml www.bagency.org Europe/London 3 ValidatorFiles/val4
diff --strip-trailing-cr -s -q ValidatorFiles/val4/20040101 ValidatorFiles/ref4/20040101
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/cloverleaf.xml www.bagency.org Europe/London 3 ValidatorFiles/val5
diff --strip-trailing-cr -s -q ValidatorFiles/val5/20040101 ValidatorFiles/ref5/20040101
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/circular.xml www.bagency.org Europe/London 3 ValidatorFiles/val6
diff --strip-trailing-cr -s -q ValidatorFiles/val6/20040101 ValidatorFiles/ref6/20040101
