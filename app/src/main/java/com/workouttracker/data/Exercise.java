package com.workouttracker.data;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.workouttracker.AddExerciseDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nbp184 on 2016/01/22.
 */
public class Exercise implements Iterable<ExerciseHistory> {

    protected static final String endExercise = ""+((char)3);
    protected static final String nlSub = ""+((char)0);


    public static Exercise load(BufferedReader inFile) throws IOException {
        Exercise rv = new Exercise(inFile.readLine());
        rv.description = inFile.readLine().replaceAll(nlSub, "\n");
        rv.weight = Double.parseDouble(inFile.readLine());
        rv.weightJump = Double.parseDouble(inFile.readLine());
        rv.repetitions = Integer.parseInt(inFile.readLine());
        rv.repetitionJump = Integer.parseInt(inFile.readLine());
        String line = inFile.readLine();
        while(line != null && line.compareTo(endExercise) != 0) {
            rv.history.add(ExerciseHistory.load(rv, inFile));
            line = inFile.readLine();
        }
        return rv;
    }

    public String name;
    public String description;
    public double weight;
    public double weightJump;
    public int repetitions;
    public int repetitionJump;
    private ArrayList<ExerciseHistory> history;

    public Exercise(String name) {
        this.name = name;
        description = "";
        weight = 0;
        weightJump = 1;
        repetitions = 0;
        repetitionJump = 1;
        history = new ArrayList<>();
    }

    public Exercise(Bundle bundle) {
        name = bundle.getString(AddExerciseDialog.NAME);
        description = "";
        weight = bundle.getDouble(AddExerciseDialog.WEIGHT);
        weightJump = bundle.getDouble(AddExerciseDialog.WEIGHT_JUMP);
        repetitions = bundle.getInt(AddExerciseDialog.REPETITIONS);
        repetitionJump = bundle.getInt(AddExerciseDialog.REPETITIONS_JUMP);
        history = new ArrayList<>();
    }

    public void makeHistory(boolean duringWorkout) {
        ExerciseHistory hist = new ExerciseHistory(this, duringWorkout);
        if(!history.isEmpty()) {
            if (duringWorkout == history.get(0).duringWorkout) {
                history.set(0, hist);
            } else {
                history.add(0, hist);
            }
        } else {
            history.add(hist);
        }
    }

    public Iterator<ExerciseHistory> iterator() {
        return history.iterator();
    }

    public void save(PrintWriter outFile) {
        outFile.println(name);
        outFile.println(description.replaceAll("\n", nlSub));
        outFile.println(weight);
        outFile.println(weightJump);
        outFile.println(repetitions);
        outFile.println(repetitionJump);
        for(ExerciseHistory hist : history) {
            outFile.println();
            hist.save(outFile);
        }
        outFile.println(endExercise);
    }

    public double weightDown() {
        weight -= weightJump;
        return weight;
    }

    public double weightUp() {
        weight += weightJump;
        return weight;
    }

    public int repsDown() {
        repetitions -= repetitionJump;
        return repetitions;
    }

    public int repsUp() {
        repetitions += repetitionJump;
        return repetitions;
    }

    public int historyCount() {
        return history.size();
    }

    public ExerciseHistory getHistory(int index) {
        return history.get(index);
    }
}
