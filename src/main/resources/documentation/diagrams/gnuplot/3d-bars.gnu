reset
set view 60, 20
set parametric
set isosample 2, 2
unset key
unset colorbox
set ticslevel 0
set urange [0:0.5]
set vrange [0:1]
set xrange [0:7]
set yrange [-3.500000:3.500000]
set zrange [0:0.300000]
set multiplot
set border 1+2+4+8+16+32+64+256+512
unset ytics
unset xtics
set palette model RGB functions 0.9, 0.9,0.95
splot 14*u, -3.500000+7*v, 0 w pm3d
unset border
unset xtics
unset ytics
unset ztics
set palette model RGB functions 1, 254.0/255.0, 189.0/255.0
splot 0, -3.500000+7*v, 0.600000*u w pm3d, 14*u, 3.500000, 0.300000*v w pm3d
set palette defined (0 1 0.5 0.5, 1 1 0.5 0.5)
splot 0+u, v, 0.100000 w pm3d,\
1+u, v, 0.200000 w pm3d,\
2+u, v, 0.200000 w pm3d,\
3+u, v, 0.050000 w pm3d,\
4+u, v, 0.150000 w pm3d,\
5+u, v, 0.300000 w pm3d,\
6+u, v, 0.100000 w pm3d
set palette defined (0 1 0.3 0.3, 1 1 0.3 0.3)
splot 0+0.5, 2*u, v*0.100000 w pm3d,\
1+0.5, 2*u, v*0.200000 w pm3d,\
2+0.5, 2*u, v*0.200000 w pm3d,\
3+0.5, 2*u, v*0.050000 w pm3d,\
4+0.5, 2*u, v*0.150000 w pm3d,\
5+0.5, 2*u, v*0.300000 w pm3d,\
6+0.5, 2*u, v*0.100000 w pm3d
set palette defined (0 0.8 0 0, 1 0.8 0 0)
set label 1 "1989" at 0.300000, -2.500000 rotate by 40
set label 2 "1990" at 1.300000, -2.500000 rotate by 40
set label 3 "1991" at 2.300000, -2.500000 rotate by 40
set label 4 "1992" at 3.300000, -2.500000 rotate by 40
set label 5 "1993" at 4.300000, -2.500000 rotate by 40
set label 6 "1994" at 5.300000, -2.500000 rotate by 40
set label 7 "1995" at 6.300000, -2.500000 rotate by 40
splot 0+u, 0, v*0.100000 w pm3d,\
1+u, 0, v*0.200000 w pm3d,\
2+u, 0, v*0.200000 w pm3d,\
3+u, 0, v*0.050000 w pm3d,\
4+u, 0, v*0.150000 w pm3d,\
5+u, 0, v*0.300000 w pm3d,\
6+u, 0, v*0.100000 w pm3d
unset multiplot
