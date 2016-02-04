package com.workouttracker.data;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by nbp184 on 2016/02/04.
 */
public class ScrollLayout extends LinearLayout {

    private LinearLayout interior;

    public ScrollLayout(Context context) {
        super(context);
        init(0);
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(0);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(defStyleAttr);
    }

    private void init(int defStyle) {
        setVerticalScrollBarEnabled(true);
        setOrientation(VERTICAL);
        interior = new LinearLayout(getContext(), null, defStyle);
        interior.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        interior.setOrientation(LinearLayout.VERTICAL);
        addView(interior);
    }

    @Override
    public void addView(View child) {
        if(isInEditMode() || child == interior) {
            super.addView(child);
        } else {
            interior.addView(child);
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if(isInEditMode() || child == interior) {
            super.addView(child, params);
        } else {
            interior.addView(child, params);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(isInEditMode() || child == interior) {
            super.addView(child, index, params);
        } else {
            interior.addView(child, index, params);
        }
    }

}
