#!/usr/bin/gnuplot

set title "PM3D surfaces with specular highlighting"

unset border
unset key
set object 1 rect from screen 0, 0, 0 to screen 1, 1, 0 behind
set object 1 rect fc  rgb "gray"  fillstyle solid 1.0  border -1
set view 236, 339, 1.245, 1.0
set isosamples 75, 75
unset xtics
unset ytics
unset ztics
set parametric
set dummy u,v
set urange [ -pi : pi ]
set vrange [ -pi : pi ]

set palette rgbformulae 8, 9, 7
set style fill solid 1.0 noborder
set pm3d depthorder noborder
set pm3d lighting specular 0.6

splot cos(u)+.5*cos(u)*cos(v),sin(u)+.5*sin(u)*cos(v),.5*sin(v) with pm3d, \
    1+cos(u)+.5*cos(u)*cos(v),.5*sin(v),sin(u)+.5*sin(u)*cos(v) with pm3d