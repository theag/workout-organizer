package com.workouttracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.workouttracker.data.Exercise;
import com.workouttracker.data.Workout;
import com.workouttracker.data.WorkoutList;

public class EditExerciseActivity extends AppCompatActivity {

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(WorkoutList.current.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        index = extras.getInt(Workout.EXERCISE_INDEX);
        Exercise ex = WorkoutList.current.getExercise(index);

        EditText et = (EditText)findViewById(R.id.exercise_name);
        et.setText(ex.name);

        et = (EditText)findViewById(R.id.exercise_description);
        et.setText(ex.description);

        et = (EditText)findViewById(R.id.exercise_weight);
        et.setText(""+ex.weight);

        et = (EditText)findViewById(R.id.exercise_weight_jump);
        et.setText(""+ex.weightJump);

        et = (EditText)findViewById(R.id.exercise_repetitions);
        et.setText(""+ex.repetitions);

        et = (EditText)findViewById(R.id.exercise_repetitions_jump);
        et.setText(""+ex.repetitionJump);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Exercise ex = WorkoutList.current.getExercise(index);
        EditText et = (EditText)findViewById(R.id.exercise_name);
        ex.name = et.getText().toString();
        et = (EditText)findViewById(R.id.exercise_description);
        ex.description = et.getText().toString();
        et = (EditText)findViewById(R.id.exercise_weight);
        ex.weight = Double.parseDouble(et.getText().toString());
        et = (EditText)findViewById(R.id.exercise_weight_jump);
        ex.weightJump = Double.parseDouble(et.getText().toString());
        et = (EditText)findViewById(R.id.exercise_repetitions);
        ex.repetitions = Integer.parseInt(et.getText().toString());
        et = (EditText)findViewById(R.id.exercise_repetitions_jump);
        ex.repetitionJump = Integer.parseInt(et.getText().toString());
        ex.makeHistory(false);
        setResult(RESULT_OK);
        finish();
    }
}
