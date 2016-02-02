package com.workouttracker.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by nbp184 on 2016/01/22.
 */
public class Workout implements Iterable<Exercise> {

    public static final String EXERCISE_INDEX = "exercise index";

    protected static final String endWorkout = ""+((char)4);
    private static final String nullStr = ""+((char)0);

    public static Workout load(BufferedReader inFile, int key) throws IOException {
        Workout rv = new Workout(inFile.readLine(), Long.parseLong(inFile.readLine()));
        String line = inFile.readLine();
        if(line.compareTo(nullStr) != 0) {
            rv.viewed = Calendar.getInstance();
            rv.viewed.setTimeInMillis(Long.parseLong(line));
        }
        line = inFile.readLine();
        if(line.compareTo(nullStr) != 0) {
            rv.used = Calendar.getInstance();
            rv.used.setTimeInMillis(Long.parseLong(line));
        }
        line = inFile.readLine();
        while(line != null && line.compareTo(endWorkout) != 0) {
            rv.exercises.add(Exercise.load(inFile));
            line = inFile.readLine();
        }
        rv.key = key;
        return rv;
    }

    public boolean unloaded;
    public int key;
    public String name;
    public final Calendar creation;
    public Calendar viewed;
    public Calendar used;
    private ArrayList<Exercise> exercises;

    public Workout() {
        name = "";
        key = -1;
        unloaded = false;
        exercises = new ArrayList<>();
        creation = Calendar.getInstance();
        viewed = null;
        used = null;
    }

    public Workout(String name, long time) {
        this.name = name;
        creation = Calendar.getInstance();
        creation.setTimeInMillis(time);
        key = -1;
        unloaded = false;
        exercises = new ArrayList<>();
        viewed = null;
        used = null;
    }

    public Workout(String name, int key) {
        this.name = name;
        this.key = key;
        unloaded = true;
        exercises = new ArrayList<>();
        creation = Calendar.getInstance();
        viewed = null;
        used = null;
    }

    public Exercise removeExercise(int index) {
        return exercises.remove(index);
    }

    public void placeExercise(Exercise ex, int index) {
        exercises.add(index, ex);
    }

    public void save(PrintWriter outFile, File fileDir) {
        outFile.println(name);
        outFile.println(creation.getTimeInMillis());
        if(viewed == null) {
            outFile.println(nullStr);
        } else {
            outFile.println(viewed.getTimeInMillis());
        }
        if(used == null) {
            outFile.println(nullStr);
        } else {
            outFile.println(used.getTimeInMillis());
        }
        Random rand = new Random();
        for(Exercise ex : exercises) {
            if(ex.image != null && ex.imageFilename == null) {
                int value = rand.nextInt();
                File imageFile = new File(fileDir, "workout-" +key +"_image-" +value +".jpg");
                while(imageFile.exists()) {
                    value++;
                    imageFile = new File(fileDir, "workout-" +key +"_image-" +value +".jpg");
                }
                ex.imageFilename = imageFile.getAbsolutePath();
            }
            outFile.println();
            ex.save(outFile);
        }
        outFile.println(endWorkout);
    }

    public int exerciseCount() {
        return exercises.size();
    }

    public Exercise getExercise(int index) {
        return exercises.get(index);
    }

    public void addExerciseTop(Exercise ex) {
        exercises.add(0, ex);
    }

    public void addExerciseBottom(Exercise ex) {
        exercises.add(ex);
    }

    @Override
    public String toString() {
        return name;
    }

    public void makeHistory(boolean duringWorkout) {
        for(Exercise ex : exercises) {
            ex.makeHistory(duringWorkout);
        }
    }

    public void setViewed() {
        viewed = Calendar.getInstance();
    }

    public void setUsed() {
        used = Calendar.getInstance();
    }

    @Override
    public Iterator<Exercise> iterator() {
        return exercises.iterator();
    }

    public Exercise addNewExerciseBottom(String name) {
        Exercise ex = new Exercise(name);
        exercises.add(ex);
        return ex;
    }
}
