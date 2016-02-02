package com.workouttracker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.workouttracker.data.Exercise;
import com.workouttracker.data.ExerciseHistory;
import com.workouttracker.data.Workout;
import com.workouttracker.data.WorkoutList;

import java.text.SimpleDateFormat;

public class ViewWorkoutExerciseActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, ExerciseHistoryGraphView.FlingListener {

    private int index;
    private boolean isCurrent;
    private GestureDetector mDetector;

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
        isCurrent = extras.getBoolean(WorkoutAdapter.CURRENT_INDEX);
        Exercise ex = WorkoutList.current.getExercise(index);

        TextView tv = (TextView)findViewById(R.id.list_item_name);
        tv.setText(ex.name);
        if(isCurrent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setBackgroundColor(getColor(R.color.exercise_history_during_workout));
            } else {
                tv.setBackgroundColor(getResources().getColor(R.color.exercise_history_during_workout));
            }
        }

        tv = (TextView)findViewById(R.id.list_item_weight);
        tv.setText("" + ex.weight);

        tv = (TextView)findViewById(R.id.list_item_repetitions);
        tv.setText("" + ex.repetitions);

        tv = (TextView)findViewById(R.id.exercise_description);
        tv.setText(ex.description);

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

        mDetector = new GestureDetector(this, this);

        ExerciseHistoryGraphView ehgv = (ExerciseHistoryGraphView)findViewById(R.id.image_history);
        ehgv.setExercise(ex);
        ehgv.setFlingListener(this);
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
        return false;
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
        System.out.println(e1.getX() +" " +e1.getY() +" -> " +e2.getX() +" " +e2.getY() +" @ " +velocityX +" " +velocityY);
        return true;
    }
}
