#!/usr/bin/gnuplot
#
# pm3d lighting model with specular highlighting
#
unset key
unset border
unset colorbox
set view 33, 291, 1.2, 1.3
set bmargin 0
set tmargin 0
set samples 200, 200
set isosamples 200, 200
set xyplane -0.05
set format ""
set grid x y lt black
unset ztics
set urange [ -20. : 20. ]
set vrange [ -20. : 20. ]
set zrange [ -0.25 : 0.5 ]

set style fill solid 1.00 noborder
set pm3d implicit at s depth noborder

slice(x,y) = (x**2+y**2 < 10.0) ? 1.0 : (x**2+y**2 > 300.0) ? NaN : sin(abs(atan2(x,y)))
sinc2(x,y) = sin(sqrt(x**2+y**2))/sqrt(x**2+y**2)
flatten(x,y) = sqrt(x**2+y**2)/5.
F(x,y) =  sinc2(x,y) * slice(x,y) * flatten(x,y)

set title "pm3d lighting model with specular highlighting"
set pm3d lighting primary 0.5 specular 0.2
splot '++' using 1:2:(F($1,$2)):(0xAAEEEE) nosurface lc rgb variable
