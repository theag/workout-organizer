package com.workouttracker;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 *
 */
public class ImageUploadButtonView extends View implements GestureDetector.OnGestureListener {

    public interface ImageUploadButtonListener {
        void onTap(boolean alreadyHas);
    }

    private static final int[] attributesToGet = {android.R.attr.layout_width, android.R.attr.layout_height};

    private Bitmap image;
    private float dpToPx;
    private ImageUploadButtonListener listener;
    private GestureDetector mDetector;

    public ImageUploadButtonView(Context context) {
        super(context);
        init(null, 0);
    }

    public ImageUploadButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ImageUploadButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        image = null;
        listener = null;
        mDetector = new GestureDetector(getContext(), this);
        dpToPx = getContext().getResources().getDisplayMetrics().densityDpi/160f;
        TypedArray a = getContext().obtainStyledAttributes(attrs, attributesToGet, defStyle, 0);
        int width = a.getLayoutDimension(0, ViewGroup.LayoutParams.MATCH_PARENT);
        int height = a.getLayoutDimension(1, ViewGroup.LayoutParams.WRAP_CONTENT);
        a.recycle();
        View parent = (View)getParent();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.width = parent.getWidth()/2;
        }
        if(height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            if(width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = layoutParams.width;
            } else {
                layoutParams.height = parent.getWidth();
            }
        }
        //setLayoutParams(layoutParams);
    }

    public void setImage(Bitmap image) {
        this.image = image;
        invalidate();
    }

    public Bitmap getImage() {
        return image;
    }

    public void setOnClickListener(ImageUploadButtonListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        canvas.translate(getPaddingLeft(), getPaddingTop());
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        if(image == null) {
            canvas.drawColor(myGetColor(android.R.color.white));

            Paint p = new Paint();
            p.setColor(myGetColor(R.color.light_gray));
            canvas.drawRect(0, 0, width, height, p);

            p.setTextSize(getResources().getDimensionPixelSize(R.dimen.standard_font_size));
            p.setColor(myGetColor(android.R.color.black));

            String str = "Tap Here to Add an Image";
            Rect r = new Rect();
            p.getTextBounds(str, 0, str.length(), r);
            while(r.width() > width - 4*dpToPx) {
                p.setTextSize(p.getTextSize() - 1);
                p.getTextBounds(str, 0, str.length(), r);
            }

            canvas.drawText(str, (width - r.width()) / 2f, (height - r.height()) / 2f, p);
            canvas.drawLine(0, 0, width - 1, 0, p);
            canvas.drawLine(width - 1, 0, width - 1, height - 1, p);
            canvas.drawLine(width - 1, height - 1, 0, height - 1, p);
            canvas.drawLine(0, height - 1, 0, 0, p);
        } else {
            Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(image, null, new RectF(canvas.getClipBounds()), p);
        }
    }

    private int myGetColor(int resID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(resID, null);
        } else {
            return getResources().getColor(resID);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if(listener != null) {
            listener.onTap(image != null);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
