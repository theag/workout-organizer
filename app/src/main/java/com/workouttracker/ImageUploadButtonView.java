package com.workouttracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 *
 */
public class ImageUploadButtonView extends View {

    private Bitmap image;
    private float dpToPx;

    public ImageUploadButtonView(Context context) {
        super(context);
        init(null, 0);
    }

    public ImageUploadButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ImageUploadButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        image = null;
        dpToPx = getResources().getDisplayMetrics().densityDpi/160f;
    }

    public void setImage(Bitmap image) {
        this.image = image;
        System.out.println("invalidate");
        invalidate();
    }

    public Bitmap getImage() {
        return image;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        canvas.translate(getPaddingLeft(), getPaddingTop());
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        if(image == null) {
            Paint p = new Paint();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                p.setColor(getContext().getColor(R.color.gray));
            } else {
                p.setColor(getResources().getColor(R.color.gray));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, 0, width - 1, height - 1, 2 * dpToPx, 2 * dpToPx, p);
            } else {
                canvas.drawRoundRect(new RectF(0, 0, width-1, height-1), 2*dpToPx, 2*dpToPx, p);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                p.setColor(getContext().getColor(android.R.color.black));
            } else {
                p.setColor(getResources().getColor(android.R.color.black));
            }
            p.setTextSize(getResources().getDimensionPixelSize(R.dimen.button_font_size));
            Rect r = new Rect();
            String str = getResources().getString(R.string.button_add_image);
            p.getTextBounds(str, 0, str.length(), r);
            Paint.FontMetrics fm = p.getFontMetrics();
            canvas.drawText(str, (width - r.width())/2f, (height - fm.ascent)/2f, p);
        } else {
            //TODO: fuss about scaling
            Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(image, null, canvas.getClipBounds(), p);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        Paint p = new Paint();
        p.setTextSize(getResources().getDimensionPixelSize(R.dimen.button_font_size));
        Rect r = new Rect();
        String str = getResources().getString(R.string.button_add_image);
        p.getTextBounds(str, 0, str.length(), r);
        switch(MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                if(image == null) {
                    width = Math.min(width, r.width() + 2*getPaddingLeft() + 2*getPaddingRight());
                } else {
                    width = Math.min(width, image.getScaledWidth(getResources().getDisplayMetrics()) + getPaddingRight() + getPaddingLeft());
                }
                break;
        }
        int height = MeasureSpec.getSize(heightMeasureSpec);
        switch(MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                if(image == null) {
                    height = Math.min(height, r.height() + 2*getPaddingTop() + 2*getPaddingBottom());
                } else {
                    height = Math.min(height, image.getScaledHeight(getResources().getDisplayMetrics()) + getPaddingTop() + getPaddingBottom());
                }
                break;
        }
        setMeasuredDimension(width, height);
    }

}
