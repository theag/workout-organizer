package com.workouttracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.workouttracker.data.Workout;
import com.workouttracker.data.WorkoutList;

public class WorkoutActivity extends AppCompatActivity {

    private static final int VIEW_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(WorkoutList.current.name);
        WorkoutAdapter adapter = new WorkoutAdapter(WorkoutAdapter.DOING_VIEW, this);
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
    }

    public int getViewIndex(View view) {
        ListView lv = (ListView)findViewById(R.id.listView);
        for(int i = 0; i < lv.getChildCount(); i++) {
            if(lv.getChildAt(i) == view) {
                return i;
            }
        }
        return -1;
    }

    public void shiftCurrentExercise(View view) {
        ListView lv = (ListView)findViewById(R.id.listView);
        WorkoutAdapter adapter = (WorkoutAdapter)lv.getAdapter();
        switch(view.getId()) {
            case R.id.next_exercise_button:
                adapter.setCurrentExercise(adapter.getCurrentExercise()+1);
                findViewById(R.id.prev_exercise_button).setEnabled(true);
                if(adapter.getCurrentExercise() == adapter.getCount()-1) {
                    view.setEnabled(false);
                }
                break;
            case R.id.prev_exercise_button:
                adapter.setCurrentExercise(adapter.getCurrentExercise()-1);
                findViewById(R.id.next_exercise_button).setEnabled(true);
                if(adapter.getCurrentExercise() == 0) {
                    view.setEnabled(false);
                }
                break;
        }
        lv.setSelection(adapter.getCurrentExercise());
    }

    public void weightDown(View view) {
        ViewGroup parent = (ViewGroup)view.getParent().getParent().getParent();
        int index = getViewIndex(parent);
        if(index < 0) {
            return;
        }
        double weight = WorkoutList.current.getExercise(index).weightDown();
        EditText et = (EditText)parent.findViewById(R.id.list_item_weight);
        et.setText(""+weight);
    }

    public void weightUp(View view) {
        ViewGroup parent = (ViewGroup)view.getParent().getParent().getParent();
        int index = getViewIndex(parent);
        if(index < 0) {
            return;
        }
        double weight = WorkoutList.current.getExercise(index).weightUp();
        EditText et = (EditText)parent.findViewById(R.id.list_item_weight);
        et.setText(""+weight);
    }

    public void repsDown(View view) {
        ViewGroup parent = (ViewGroup)view.getParent().getParent().getParent();
        int index = getViewIndex(parent);
        if(index < 0) {
            return;
        }
        int reps = WorkoutList.current.getExercise(index).repsDown();
        EditText et = (EditText)parent.findViewById(R.id.list_item_repetitions);
        et.setText(""+reps);
    }

    public void repsUp(View view) {
        ViewGroup parent = (ViewGroup)view.getParent().getParent().getParent();
        int index = getViewIndex(parent);
        if(index < 0) {
            return;
        }
        int reps = WorkoutList.current.getExercise(index).repsUp();
        EditText et = (EditText)parent.findViewById(R.id.list_item_repetitions);
        et.setText(""+reps);
    }

    public void viewExercise(View view) {
        ViewGroup parent = (ViewGroup)view.getParent();
        if(view.getId() == R.id.list_item_name) {
            parent = (ViewGroup)parent.getParent();
        }
        int index = getViewIndex(parent);
        if(index < 0) {
            return;
        }
        Intent intent = new Intent(this, ViewWorkoutExerciseActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Workout.EXERCISE_INDEX, index);
        intent.putExtras(extras);
        startActivityForResult(intent, VIEW_REQUEST);
    }


    public void endWorkout(View view) {
        WorkoutList.current.makeHistory(true);
        WorkoutList.current.setUsed();
        setResult(RESULT_OK);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == VIEW_REQUEST) {
                ListView lv = (ListView) findViewById(R.id.listView);
                WorkoutAdapter wa = (WorkoutAdapter) lv.getAdapter();
                wa.notifyDataSetChanged();
            }
        }
    }
}
