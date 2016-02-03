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
import android.widget.TextView;

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

    public ViewGroup listItemParent(View view) {
        ViewGroup vg = (ViewGroup)view.getParent();
        while(vg.getId() != R.id.list_item_parent) {
            vg = (ViewGroup)vg.getParent();
        }
        return vg;
    }

    public void weightDown(View view) {
        ViewGroup parent = listItemParent(view);
        TextView position = (TextView)parent.findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());
        double weight = WorkoutList.current.getExercise(index).weightDown();
        EditText et = (EditText)parent.findViewById(R.id.list_item_weight);
        et.setText(""+weight);
    }

    public void weightUp(View view) {
        ViewGroup parent = listItemParent(view);
        TextView position = (TextView)parent.findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());
        double weight = WorkoutList.current.getExercise(index).weightUp();
        EditText et = (EditText)parent.findViewById(R.id.list_item_weight);
        et.setText(""+weight);
    }

    public void repsDown(View view) {
        ViewGroup parent = listItemParent(view);
        TextView position = (TextView)parent.findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());
        int reps = WorkoutList.current.getExercise(index).repsDown();
        EditText et = (EditText)parent.findViewById(R.id.list_item_repetitions);
        et.setText(""+reps);
    }

    public void repsUp(View view) {
        ViewGroup parent = listItemParent(view);
        TextView position = (TextView)parent.findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());
        int reps = WorkoutList.current.getExercise(index).repsUp();
        EditText et = (EditText)parent.findViewById(R.id.list_item_repetitions);
        et.setText(""+reps);
    }

    public void viewExercise(View view) {
        ViewGroup parent = listItemParent(view);
        TextView position = (TextView)parent.findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());
        Intent intent = new Intent(this, ViewWorkoutExerciseActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Workout.EXERCISE_INDEX, index);
        int current = ((WorkoutAdapter)((ListView)findViewById(R.id.listView)).getAdapter()).getCurrentExercise();
        extras.putBoolean(WorkoutAdapter.CURRENT_INDEX, index == current);
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
        if (requestCode == VIEW_REQUEST) {
            if (resultCode == RESULT_OK) {
                ListView lv = (ListView) findViewById(R.id.listView);
                WorkoutAdapter wa = (WorkoutAdapter) lv.getAdapter();
                wa.notifyDataSetChanged();
                if(data.getIntExtra(Workout.EXERCISE_INDEX, -1) == wa.getCurrentExercise()) {
                    lv.setSelection(wa.getCurrentExercise());
                }
            } else if(resultCode == ViewWorkoutExerciseActivity.RESULT_SWIPE_LEFT) {
                ListView lv = (ListView) findViewById(R.id.listView);
                WorkoutAdapter adapter = (WorkoutAdapter) lv.getAdapter();
                if(adapter.getCurrentExercise() < adapter.getCount()-1) {
                    adapter.setCurrentExercise(adapter.getCurrentExercise() + 1);
                    findViewById(R.id.prev_exercise_button).setEnabled(true);
                    if (adapter.getCurrentExercise() == adapter.getCount() - 1) {
                        findViewById(R.id.next_exercise_button).setEnabled(false);
                    }
                    Intent intent = new Intent(this, ViewWorkoutExerciseActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt(Workout.EXERCISE_INDEX, adapter.getCurrentExercise());
                    extras.putBoolean(WorkoutAdapter.CURRENT_INDEX, true);
                    intent.putExtras(extras);
                    startActivityForResult(intent, VIEW_REQUEST);
                } else {
                    lv.setSelection(adapter.getCurrentExercise());
                }
            } else if(resultCode == ViewWorkoutExerciseActivity.RESULT_SWIPE_RIGHT) {
                ListView lv = (ListView) findViewById(R.id.listView);
                WorkoutAdapter adapter = (WorkoutAdapter) lv.getAdapter();
                if(adapter.getCurrentExercise() > 0) {
                    adapter.setCurrentExercise(adapter.getCurrentExercise() - 1);
                    findViewById(R.id.next_exercise_button).setEnabled(true);
                    if (adapter.getCurrentExercise() == 0) {
                        findViewById(R.id.prev_exercise_button).setEnabled(false);
                    }
                    Intent intent = new Intent(this, ViewWorkoutExerciseActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt(Workout.EXERCISE_INDEX, adapter.getCurrentExercise());
                    extras.putBoolean(WorkoutAdapter.CURRENT_INDEX, true);
                    intent.putExtras(extras);
                    startActivityForResult(intent, VIEW_REQUEST);
                } else {
                    lv.setSelection(adapter.getCurrentExercise());
                }
            }
        }
    }
}
