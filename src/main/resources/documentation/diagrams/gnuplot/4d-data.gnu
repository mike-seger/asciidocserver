#!/usr/bin/gnuplot
#
# set terminal pngcairo  transparent enhanced font "arial,10" fontscale 1.0 size 600, 400
# set output 'heatmaps.6.png'
set format cb "%4.1f"
set view 49, 28, 1, 1.48
set samples 25, 25
set isosamples 50, 50
set xyplane relative 0
set cbtics border in scale 0,0 mirror norotate  autojustify
set title "4D data (3D Heat Map)\nIndependent value color-mapped onto 3D surface"
set title  offset character 0, 1, 0 font "" textcolor lt -1 norotate
set urange [ 5.00000 : 35.0000 ] noreverse nowriteback
set vrange [ 5.00000 : 35.0000 ] noreverse nowriteback
set xlabel "x"
set xlabel  offset character 3, 0, 0 font "" textcolor lt -1 norotate
set xrange [ * : * ] noreverse writeback
set x2range [ * : * ] noreverse writeback
set ylabel "y"
set ylabel  offset character -5, 0, 0 font "" textcolor lt -1 rotate
set yrange [ * : * ] noreverse writeback
set y2range [ * : * ] noreverse writeback
set zlabel "z"
set zlabel  offset character 2, 0, 0 font "" textcolor lt -1 norotate
set zrange [ * : * ] noreverse writeback
set cbrange [ * : * ] noreverse writeback
set rrange [ * : * ] noreverse writeback
set pm3d implicit at s
set colorbox user
set colorbox vertical origin screen 0.9, 0.2 size screen 0.03, 0.6 front  noinvert noborder
sinc(x,y) = sin(sqrt((x-20.)**2+(y-20.)**2))/sqrt((x-20.)**2+(y-20.)**2)
Z(x,y) = 100. * (sinc(x,y) + 1.5)
color(x,y) = 10. * (1.1 + sin((x-20.)/5.)*cos((y-20.)/10.))
NO_ANIMATION = 1
## Last datafile plotted: "++"
splot '++' using 1:2:(Z($1,$2)):(color($1,$2)) with pm3d title "4 data columns x/y/z/color"
