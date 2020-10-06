#!/usr/bin/gnuplot
#
# set terminal pngcairo  transparent enhanced font "arial,10" fontscale 1.0 size 600, 400
# set output 'hidden_compare.1.png'
unset border
set dummy u, v
unset key
set parametric
set view 225, 206, 1.25, 0.5
set isosamples 200, 200
set hidden3d back offset 1 trianglepattern 3 undefined 1 altdiagonal bentover
set style data lines
unset xtics
unset ytics
unset ztics
set title "pm3d 2-color surface"
set urange [ -3.14159 : 3.14159 ] noreverse nowriteback
set vrange [ 0.250000 : 3.14159 ] noreverse nowriteback
set xrange [ * : * ] noreverse writeback
set x2range [ * : * ] noreverse writeback
set yrange [ * : * ] noreverse writeback
set y2range [ * : * ] noreverse writeback
set zrange [ * : * ] noreverse writeback
set cbrange [ * : * ] noreverse writeback
set rrange [ * : * ] noreverse writeback
set pm3d depthorder
set pm3d lighting primary 0.33 specular 0.2 spec2 0.3
unset colorbox
NO_ANIMATION = 1
splot (cos(u)+.5*cos(u)*cos(v))*(1.+sin(11.*u)/10.),       (sin(u)+.5*sin(u)*cos(v))*(1.+sin(11.*u)/10.),       0.5*sin(v) with pm3d fc ls 3
