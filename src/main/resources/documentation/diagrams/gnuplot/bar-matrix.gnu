reset
filename="assets/data/bar-matrix.dat"
w=0.4
FIT_LIMIT=1e-8
mult=1.05; cd=1.4

f(x,a)=(abs(x-a)<0.5?d:0)
R(x)=abs(2*x-0.5); G(x)=sin(x*pi); B(x)=cos(x*pi/2.0)

set view 60, 20
set parametric; set isosample 2, 2
unset border; unset tics; unset key; unset colorbox
set ticslevel 0
set urange [0:1]; set vrange [0:1]

splot 'assets/data/bar-matrix.dat' mat
set zrange [mult*GPVAL_DATA_Z_MIN:mult*GPVAL_DATA_Z_MAX]
Y=GPVAL_DATA_Y_MAX+1
X=GPVAL_DATA_X_MAX+1

set xrange [1-w:X+2*w]

set multiplot

C=0; xx=1; yy=Y
call 'diagrams/gnuplot/bar-matrix-r.gnu'

set palette model RGB functions 0.9, 0.9,0.95
splot -w+u*(X+3*w), -w+v*(Y+w), 0 w pm3d

C=1; xx=1; yy=Y
call 'diagrams/gnuplot/bar-matrix-r.gnu'
unset multiplot
