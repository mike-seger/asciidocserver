#!/usr/bin/gnuplot
#
# Showcasing the viridis colormap
#
# AUTHOR: Hagen Wierstorf
# gnuplot 5.0 patchlevel 3

reset

unset key

# border
set style line 11 lc rgb '#808080' lt 1
set border 3 front ls 11
set tics nomirror out scale 0.75

# colorbar
# disable colorbar tics
set cbtics scale 0
# viridis palette colors
load '{docdir}/assets/data/viridis.pal'

set xrange [0:59]
set yrange [0:59]
set xlabel 'x (Âµm)'
set ylabel 'y (Âµm)'

plot '{docdir}/assets/data/test_colormap.txt' u ($1/3.0):($2/3.0):($3/1000.0) matrix with image