package com.workouttracker;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
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
import java.io.IOException;
import java.io.PrintWriter;

public class MainWorkoutActivity extends AppCompatActivity implements MyOptionPane.MyOptionPaneListener {

    private static final int EDIT_REQUEST = 1;
    private static final int WORKOUT_REQUEST = 2;
    private static final String CONFIRM_DELETE = "confirm delete";
    private static final int EXPORT_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_workout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if(WorkoutList.current.unloaded) {
            try {
                WorkoutList.current = Workout.load(new BufferedReader(new FileReader(new File(getFilesDir(), "workout" +WorkoutList.current.key + ".wrk"))), WorkoutList.current.key);
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
                return true;
            case R.id.action_share:
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, WorkoutList.current.printWorkout());
                intent.setType("text/plain");
                // Always use string resources for UI text.
                // This says something like "Share this photo with"
                String title = "Share this workout with..";//getResources().getString(R.string.chooser_title);
                // Create intent to show chooser
                Intent chooser = Intent.createChooser(intent, title);

                // Verify the intent will resolve to at least one activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    System.out.println("nope");
                }
                return true;
            case R.id.action_export:
                try {
                    //TODO: XML!!!! better make some XML bitch
                    File file = new File(getFilesDir(), "workout" +WorkoutList.current.key + ".wrk");
                    PrintWriter outFile = new PrintWriter(file);
                    WorkoutList.current.save(outFile);
                    outFile.close();
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("application/xml");
                    Uri fileUri = FileProvider.getUriForFile(this, "com.workouttracker.fileprovider", file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    title = "Share this workout with..";
                    chooser = Intent.createChooser(intent, title);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(chooser, EXPORT_REQUEST);
                    } else {
                        System.out.println("nope");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("double nope");
                }
                return true;
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
        } else if(requestCode == EXPORT_REQUEST) {
            //File file = new File(getFilesDir(), "workout" +WorkoutList.current.key + ".txt");
            //file.delete();
        }
    }

    public void onBackPressed() {
        setResult(RESULT_OK);
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
                WorkoutList.current.save(outFile);
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
