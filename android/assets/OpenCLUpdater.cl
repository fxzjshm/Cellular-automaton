inline int getRealX(int x, int width);
inline int getRealY(int y, int height);

// TODO fix unknoen behaviour on the edge
__kernel void update(__global const int *oldMapBool, __global int *newMapBool,
                     const int width, const int height){
    int w = width, h = height;
    int gid = get_global_id(0);
    int x = gid / h, y = gid % h, // TODO 2D thread
        neighbourCount = 0;
    int xm1 = getRealX(x - 1, w), xp1 = getRealX(x + 1, w),
        ym1 = getRealY(y - 1, h), yp1 = getRealY(y + 1, h),
        xm1Xh = xm1 * h, xXh = x * h, xp1Xh = xp1 * h;

    // TODO branch()
    // @off
    // @formatter:off
    if (oldMapBool[xm1Xh + ym1]) neighbourCount++;
    if (oldMapBool[ xXh  + ym1]) neighbourCount++;
    if (oldMapBool[xp1Xh + ym1]) neighbourCount++;
    if (oldMapBool[xm1Xh +  y ]) neighbourCount++;
    if (oldMapBool[xp1Xh +  y ]) neighbourCount++;
    if (oldMapBool[xm1Xh + yp1]) neighbourCount++;
    if (oldMapBool[ xXh  + yp1]) neighbourCount++;
    if (oldMapBool[xp1Xh + yp1]) neighbourCount++;
    // @on
    // @formatter:on

    if (3 == neighbourCount) newMapBool[gid] = 1;
    else if (2 == neighbourCount) newMapBool[gid] = oldMapBool[gid];
    else newMapBool[gid] = 0;

    // if(newMapBool[xXh + y] != oldMapBool[xXh + y])printf("%d,%d,%d\n",x,y,neighbourCount);
}

inline int getRealX(int x, int width) {
    while (x < 0) {
        x += width;
    }
    const int w1 = width - 1;
    while (x > w1) {
        x -= width;
    }
    return x;
}

inline int getRealY(int y, int height) {
    while (y < 0) {
        y += height;
    }
    const int h1 = height - 1;
    while (y > h1) {
        y -= height;
    }
    return y;
}