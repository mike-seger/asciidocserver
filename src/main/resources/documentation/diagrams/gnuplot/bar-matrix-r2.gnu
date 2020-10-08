# used by bar-matrix.gnu

unset parametric
yy=yy-1
d=0.3
set yrange [*:*]
fit [0:Y] f(x,yy) filename u 0:xx via d
set yrange [-w:Y+2*w]
set parametric
r=R(yy/Y); g=G(yy/Y); b=B(yy/Y)
if(C==0 && d<0) \
set palette defined (0 r g b, 1 r g b);\
splot w*u+xx, w*v+yy, d w pm3d;\
r=r/cd; g=g/cd; b=b/cd;\
set palette defined (0 r g b, 1 r g b);\
splot w*u+xx, yy, v*d w pm3d;\
r=r/cd; g=g/cd; b=b/cd;\
set palette defined (0 r g b, 1 r g b);\
splot w+xx, yy+w*u, v*d w pm3d;

if(C==1 && d>0) \
set palette defined (0 r g b, 1 r g b);\
splot w*u+xx, yy+w*v, d w pm3d;\
r=r/cd; g=g/cd; b=b/cd;\
set palette defined (0 r g b, 1 r g b);\
splot w*u+xx, yy, v*d w pm3d;\
r=r/cd; g=g/cd; b=b/cd;\
set palette defined (0 r g b, 1 r g b);\
splot w+xx, yy+w*u, v*d w pm3d;

if(yy>0) reread
