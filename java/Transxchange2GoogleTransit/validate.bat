echo off
java Transxchange2GoogleTransit ValidatorFiles\linear.xml www.aagency.org Europe/London 3 ValidatorFiles\val1
comp ValidatorFiles\val1\*.txt ValidatorFiles\ref1\*.txt
java Transxchange2GoogleTransit ValidatorFiles\express.xml www.aagency.org Europe/London 3 ValidatorFiles\val2
comp ValidatorFiles\val2\*.txt ValidatorFiles\ref2\*.txt
java Transxchange2GoogleTransit ValidatorFiles\circular.xml www.aagency.org Europe/London 3 ValidatorFiles\val3
comp ValidatorFiles\val3\*.txt ValidatorFiles\ref3\*.txt
java Transxchange2GoogleTransit ValidatorFiles\cloverleaf.xml www.aagency.org Europe/London 3 ValidatorFiles\val4
comp ValidatorFiles\val4\*.txt ValidatorFiles\ref4\*.txt
java Transxchange2GoogleTransit ValidatorFiles\eye.xml www.aagency.org Europe/London 3 ValidatorFiles\val5
comp ValidatorFiles\val5\*.txt ValidatorFiles\ref5\*.txt
java Transxchange2GoogleTransit ValidatorFiles\large.xml www.aagency.org Europe/London 3 ValidatorFiles\val6
comp ValidatorFiles\val6\*.txt ValidatorFiles\ref6\*.txt
echo on
