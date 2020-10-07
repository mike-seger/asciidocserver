set mapping cylindrical
unset tics
unset border
set hidden
set xrange [-pi : pi] 
set yrange [-pi : pi] 
set zrange [0 : pi] 
set iso 60
unset key
splot '++' using 1:2:(sin($2)) with lines
