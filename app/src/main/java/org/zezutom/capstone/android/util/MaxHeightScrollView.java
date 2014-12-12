package org.zezutom.capstone.android.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

import org.zezutom.capstone.android.R;

/**
 * courtesy:
 *  https://gist.github.com/JMPergar/439aaa3249fa184c7c0c
 *  http://stackoverflow.com/questions/17683789/android-scrollview-set-height-for-displayed-content
 */
public class MaxHeightScrollView extends ScrollView {

    public static final String TAG = "ScrollViewWithMaxHeight";

    public static final int DEFAULT_HEIGHT_VALUE = 200;

    public static int WITHOUT_MAX_HEIGHT_VALUE = -1;

    private int maxHeight = WITHOUT_MAX_HEIGHT_VALUE;

    public MaxHeightScrollView(Context context) {
        super(context);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
            maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight, DEFAULT_HEIGHT_VALUE);
            styledAttrs.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (maxHeight != WITHOUT_MAX_HEIGHT_VALUE
                    && heightSize > maxHeight) {
                heightSize = maxHeight;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
            getLayoutParams().height = heightSize;
        } catch (Exception e) {
            Log.e(TAG, "Error forcing height:" + e.getMessage());
        } finally {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
