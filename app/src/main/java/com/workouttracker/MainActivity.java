package com.workouttracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.workouttracker.data.Workout;
import com.workouttracker.data.WorkoutList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity implements SortDialog.SortDialogListener {

    public static final String CURRENT_SORT_ORDER = "current sort order";

    private static final int CREATE_REQUEST = 1;
    private static final int VIEW_REQUEST = 2;

    private WorkoutList wl;
    private int sortOrder;
    private boolean searchStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        wl = WorkoutList.getInstance(this);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        sortOrder = sharedPref.getInt(CURRENT_SORT_ORDER, SortDialog.RECENTLY_USED);

        searchStarted = false;

        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(wl.getAdapter(this, sortOrder));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                ArrayAdapter<Workout> adapter = (ArrayAdapter) lv.getAdapter();
                WorkoutList.current = adapter.getItem(position);
                openWorkout();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_create:
                WorkoutList.current = new Workout();
                intent = new Intent(this, EditActivity.class);
                startActivityForResult(intent, CREATE_REQUEST);
                break;
            case R.id.action_sort:
                SortDialog dialog = new SortDialog();
                dialog.show(getSupportFragmentManager(), "sortDialog");
                break;
            case R.id.action_search:
                searchStarted = true;
                onSearchRequested();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void openWorkout() {
        Intent intent = new Intent(this, MainWorkoutActivity.class);
        startActivityForResult(intent, VIEW_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == VIEW_REQUEST) {
            if(resultCode == RESULT_CANCELED) {
                wl.removeWorkout(WorkoutList.current.key);
                getContentResolver().delete(SearchSuggestionProvider.URI, SearchSuggestionProvider.columnNames[0], new String[]{""+WorkoutList.current.key});
                WorkoutList.current = null;
            } else if(resultCode == RESULT_OK) {
                ContentValues values = new ContentValues();
                values.put(SearchSuggestionProvider.columnNames[1], WorkoutList.current.name);
                values.put(SearchSuggestionProvider.columnNames[0], WorkoutList.current.key);
                getContentResolver().update(SearchSuggestionProvider.URI, values, null, null);
            }
            ListView lv = (ListView)findViewById(R.id.listView);
            lv.setAdapter(wl.getAdapter(this, sortOrder));
        } else if(requestCode == CREATE_REQUEST) {
            if(resultCode == RESULT_OK) {
                wl.addNewWorkout(WorkoutList.current);

                ContentValues values = new ContentValues();
                values.put(SearchSuggestionProvider.columnNames[0], WorkoutList.current.key);
                values.put(SearchSuggestionProvider.columnNames[1], WorkoutList.current.name);
                getContentResolver().insert(SearchSuggestionProvider.URI, values);

                ListView lv = (ListView)findViewById(R.id.listView);
                lv.setAdapter(wl.getAdapter(this, sortOrder));

                Intent intent = new Intent(this, MainWorkoutActivity.class);
                startActivityForResult(intent, VIEW_REQUEST);
            } else if(resultCode == RESULT_CANCELED) {
                WorkoutList.current = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(searchStarted) {
            getContentResolver().update(SearchSuggestionProvider.URI, null, null, null);
            ListView lv = (ListView)findViewById(R.id.listView);
            lv.setAdapter(wl.getAdapter(this, sortOrder));
            searchStarted = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(CURRENT_SORT_ORDER, sortOrder);
            editor.commit();
            PrintWriter outFile = new PrintWriter(new File(getFilesDir(),"list.txt"));
            wl.save(outFile);
            outFile.close();
            for(String filename : wl.getTrashFiles()) {
                (new File(getFilesDir(), filename)).delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSortDialogClick(int which) {
        sortOrder = which;
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(wl.getAdapter(this, sortOrder));
    }
}
