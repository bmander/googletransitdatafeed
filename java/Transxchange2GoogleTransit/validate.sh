java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/linear.xml http://www.bagency.org Europe/London 3 ValidatorFiles/val1
diff --strip-trailing-cr -s -q ValidatorFiles/val1 ValidatorFiles/ref1/20040922
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/large.xml http://www.bagency.org Europe/London 3 ValidatorFiles/val2
diff --strip-trailing-cr -s -q ValidatorFiles/val2 ValidatorFiles/ref2/20040922
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/eye.xml http://www.bagency.org Europe/London 3 ValidatorFiles/val3
diff --strip-trailing-cr -s -q ValidatorFiles/val3 ValidatorFiles/ref3/20040301
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/express.xml http://www.bagency.org Europe/London 3 ValidatorFiles/val4
diff --strip-trailing-cr -s -q ValidatorFiles/val4 ValidatorFiles/ref4/20040101
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/cloverleaf.xml http://www.bagency.org Europe/London 3 ValidatorFiles/val5
diff --strip-trailing-cr -s -q ValidatorFiles/val5 ValidatorFiles/ref5/20040101
java -jar dist/transxchange2GoogleTransit.jar ValidatorFiles/circular.xml http://www.bagency.org Europe/London 3 ValidatorFiles/val6
diff --strip-trailing-cr -s -q ValidatorFiles/val6 ValidatorFiles/ref6/20040101
