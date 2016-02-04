package com.workouttracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.workouttracker.data.Exercise;
import com.workouttracker.data.Workout;
import com.workouttracker.data.WorkoutList;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements AddExerciseDialog.AddExerciseDialogListener, MyOptionPane.MyOptionPaneListener {

    private static final int EDIT_REQUEST = 1;
    private static final String ADD_EXERCISE_ERROR = "add exercise error message";
    private static final String DELETE_EXERCISE = "delete exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        EditText editText = (EditText)findViewById(R.id.title_editText);
        editText.setText(WorkoutList.current.name);
        WorkoutAdapter adapter = new WorkoutAdapter(WorkoutAdapter.EDIT_VIEW, this);
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        EditText editText = (EditText)findViewById(R.id.title_editText);
        WorkoutList.current.name = editText.getText().toString();
        if(WorkoutList.current.name.isEmpty() && WorkoutList.current.exerciseCount() == 0) {
            setResult(RESULT_CANCELED);
        } else if(WorkoutList.current.name.isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("(d/M/yy)");
            WorkoutList.current.name = format.format(Calendar.getInstance().getTime());
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_OK);
        }
        finish();
    }

    public ViewGroup listItemParent(View view) {
        if(view.getId() == R.id.list_item_parent) {
            return (ViewGroup)view;
        }
        ViewGroup vg = (ViewGroup)view.getParent();
        while(vg.getId() != R.id.list_item_parent) {
            vg = (ViewGroup)vg.getParent();
        }
        return vg;
    }

    public void editExercise(View view) {
        TextView position = (TextView)listItemParent(view).findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());
        Intent intent = new Intent(this, EditExerciseActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Workout.EXERCISE_INDEX, index);
        intent.putExtras(extras);
        startActivityForResult(intent, EDIT_REQUEST);
    }

    public void addExercise(View view) {
        TextView position = (TextView)listItemParent(view).findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());
        AddExerciseDialog.showDialog(getSupportFragmentManager(), "add exercise", index == WorkoutAdapter.TOP_BUTTON);
    }

    private int savedIndex;

    public void deleteExercise(View view) {
        TextView position = (TextView)listItemParent(view).findViewById(R.id.list_item_position);
        savedIndex = Integer.parseInt(position.getText().toString());
        MyOptionPane.showConfirmDialog(getSupportFragmentManager(), DELETE_EXERCISE, "Are you sure you wish to delete exercise \"" + WorkoutList.current.getExercise(savedIndex).name + "\"?", "Confirm Delete");
    }

    public void moveExerciseUp(View view) {
        TextView position = (TextView)listItemParent(view).findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());

        Exercise ex = WorkoutList.current.removeExercise(index);
        WorkoutList.current.placeExercise(ex, index-1);

        ListView lv = (ListView)findViewById(R.id.listView);
        WorkoutAdapter wa = (WorkoutAdapter)lv.getAdapter();
        wa.notifyDataSetChanged();
    }

    public void moveExerciseDown(View view) {
        TextView position = (TextView)listItemParent(view).findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());

        Exercise ex = WorkoutList.current.removeExercise(index);
        WorkoutList.current.placeExercise(ex, index+1);

        ListView lv = (ListView)findViewById(R.id.listView);
        WorkoutAdapter wa = (WorkoutAdapter)lv.getAdapter();
        wa.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == EDIT_REQUEST) {
                ListView lv = (ListView)findViewById(R.id.listView);
                WorkoutAdapter wa = (WorkoutAdapter)lv.getAdapter();
                wa.notifyDataSetChanged();
            }
        }
    }

    private Bundle data;

    @Override
    public void onAddExerciseDialogClick(Bundle data) {
        if(data.containsKey(AddExerciseDialog.ERROR)) {
            this.data = data;
            MyOptionPane.showMessageDialog(getSupportFragmentManager(), ADD_EXERCISE_ERROR, data.getString(AddExerciseDialog.ERROR), "Error");
            return;
        }
        Exercise ex;
        ListView lv;
        WorkoutAdapter wa;
        switch(data.getInt(AddExerciseDialog.WHICH)) {
            case AddExerciseDialog.POSITIVE_BUTTON:
                ex = new Exercise(data);
                ex.makeHistory(false);
                if(data.getBoolean(AddExerciseDialog.ADD_AT_START)) {
                    WorkoutList.current.addExerciseTop(ex);
                } else {
                    WorkoutList.current.addExerciseBottom(ex);
                }
                lv = (ListView)findViewById(R.id.listView);
                wa = (WorkoutAdapter)lv.getAdapter();
                wa.notifyDataSetChanged();
                break;
            case AddExerciseDialog.NEUTRAL_BUTTON:
                ex = new Exercise(data);
                ex.makeHistory(false);
                if(data.getBoolean(AddExerciseDialog.ADD_AT_START)) {
                    WorkoutList.current.addExerciseTop(ex);
                } else {
                    WorkoutList.current.addExerciseBottom(ex);
                }
                lv = (ListView)findViewById(R.id.listView);
                wa = (WorkoutAdapter)lv.getAdapter();
                wa.notifyDataSetChanged();
                AddExerciseDialog.showDialog(getSupportFragmentManager(), "add exercise", data.getBoolean(AddExerciseDialog.ADD_AT_START));
                break;
        }
    }

    @Override
    public void onMyOptionPaneClick(String tag) {
        if(tag.compareTo(ADD_EXERCISE_ERROR) == 0) {
            AddExerciseDialog.showDialog(getSupportFragmentManager(), "add exercise", data);
            data = null;
        }
    }

    @Override
    public void onMyOptionPaneClick(String tag, int result) {
        if(tag.compareTo(DELETE_EXERCISE) == 0) {
            if(result == MyOptionPane.YES_OPTION) {
                WorkoutList.current.removeExercise(savedIndex);
                ListView lv = (ListView)findViewById(R.id.listView);
                WorkoutAdapter wa = (WorkoutAdapter)lv.getAdapter();
                wa.notifyDataSetChanged();
            }
        }
    }
}
