#pragma version(1)
#pragma rs java_package_name(com.fivemileview.photospheretoplanets)
#pragma rs_fp_relaxed
#include "rs_debug.rsh"

uint32_t adjust;

/**
 * Compute the color transformation for each pixel at grid location (x, y)
 */
uchar4 __attribute__((kernel)) compute(uchar4 in, uint32_t x, uint32_t y){
    uchar red = in.r;
    uchar green = in.g;
    uchar blue = in.b;
    uchar alpha = in.a;

    /*uchar4 result;
    switch (adjust) {
        case 0:
        case 3:
        default: {
            result = (uchar4){red, green, blue, alpha};
            break;
        }
        case 1: {
            result = (uchar4){green, blue, red, alpha};
            break;
        }
        case 2: {
            result = (uchar4){blue, red, green, alpha};
            break;
        }
    }*/
    uchar4 result;
    switch (adjust/255) {
        case 0:
        case 3:
        default: {
            result = (uchar4){red, green, blue, alpha};
            break;
        }
        case 1: {
            result = (uchar4){green, blue, red, alpha};
            break;
        }
        case 2: {
            result = (uchar4){blue, red, green, alpha};
            break;
        }
    }
    uint32_t fine = adjust%255;
    result = (uchar4){result.r*(255 - fine)/255 + result.g*fine/255, result.g*(255 - fine)/255 + result.b*fine/255, result.b*(255 - fine)/255 + result.r*fine/255, alpha};

    return result;
}