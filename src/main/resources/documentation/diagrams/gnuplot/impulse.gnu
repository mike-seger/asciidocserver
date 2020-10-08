set samples 50
plot [0:4*pi] exp(-x/4.)*sin(x) with impulses lw 2 notitle,\
	exp(-x/4.)*sin(x) with points pt 7
