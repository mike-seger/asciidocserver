#!/usr/bin/gnuplot
#
# set terminal pngcairo  transparent enhanced font "arial,10" fontscale 1.0 size 600, 400
# set output 'boxes3d.6.png'
set boxwidth 0.4 absolute
set boxdepth 0.3
set style fill   solid 1.00 border
set grid nopolar
set grid xtics nomxtics ytics nomytics ztics nomztics nortics nomrtics \
 nox2tics nomx2tics noy2tics nomy2tics nocbtics nomcbtics
set grid vertical layerdefault   lt 0 linecolor 0 linewidth 1.000,  lt 0 linecolor 0 linewidth 1.000
unset key
set wall z0  fc  rgb "slategrey"  fillstyle  transparent solid 0.50 border lt -1
set view 59, 24, 1, 1
set style data lines
set xyplane at 0
set title "Full treatment: 3D boxes with pm3d depth sorting and lighting"
set xrange [ * : * ] noreverse writeback
set x2range [ * : * ] noreverse writeback
set yrange [ 0.00000 : 6.00000 ] noreverse nowriteback
set y2range [ * : * ] noreverse writeback
set zrange [ * : * ] noreverse writeback
set cbrange [ * : * ] noreverse writeback
set rrange [ * : * ] noreverse writeback
set pm3d depthorder base
set pm3d interpolate 1,1 flush begin noftriangles border linewidth 1.000 dashtype solid corners2color mean
set pm3d lighting primary 0.5 specular 0.2 spec2 0
rgbfudge(x) = x*51*32768 + (11-x)*51*128 + int(abs(5.5-x)*510/9.)
ti(col) = sprintf("%d",col)
NO_ANIMATION = 1
## Last datafile plotted: "candlesticks.dat"
splot for [col=1:5] '{docdir}/assets/data/candlesticks.dat' using 1:(col):(col*column(col)):(rgbfudge($1))       with boxes fc rgb variable
