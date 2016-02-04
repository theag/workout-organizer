package com.workouttracker.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Created by nbp184 on 2016/01/22.
 */
public class ExerciseHistory implements Comparable<ExerciseHistory> {

    public static ExerciseHistory load(Exercise parent, BufferedReader inFile) throws IOException {
        boolean duringWorkout = Boolean.parseBoolean(inFile.readLine());
        double weight = Double.parseDouble(inFile.readLine());
        int repetitions = Integer.parseInt(inFile.readLine());
        ExerciseHistory rv = new ExerciseHistory(parent, weight, repetitions, duringWorkout);
        rv.date.setTimeInMillis(Long.parseLong(inFile.readLine()));
        return rv;
    }

    public final Exercise parent;
    public final Calendar date;
    public final double weight;
    public final int repetitions;
    public final boolean duringWorkout;

    public ExerciseHistory(Exercise parent, boolean duringWorkout) {
        this.parent = parent;
        this.date = Calendar.getInstance();
        weight = parent.weight;
        repetitions = parent.repetitions;
        this.duringWorkout = duringWorkout;
    }

    private ExerciseHistory(Exercise parent, double weight, int repetitions, boolean duringWorkout) {
        this.parent = parent;
        this.weight = weight;
        this.repetitions = repetitions;
        this.duringWorkout = duringWorkout;
        this.date = Calendar.getInstance();
    }

    public int compareTo(ExerciseHistory o) {
        if(date.get(Calendar.YEAR) != o.date.get(Calendar.YEAR)) {
            return o.date.get(Calendar.YEAR) - date.get(Calendar.YEAR);
        } else if(date.get(Calendar.MONTH) != o.date.get(Calendar.MONTH)) {
            return o.date.get(Calendar.MONTH) - date.get(Calendar.MONTH);
        } else if(date.get(Calendar.DAY_OF_MONTH) != o.date.get(Calendar.DAY_OF_MONTH)) {
            return o.date.get(Calendar.DAY_OF_MONTH) - date.get(Calendar.DAY_OF_MONTH);
        } else {
            return -date.compareTo(o.date);
        }
    }

    public boolean sameDay(ExerciseHistory o) {
        if(date.get(Calendar.YEAR) != o.date.get(Calendar.YEAR)) {
            return false;
        } else if(date.get(Calendar.MONTH) != o.date.get(Calendar.MONTH)) {
            return false;
        } else if(date.get(Calendar.DAY_OF_MONTH) != o.date.get(Calendar.DAY_OF_MONTH)) {
            return false;
        } else {
            return true;
        }
    }

    public void save(PrintWriter outFile) {
        outFile.println(duringWorkout);
        outFile.println(weight);
        outFile.println(repetitions);
        outFile.println(date.getTimeInMillis());
    }

}