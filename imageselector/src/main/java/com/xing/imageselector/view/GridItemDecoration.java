package com.xing.imageselector.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xing.imageselector.utils.DensityUtil;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Paint paint;
    private int color;
    private int dividerHeight;

    public GridItemDecoration(Context context) {
        this.mContext = context;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = (int) DensityUtil.dp2Px(mContext, 2);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int top = childView.getBottom();
        }
    }
}
