package com.workouttracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.workouttracker.data.Exercise;
import com.workouttracker.data.ExerciseHistory;
import com.workouttracker.data.HistoryDot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExerciseHistoryGraphView extends View implements GestureDetector.OnGestureListener {

    public interface FlingListener {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }

    private static final int FINGER_WIDTH = 100;
    private static final float DOT_RADIUS = 7;

    private Exercise ex;
    private float dpToPx;
    private ArrayList<HistoryDot> dots;
    private HistoryDot clickedDot;
    private GestureDetector mDetector;
    private FlingListener listener;

    public ExerciseHistoryGraphView(Context context) {
        super(context);
        initializeStuff();
    }

    public ExerciseHistoryGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeStuff();
    }

    private void initializeStuff() {
        ex = null;
        dpToPx = getContext().getResources().getDisplayMetrics().densityDpi/160f;
        dots = new ArrayList<>();
        clickedDot = null;
        mDetector = new GestureDetector(getContext(), this);
        HistoryDot.clickRadius = FINGER_WIDTH/4*dpToPx;
        listener = null;
    }

    public void setFlingListener(FlingListener listener) {
        this.listener = listener;
    }

    public void setExercise(Exercise ex) {
        this.ex = ex;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        canvas.translate(getPaddingLeft(), getPaddingTop());
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        canvas.drawColor(myGetColor(android.R.color.white));

        Paint p = new Paint();
        p.setTextSize(getResources().getDimensionPixelSize(R.dimen.standard_font_size));

        p.setColor(myGetColor(android.R.color.black));

        Paint.FontMetrics fm = p.getFontMetrics();
        float div = -fm.ascent + 6;
        float topHeight =  (height - div)/2f;
        float bottomHeight = height - topHeight - div;
        float dotRadius = DOT_RADIUS*dpToPx;
        canvas.drawLines(getRectOutline(0, 0, width - 1, topHeight - 1), p);
        canvas.drawLines(getRectOutline(0, topHeight + div, width - 1, height - 1), p);

        if(ex == null || ex.historyCount() == 0) return;

        float strSpace = p.measureText(" 555.0");
        float xSpace = FINGER_WIDTH*dpToPx;
        int timeCount = (int)Math.ceil((width - strSpace - Math.max(dotRadius, strSpace/2f) - 5*dpToPx)/xSpace);

        int visibleCount = timeCount;
        if(visibleCount > ex.historyCount()) {
            visibleCount = ex.historyCount();
        }

        ExerciseHistory hists[] = new ExerciseHistory[visibleCount];

        double weightMax = Double.NEGATIVE_INFINITY;
        double weightMin = Double.POSITIVE_INFINITY;
        int repsMax = Integer.MIN_VALUE;
        int repsMin = Integer.MAX_VALUE;
        for(int i = 0; i < visibleCount; i++) {
            hists[i] = ex.getHistory(i);
            if(hists[i].weight > weightMax) {
                weightMax = hists[i].weight;
            }
            if(hists[i].weight < weightMin) {
                weightMin = hists[i].weight;
            }
            if(hists[i].repetitions > repsMax) {
                repsMax = hists[i].repetitions;
            }
            if(hists[i].repetitions < repsMin) {
                repsMin = hists[i].repetitions;
            }
        }

        float yDiv = 2*(fm.descent - fm.ascent);
        float bottomMargin = dotRadius + 5*dpToPx;

        //weight divisions
        int weightCount = (int)((topHeight -bottomMargin)/yDiv);
        if(weightCount*yDiv - fm.ascent < topHeight - bottomMargin) {
            weightCount += 1;
        }
        double weightDiv;
        if(weightMax > weightMin) {
            weightDiv = (weightMax - weightMin)/(weightCount - 1);
            if(weightDiv <= 1) {
                weightDiv = 1;
            } else if(weightDiv <= 2) {
                weightDiv = 2;
            } else if(weightDiv <= 20) {
                weightDiv = 5f*(float)Math.ceil(weightDiv/5f);
            } else if(weightDiv <= 100){
                weightDiv = 25f*(float)Math.ceil(weightDiv / 25f);
            } else {
                weightDiv = 100f*(float)Math.ceil(weightDiv/100f);
            }
        } else {
            weightDiv = 1;
        }
        double weightStart = weightDiv*Math.floor(weightMin/weightDiv);
        for(int i = 0; i < weightCount; i++) {
            canvas.drawLine(0, topHeight - bottomMargin - i*yDiv, width-1, topHeight - bottomMargin - i*yDiv, p);
            String str = "" +(weightStart+i*weightDiv);
            canvas.drawText(str, width - 1 - dpToPx - p.measureText(str), topHeight - bottomMargin - i*yDiv - dpToPx, p);
        }
        canvas.drawText("Weight (lbs)", 2*dpToPx, div, p);
        //repetition divisions
        int repsCount = (int)((bottomHeight - bottomMargin)/yDiv);
        if(repsCount*yDiv - fm.ascent < bottomHeight - bottomMargin) {
            repsCount += 1;
        }
        int repsDiv;
        if(repsMax > repsMin) {
            repsDiv = (repsMax - repsMin)/(repsCount - 1);
            if(repsDiv <= 1) {
                repsDiv = 1;
            } else if(repsDiv <= 2) {
                repsDiv = 2;
            } else if(repsDiv <= 20) {
                repsDiv = 5*(int)Math.ceil(repsDiv/5.0);
            } else if(repsDiv <= 100){
                repsDiv = 25*(int)Math.ceil(repsDiv/25.0);
            } else {
                repsDiv = 100*(int)Math.ceil(repsDiv/100.0);
            }
        } else {
            repsDiv = 1;
        }
        int repsStart = repsDiv*(repsMin/repsDiv);
        for(int i = 0; i < repsCount; i++) {
            canvas.drawLine(0, height - 1 - bottomMargin - i*yDiv, width-1, height - 1 - bottomMargin - i*yDiv, p);
            String str = "" +(repsStart+i*repsDiv);
            canvas.drawText(str, width - 1 - dpToPx - p.measureText(str), height - 1 - bottomMargin - i*yDiv - dpToPx, p);
        }
        canvas.drawText("Repetitions", 2 * dpToPx, topHeight + 2 * div, p);

        float x = width - strSpace - xSpace*(timeCount - visibleCount - 1) - 1;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        String str;
        Paint wideLine = new Paint(p);
        wideLine.setColor(myGetColor(R.color.exercise_history_graph));
        wideLine.setStrokeWidth(2*dpToPx);
        float strWidth, y, y2;
        dots.clear();
        HistoryDot dot = null;
        for(int i = 0; i < visibleCount; i++) {
            dot = new HistoryDot(hists[i]);
            x -= xSpace;
            //draw date string
            p.setColor(myGetColor(android.R.color.black));
            str = sdf.format(hists[i].date.getTime());
            strWidth = p.measureText(str);
            canvas.drawText(str, x - strWidth/2f, topHeight + div - 5, p);
            canvas.drawLine(x, 0, x, topHeight, p);
            canvas.drawLine(x, topHeight + div, x, height - 1, p);
            //draw weight line and dot
            y = topHeight - bottomMargin - yDiv*((float)hists[i].weight - (float)weightStart)/(float)weightDiv;
            if(i+1 < visibleCount) {
                y2 = topHeight - bottomMargin - yDiv*((float)hists[i+1].weight - (float)weightStart)/(float)weightDiv;
                canvas.drawLine(x, y, x - xSpace, y2, wideLine);
                if(hists[i].duringWorkout) {
                    p.setColor(myGetColor(R.color.exercise_history_during_workout));
                } else {
                    p.setColor(myGetColor(R.color.exercise_history_graph));
                }
                dot.setWeightBounds(new RectF(x - dotRadius, y - dotRadius, x + dotRadius, y + dotRadius));
                canvas.drawOval(dot.getWeightBounds(), p);
            }
            //draw reps line and dot
            p.setColor(myGetColor(R.color.exercise_history_graph));
            y = height - 1 - bottomMargin - yDiv*(hists[i].repetitions - repsStart)/repsDiv;
            if(i+1 < visibleCount) {
                y2 = height - 1 - bottomMargin - yDiv*(hists[i+1].repetitions - repsStart)/repsDiv;
                canvas.drawLine(x,y, x - xSpace, y2, wideLine);
                if(hists[i].duringWorkout) {
                    p.setColor(myGetColor(R.color.exercise_history_during_workout));
                } else {
                    p.setColor(myGetColor(R.color.exercise_history_graph));
                }
                dot.setRepsBounds(new RectF(x - dotRadius, y - dotRadius, x + dotRadius, y + dotRadius));
                canvas.drawOval(dot.getRepsBounds(), p);
                dots.add(dot);
                dot = null;
            }
        }
        Rect clip;
        if(dot == null) {
            dot = new HistoryDot(hists[visibleCount-1]);
        }
        //weight
        p.setColor(myGetColor(R.color.exercise_history_graph));
        y = topHeight - bottomMargin - yDiv*((float)hists[visibleCount-1].weight - (float)weightStart)/(float)weightDiv;
        if(ex.historyCount() > visibleCount) {
            y2 = topHeight - bottomMargin - yDiv*((float)ex.getHistory(visibleCount).weight - (float)weightStart)/(float)weightDiv;
            clip = canvas.getClipBounds();
            canvas.clipRect(0, 0, width - 1, topHeight - 1);
            canvas.drawLine(x, y, x - xSpace, y2, wideLine);
            canvas.clipRect(clip, Region.Op.UNION);
        }
        if(hists[visibleCount-1].duringWorkout) {
            p.setColor(myGetColor(R.color.exercise_history_during_workout));
        } else {
            p.setColor(myGetColor(R.color.exercise_history_graph));
        }
        dot.setWeightBounds(new RectF(x - dotRadius, y - dotRadius, x + dotRadius, y + dotRadius));
        canvas.drawOval(dot.getWeightBounds(), p);

        //reps
        p.setColor(myGetColor(R.color.exercise_history_graph));
        y = height - 1 - bottomMargin - yDiv*(hists[visibleCount-1].repetitions - repsStart)/repsDiv;
        if(ex.historyCount() > visibleCount) {
            y2 = height - 1 - bottomMargin - yDiv * (ex.getHistory(visibleCount).repetitions - repsStart) / repsDiv;
            clip = canvas.getClipBounds();
            canvas.clipRect(0, topHeight + div, width - 1, height - 1);
            canvas.drawLine(x, y, x - xSpace, y2, wideLine);
            canvas.clipRect(clip, Region.Op.UNION);
        }
        if(hists[visibleCount-1].duringWorkout) {
            p.setColor(myGetColor(R.color.exercise_history_during_workout));
        } else {
            p.setColor(myGetColor(R.color.exercise_history_graph));
        }
        dot.setRepsBounds(new RectF(x - dotRadius, y - dotRadius, x + dotRadius, y + dotRadius));
        canvas.drawOval(dot.getRepsBounds(), p);
        dots.add(dot);

        p.setColor(myGetColor(android.R.color.black));
        canvas.drawLines(getRectOutline(0, 0, width - 1, topHeight - 1), p);
        canvas.drawLines(getRectOutline(0, topHeight + div, width - 1, height - 1), p);

        if(clickedDot != null) {
            p.setTextSize(getResources().getDimensionPixelSize(R.dimen.popup_font_size));
            fm = p.getFontMetrics();
            String date = sdf.format(clickedDot.eh.date.getTime());
            String info;
            RectF box;
            if(clickedDot.inWeight) {
                info = clickedDot.eh.weight +" lbs";
                box = new RectF(clickedDot.getWeightBounds());
                clip = new Rect(0, 0, width - 1, (int)topHeight - 1);
            } else {
                info = clickedDot.eh.repetitions +" reps";
                box = new RectF(clickedDot.getRepsBounds());
                clip = new Rect(0, (int)(topHeight + div), width - 1, height - 1);
            }
            float boxWidth = Math.max(p.measureText(date), p.measureText(info)) + 10*dpToPx;
            float boxHeight = -2*fm.ascent +fm.descent + 10*dpToPx;
            float centerX = box.left + (box.right - box.left)/2;
            float topY = box.top;
            box.set(box.left + (box.right - box.left - boxWidth)/2, box.top - dotRadius - boxHeight, box.left + (box.right - box.left - boxWidth)/2 + boxWidth, box.top - dotRadius);
            float[][] triangle = {{centerX, topY}, {centerX + dotRadius, topY - dotRadius}, {centerX - dotRadius, topY - dotRadius}, {centerX, topY}};
            //check right
            while(box.right > clip.right) {
                box.right -= 1;
                box.left -= 1;
                if(box.right - 1 < centerX) {
                    break;
                }
            }
            //check left
            while(box.left < clip.left) {
                box.left += 1;
                box.right += 1;
                if(box.left + 1 > centerX) {
                    break;
                }
            }
            if(box.top < clip.top) {
                box.set(box.left, topY + 3*dotRadius, box.right, topY + 3*dotRadius + box.height());
                triangle[0][1] += 2*dotRadius;
                triangle[1][1] += 4*dotRadius;
                triangle[2][1] += 4*dotRadius;
                triangle[3][1] += 2*dotRadius;
            }
            Path trianglePath = new Path();
            trianglePath.moveTo(triangle[0][0], triangle[0][1]);
            for(int i = 1; i < triangle.length; i++) {
                trianglePath.lineTo(triangle[i][0], triangle[i][1]);
            }
            p.setColor(myGetColor(android.R.color.white));
            canvas.drawRect(box, p);
            p.setColor(myGetColor(android.R.color.black));
            canvas.drawText(info, box.left + 5 * dpToPx, box.bottom - 5 * dpToPx, p);
            p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(date, box.left + 5 * dpToPx, box.top - fm.ascent + 5 * dpToPx, p);
            if(clickedDot.eh.duringWorkout) {
                p.setColor(myGetColor(R.color.exercise_history_during_workout));
            } else {
                p.setColor(myGetColor(R.color.exercise_history_graph));
            }
            canvas.drawPath(trianglePath, p);
            p.setStrokeWidth(4 * dpToPx);
            p.setStrokeJoin(Paint.Join.ROUND);
            Path path = new Path();
            path.addRect(box, Path.Direction.CW);
            p.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, p);
        }
    }

    private float[] getRectOutline(float x1, float y1, float x2, float y2) {
        return new float[]{x1, y1, x2, y1,
                      x2, y1, x2, y2,
                      x2, y2, x1, y2,
                      x1, y2, x1, y1};
    }

    private float[] getRectOutline(RectF rect) {
        return new float[]{rect.left, rect.top, rect.right, rect.top,
                rect.right, rect.top, rect.right, rect.bottom,
                rect.right, rect.bottom, rect.left, rect.bottom,
                rect.left, rect.bottom, rect.left, rect.top};
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
        HistoryDot dot;
        clickedDot = null;
        for(int i = 0; i < dots.size(); i++) {
            dot = dots.get(i);
            if(dot.weightBoundsContains(e.getX(), e.getY())) {
                dot.inWeight = true;
                clickedDot = dot;
                break;
            } else if(dot.repsBoundsContains(e.getX(), e.getY())) {
                dot.inWeight = false;
                clickedDot = dot;
                break;
            }
        }
        invalidate();
        System.out.println("got tap");
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
        if(listener != null) {
            System.out.println("history fling");
            return listener.onFling(e1, e2, velocityX, velocityY);
        } else {
            return false;
        }
    }

}
