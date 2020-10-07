#set term x11 enhanced 
set yrange [-1.5:1.5] 
set xrange [0:6.3] 
set ytics nomirror 
set y2tics 0,.1
set y2range [0:1.2]
set style fill pattern 5
set key at graph .9, .9 spacing 3 font "Helvetica, 14"
set xlabel "Time (sec.)" font "Courier, 18"
set ylabel "Amplitude" font "Courier, 18"
set y2label "Error Magnitude" font "Courier, 18"
set title "Fourier Approximation to Square Wave" font "Times-Roman, 24"
set label \
  "sgn(x) = 4/{/Symbol p}&{x}~{~@{/Symbol=24 S}{- .5/*.7n=1,3,5,...}}{.9/Symbol=18 \245}& {xx}1/n sin(n{/Symbol p}x)" \
  font "Times-Roman, 18" \
  at graph .04, .65
plot 'assets/data/fourier-rectangle.dat' using 1:2:(sgn($2)) with filledcurves notitle,\
  '' using 1:(sgn($2)) with lines title "Square Wave",\
  '' using 1:2 with lines lw 2 title "Fourier approximation",\
  '' using 1:(abs(sgn($2)-$2)) with lines axis x1y2 title "Error magnitude"
