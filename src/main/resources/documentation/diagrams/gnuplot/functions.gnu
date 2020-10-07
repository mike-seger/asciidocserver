set multiplot
set size 1, 0.5

set origin 0.0,0.5
plot sin(x), log(x)

set origin 0.0,0.0
plot sin(x), log(x), cos(x)

unset multiplot
