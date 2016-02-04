package com.workouttracker;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.workouttracker.data.Exercise;
import com.workouttracker.data.ExerciseHistory;
import com.workouttracker.data.Workout;
import com.workouttracker.data.WorkoutList;

import java.text.SimpleDateFormat;

public class ViewWorkoutExerciseActivity extends AppCompatActivity {

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout_exercise);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(WorkoutList.current.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        index = extras.getInt(Workout.EXERCISE_INDEX);
        Exercise ex = WorkoutList.current.getExercise(index);

        TextView tv = (TextView)findViewById(R.id.list_item_name);
        tv.setText(ex.name);

        tv = (TextView)findViewById(R.id.list_item_weight);
        tv.setText("" + ex.weight);

        tv = (TextView)findViewById(R.id.list_item_repetitions);
        tv.setText("" + ex.repetitions);

        tv = (TextView)findViewById(R.id.exercise_description);
        tv.setText(ex.description);

        if(ex.image != null) {
            ImageView iv = (ImageView)findViewById(R.id.exercise_image);
            iv.setVisibility(View.VISIBLE);
            iv.setImageDrawable(new BitmapDrawable(getResources(), ex.image));
        }

        //ListView lv = (ListView)findViewById(R.id.list_history);
        //lv.setAdapter(new HistoryAdapter(this, ex));
        TableLayout tl = (TableLayout)findViewById(R.id.list_history);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        for(ExerciseHistory eh : ex) {
            view = inflater.inflate(R.layout.exercise_history_table_row, null);
            tv = (TextView)view.findViewById(R.id.history_date);
            tv.setText(sdf.format(eh.date.getTime()).toString());

            tv = (TextView)view.findViewById(R.id.history_weight);
            tv.setText(eh.weight +" " +getString(R.string.text_weight_unit));

            tv = (TextView)view.findViewById(R.id.history_repetitions);
            tv.setText(eh.repetitions+" " +getString(R.string.text_repetitions_unit));

            if(eh.duringWorkout) {
                view.setBackgroundResource(R.color.exercise_history_during_workout);
            }

            tl.addView(view);
        }

        ExerciseHistoryGraphView ehgv = (ExerciseHistoryGraphView)findViewById(R.id.image_history);
        ehgv.setExercise(ex);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    public void weightDown(View view) {
        LinearLayout parent = (LinearLayout)view.getParent();
        double weight = WorkoutList.current.getExercise(index).weightDown();
        EditText et = (EditText)parent.findViewById(R.id.list_item_weight);
        et.setText("" + weight);
    }

    public void weightUp(View view) {
        LinearLayout parent = (LinearLayout)view.getParent();
        double weight = WorkoutList.current.getExercise(index).weightUp();
        EditText et = (EditText)parent.findViewById(R.id.list_item_weight);
        et.setText(""+weight);
    }

    public void repsDown(View view) {
        LinearLayout parent = (LinearLayout)view.getParent();
        int reps = WorkoutList.current.getExercise(index).repsDown();
        EditText et = (EditText)parent.findViewById(R.id.list_item_repetitions);
        et.setText(""+reps);
    }

    public void repsUp(View view) {
        LinearLayout parent = (LinearLayout)view.getParent();
        int reps = WorkoutList.current.getExercise(index).repsUp();
        EditText et = (EditText)parent.findViewById(R.id.list_item_repetitions);
        et.setText("" + reps);
    }

    public void switchHistoryView(View view) {
        RadioGroup rg = (RadioGroup)findViewById(R.id.switch_graph_table);
        if(rg.getCheckedRadioButtonId() == R.id.history_graph_button) {
            View view2 = findViewById(R.id.image_history);
            view2.setVisibility(View.VISIBLE);
            view2 = findViewById(R.id.list_history);
            view2.setVisibility(View.GONE);
        } else if(rg.getCheckedRadioButtonId() == R.id.history_table_button) {
            View view2 = findViewById(R.id.image_history);
            view2.setVisibility(View.GONE);
            view2 = findViewById(R.id.list_history);
            view2.setVisibility(View.VISIBLE);
        }

    }

}
