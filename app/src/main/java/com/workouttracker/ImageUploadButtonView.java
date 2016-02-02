package com.workouttracker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 */
public class ImageUploadButtonView extends View {

    private Drawable image;

    public ImageUploadButtonView(Context context) {
        super(context);
        init();
    }

    public ImageUploadButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageUploadButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        image = null;
    }

    public void setImage(Drawable image) {
        this.image = image;
        invalidate();
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

            String str = "Click Here to Add an Image";
            Rect r = null;
            p.getTextBounds(str, 0, str.length(), r);
            canvas.drawText(str, (width - r.width()) / 2f, (height - r.height()) / 2f, p);
            canvas.drawLine(0, 0, width - 1, 0, p);
            canvas.drawLine(width - 1, 0, width - 1, height - 1, p);
            canvas.drawLine(width - 1, height - 1, 0, height - 1, p);
            canvas.drawLine(0, height - 1, 0, 0, p);
        } else {

        }
    }

    private int myGetColor(int resID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(resID, null);
        } else {
            return getResources().getColor(resID);
        }
    }
}
