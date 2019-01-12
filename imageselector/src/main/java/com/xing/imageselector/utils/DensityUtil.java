package com.xing.imageselector.utils;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtil {
    private DensityUtil() {
        throw new UnsupportedOperationException("can't be initial");
    }

    public static float dp2Px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
