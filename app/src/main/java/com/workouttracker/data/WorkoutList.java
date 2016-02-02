package com.workouttracker.data;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.workouttracker.SortDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by nbp184 on 2016/01/22.
 */
public class WorkoutList {

    public static Workout current = null;

    private static final String unitSep = "" +((char)31);
    private static WorkoutList instance = null;

    public static WorkoutList load(BufferedReader inFile) throws IOException {
        instance = new WorkoutList();
        String line = inFile.readLine();
        instance.nextKey = Integer.parseInt(line);
        line = inFile.readLine();
        while(line != null) {
            int index = line.indexOf(unitSep);
            if(index < 0) {
                throw new IOException("IndexOutOfBoundsException: " +line);
            }
            instance.addWorkout(Integer.parseInt(line.substring(0, index)), line.substring(index+1));
            line = inFile.readLine();
        }
        return instance;
    }

    public static WorkoutList load() {
        instance = new WorkoutList();
        instance.addNewWorkout(loadGoodlifeCircuit());
        return instance;
    }

    public static WorkoutList getInstance(Context context) {
        if(instance == null) {
            try {
                BufferedReader inFile = new BufferedReader(new FileReader(new File(context.getFilesDir(), "list.txt")));
                instance = load(inFile);
                inFile.close();

            } catch (IOException e) {
                e.printStackTrace();
                instance = load();
                try {
                    PrintWriter outFile = new PrintWriter(new File(context.getFilesDir(), "workout0.wrk"));
                    instance.getWorkout(0).save(outFile);
                    outFile.close();
                } catch (FileNotFoundException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return instance;
    }

    public static WorkoutList getInstance() {
        return instance;
    }

    private static Workout loadGoodlifeCircuit() {
        Workout rv = new Workout();
        rv.name = "Goodlife Circuit";
        Exercise ex = rv.addNewExerciseBottom("Lower Back Extension");
        ex.weight = 65;
        ex.repetitions = 12;
        ex = rv.addNewExerciseBottom("2");
        ex.weight = 45;
        ex.repetitions = 12;
        ex = rv.addNewExerciseBottom("Leg Curl");
        ex.weight = 40;
        ex.repetitions = 12;
        ex = rv.addNewExerciseBottom("Seated Row");
        ex.weight = 35;
        ex.repetitions = 12;
        ex = rv.addNewExerciseBottom("Chest Press");
        ex.weight = 50;
        ex.repetitions = 12;
        ex = rv.addNewExerciseBottom("Lateral Raise");
        ex.weight = 25;
        ex.repetitions = 12;
        ex = rv.addNewExerciseBottom("Arm Curl");
        ex.weight = 20;
        ex.repetitions = 12;
        ex = rv.addNewExerciseBottom("Arm Extension");
        ex.weight = 20;
        ex.repetitions = 12;
        ex = rv.addNewExerciseBottom("Abdominals");
        ex.weight = 0;
        ex.repetitions = 12;
        rv.makeHistory(false);
        return rv;
    }

    private HashMap<Integer, Workout> workouts;
    private int nextKey;
    private ArrayList<Workout> trash;

    public WorkoutList() {
        workouts = new HashMap<>();
        nextKey = 0;
        trash = new ArrayList<>();
    }

    public void addWorkout(int key, String name) {
        workouts.put(key, new Workout(name, key));
    }

    public void addNewWorkout(Workout workout) {
        workout.key = nextKey;
        workouts.put(nextKey, workout);
        nextKey++;
    }

    public void save(PrintWriter outFile) {
        outFile.println(nextKey);
        for(Workout w : workouts.values()) {
            outFile.println(w.key +unitSep +w.name);
        }
    }

    public Workout getWorkout(int key) {
        return workouts.get(key);
    }

    public void loadWorkout(int key, BufferedReader inFile) throws IOException {
        workouts.put(key, Workout.load(inFile, key));
    }

    public void removeWorkout(int key) {
        trash.add(workouts.remove(key));
    }

    public String[] getTrashFiles() {
        ArrayList<String> arr = new ArrayList<>(trash.size());
        for(Workout w : trash) {
            arr.add("workout" +w.key +".wrk");
        }
        String[] rv = new String[arr.size()];
        arr.toArray(rv);
        return rv;
    }

    private String stripExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return filename.substring(0, index);
    }

    public ArrayAdapter<Workout> getAdapter(Context context, int sortOrder) {
        ArrayList<Workout> arr = new ArrayList<>();
        arr.addAll(workouts.values());
        Collections.sort(arr, getComparator(sortOrder));
        return new ArrayAdapter<Workout>(context, android.R.layout.simple_list_item_1, arr);
    }

    public ArrayList<Workout> getSearchResults(String query, int sortOrder) {
        ArrayList<Workout> arr1 = new ArrayList<>();
        ArrayList<Workout> arr2 = new ArrayList<>();
        for(Workout w : workouts.values()) {
            if(w.name.toLowerCase().startsWith(query)) {
                arr1.add(w);
            } else if(w.name.toLowerCase().contains(query)) {
                arr2.add(w);
            }
        }
        Comparator<Workout> comparator = getComparator(sortOrder);
        Collections.sort(arr1, comparator);
        Collections.sort(arr2, comparator);
        arr1.addAll(arr2);
        return arr1;
    }

    private Comparator<Workout> getComparator(int sortOrder) {
        switch(sortOrder) {
            case SortDialog.RECENTLY_VIEWED:
                return new Comparator<Workout>() {
                    @Override
                    public int compare(Workout lhs, Workout rhs) {
                        if(lhs.viewed != null && rhs.viewed != null) {
                            return lhs.viewed.compareTo(rhs.viewed);
                        } else if(lhs.viewed != null) {
                            return -1;
                        } else if(rhs.viewed != null) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                };
            case SortDialog.CREATION:
                return new Comparator<Workout>() {
                    @Override
                    public int compare(Workout lhs, Workout rhs) {
                        return lhs.creation.compareTo(rhs.creation);
                    }
                };
            case SortDialog.ALPHABETICALLY:
                return new Comparator<Workout>() {
                    @Override
                    public int compare(Workout lhs, Workout rhs) {
                        return lhs.name.compareTo(rhs.name);
                    }
                };
            default:
            case SortDialog.RECENTLY_USED:
                return new Comparator<Workout>() {
                    @Override
                    public int compare(Workout lhs, Workout rhs) {
                        if(lhs.used != null && rhs.used != null) {
                            return lhs.used.compareTo(rhs.used);
                        } else if(lhs.used != null) {
                            return -1;
                        } else if(rhs.used != null) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                };
        }
    }

    public ArrayList<Object[]> getSearchData(int sortOrder) {
        ArrayList<Workout> arr = new ArrayList<>();
        arr.addAll(workouts.values());
        Collections.sort(arr, getComparator(sortOrder));
        ArrayList<Object[]> rv = new ArrayList<>();
        Object[] row;
        for(Workout w : arr) {
            row = new Object[3];
            row[0] = w.key;
            row[1] = w.name;
            row[2] = w.key;
            rv.add(row);
        }
        return rv;
    }

}
