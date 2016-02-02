package com.workouttracker.data;

import android.graphics.RectF;

import com.workouttracker.data.ExerciseHistory;

/**
 * Created by nbp184 on 2016/01/27.
 */
public class HistoryDot {

    public static float clickRadius = 0;

    public final ExerciseHistory eh;
    public boolean inWeight;
    private RectF weightBounds;
    private RectF repsBounds;

    public HistoryDot(ExerciseHistory eh) {
        this.eh = eh;
    }

    public void setWeightBounds(RectF weightBounds) {
        this.weightBounds = weightBounds;
    }

    public void setRepsBounds(RectF repsBounds) {
        this.repsBounds = repsBounds;
    }

    public RectF getRepsBounds() {
        return repsBounds;
    }

    public RectF getWeightBounds() {
        return weightBounds;
    }

    public boolean weightBoundsContains(float x, float y) {
        return weightBounds.left - clickRadius <= x && weightBounds.right + clickRadius >= x && weightBounds.top - clickRadius <= y && weightBounds.bottom + clickRadius >= y;
    }

    public boolean repsBoundsContains(float x, float y) {
        return repsBounds.left - clickRadius <= x && repsBounds.right + clickRadius >= x && repsBounds.top - clickRadius <= y && repsBounds.bottom + clickRadius >= y;
    }
}
