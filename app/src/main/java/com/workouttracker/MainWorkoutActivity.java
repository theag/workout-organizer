package com.workouttracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.workouttracker.data.Workout;
import com.workouttracker.data.WorkoutList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

public class MainWorkoutActivity extends AppCompatActivity implements MyOptionPane.MyOptionPaneListener {

    private static final int EDIT_REQUEST = 1;
    private static final int WORKOUT_REQUEST = 2;
    private static final String CONFIRM_DELETE = "confirm delete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_workout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if(WorkoutList.current.unloaded) {
            try {
                WorkoutList.current = Workout.load(new BufferedReader(new FileReader(new File(getFilesDir(), "workout" +WorkoutList.current.key + ".wrk"))), WorkoutList.current.key);
                cleanOutImageFiles();
            } catch (IOException e) {
                e.printStackTrace();
                setResult(RESULT_CANCELED);
                finish();
            }
        }

        WorkoutAdapter adapter = new WorkoutAdapter(WorkoutAdapter.BASE_VIEW, this);
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
        getSupportActionBar().setTitle(WorkoutList.current.name);
        WorkoutList.current.setViewed();
    }

    private void cleanOutImageFiles() {
        File[] imageFiles = getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg") && filename.startsWith("workout-" +WorkoutList.current.key +"_");
            }
        });
        for(File file : imageFiles) {
            file.delete();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_edit:
                intent = new Intent(this, EditActivity.class);
                startActivityForResult(intent, EDIT_REQUEST);
                return true;
            case R.id.action_delete:
                MyOptionPane.showConfirmDialog(getSupportFragmentManager(), CONFIRM_DELETE, "Are you sure you wish to delete this workout?", "Confirm Delete");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startWorkout(View view) {
        Intent intent = new Intent(this, WorkoutActivity.class);
        startActivityForResult(intent, WORKOUT_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REQUEST) {
            if(resultCode == RESULT_OK) {
                getSupportActionBar().setTitle(WorkoutList.current.name);
                ListView lv = (ListView) findViewById(R.id.listView);
                WorkoutAdapter wa = (WorkoutAdapter) lv.getAdapter();
                wa.notifyDataSetChanged();
            } else if(resultCode == RESULT_CANCELED) {
                setResult(RESULT_CANCELED);
                finish();
            }
        } else if (requestCode == WORKOUT_REQUEST) {
            if(resultCode == RESULT_OK) {
                ListView lv = (ListView) findViewById(R.id.listView);
                WorkoutAdapter wa = (WorkoutAdapter) lv.getAdapter();
                wa.notifyDataSetChanged();
            }
        }
    }

    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    public ViewGroup listItemParent(View view) {
        ViewGroup vg = (ViewGroup)view.getParent();
        while(vg.getId() != R.id.list_item_parent) {
            vg = (ViewGroup)vg.getParent();
        }
        return vg;
    }

    public void viewExercise(View view) {
        TextView position = (TextView)listItemParent(view).findViewById(R.id.list_item_position);
        int index = Integer.parseInt(position.getText().toString());
        Intent intent = new Intent(this, ViewExerciseActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Workout.EXERCISE_INDEX, index);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        if(WorkoutList.current != null) {
            try {
                PrintWriter outFile = new PrintWriter(new File(getFilesDir(), "workout" +WorkoutList.current.key + ".wrk"));
                WorkoutList.current.save(outFile, getFilesDir());
                outFile.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMyOptionPaneClick(String tag) {

    }

    @Override
    public void onMyOptionPaneClick(String tag, int result) {
        if(tag.compareTo(CONFIRM_DELETE) == 0) {
            if(result == MyOptionPane.YES_OPTION) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}
