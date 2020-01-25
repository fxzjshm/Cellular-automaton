inline int getRealX(int x, int width);
inline int getRealY(int y, int height);

__kernel void update(__global const bool *oldMapBool, __global bool *newMapBool,
                     __global const int *width, __global const int *height){
    int w = *width, h = *height;
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

    if (3 == neighbourCount) newMapBool[xXh + y] = true;
    if (neighbourCount < 2 || neighbourCount > 3) newMapBool[xXh + y] = false;
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