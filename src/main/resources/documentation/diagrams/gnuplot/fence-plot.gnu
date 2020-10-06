#!/usr/bin/gnuplot

unset hidden3d

#
# 2) Each fence is a curve drawn with zerrorfill
#    surface appearance is controlled by "set pm3d" and "set style fill"
#    If depth-cueing is needed, use "set pm3d depthorder"
#
set format z "%.1f"
unset key
unset arrow
unset label
set view 70,25
unset xtics
unset ytics
set title "fence plot constructed with zerrorfill"
set zrange [-1:1]
set arrow 1 from 5,-.5,-1.2 to 5,.5,-1.2 lt -1
set label 1 "sampling on Y" at 6,-0.1,-1
set xlabel "X axis"  rotate parallel
set xrange [-5:4]
set yrange [-0.5:0.5]
sinc(u,v) = sin(sqrt(u**2+v**2)) / sqrt(u**2+v**2)

set style fill  solid 0.75 noborder
splot for [x=-4:4][y=-50:50:3] '+' using (x):($1/100.):(-1):(-1):(sinc($1/10., 1.+x)) with zerrorfill
