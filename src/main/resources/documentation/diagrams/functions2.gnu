set yrange [-1.5:1.5]
set xrange [0:6.3]
set ytics nomirror
set y2tics 0,.1
set y2range [0:1.2]
set style fill pattern 5
set xlabel "Time (sec.)"
set ylabel "Amplitude"
set y2label "Error Magnitude"
set title "Fourier Approximation to Square Wave"
plot 'ch2.dat' using 1:2:(sgn($2)) with filledcurves,\'' using 1:2 with lines...
