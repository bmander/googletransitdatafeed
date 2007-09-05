echo off
java -jar dist\transxchange2GoogleTransit.jar ValidatorFiles\linear.xml http://www.bagency.org Europe/London 3 ValidatorFiles\val1\20040922
comp ValidatorFiles\val1\20040922\*.txt ValidatorFiles\ref1\20040922\*.txt
java -jar dist\transxchange2GoogleTransit.jar ValidatorFiles\large.xml http://www.bagency.org Europe/London 3 ValidatorFiles\val2\20040922
comp ValidatorFiles\val2\20040922\*.txt ValidatorFiles\ref2\20040922\*.txt
java -jar dist\transxchange2GoogleTransit.jar ValidatorFiles\eye.xml http://www.bagency.org Europe/London 3 ValidatorFiles\val3\20040301
comp ValidatorFiles\val3\20040301\*.txt ValidatorFiles\ref3\20040301\*.txt
java -jar dist\transxchange2GoogleTransit.jar ValidatorFiles\express.xml http://www.bagency.org Europe/London 3 ValidatorFiles\val4\20040101
comp ValidatorFiles\val4\20040101\*.txt ValidatorFiles\ref4\20040101\*.txt
java -jar dist\transxchange2GoogleTransit.jar ValidatorFiles\cloverleaf.xml http://www.bagency.org Europe/London 3 ValidatorFiles\val5\20040101
comp ValidatorFiles\val5\20040101\*.txt ValidatorFiles\ref5\20040101\*.txt
java -jar dist\transxchange2GoogleTransit.jar ValidatorFiles\circular.xml http://www.bagency.org Europe/London 3 ValidatorFiles\val6\20040101
comp ValidatorFiles\val6\20040101\*.txt ValidatorFiles\ref6\20040101\*.txt
echo on
