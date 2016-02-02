package com.workouttracker;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.workouttracker.data.Workout;
import com.workouttracker.data.WorkoutList;

import java.util.ArrayList;

/**
 * Created by nbp184 on 2016/01/28.
 */
public class SearchActivity extends AppCompatActivity {

    private static final int VIEW_REQUEST = 1;
    private static final int VIEW_SKIP_REQUEST = 2;
    private ArrayList<Workout> searchResults;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_search);

        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                ArrayAdapter<Workout> adapter = (ArrayAdapter) lv.getAdapter();
                WorkoutList.current = adapter.getItem(position);
                openWorkout(VIEW_REQUEST);
            }
        });

        handleIntent(getIntent());

    }

    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void openWorkout(int requestCode) {
        Intent intent = new Intent(this, MainWorkoutActivity.class);
        startActivityForResult(intent, requestCode);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            TextView tv = (TextView)findViewById(R.id.query_text);
            tv.setText("\"" +query +"\"");
            System.out.println("search");
            doSearch(query);
        } else if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            Uri data = intent.getData();
            WorkoutList.current = WorkoutList.getInstance().getWorkout(Integer.parseInt(data.getPath()));
            openWorkout(VIEW_SKIP_REQUEST);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchRequested();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void doSearch(String query) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int sortOrder = sharedPref.getInt(MainActivity.CURRENT_SORT_ORDER, SortDialog.RECENTLY_USED);
        searchResults = WorkoutList.getInstance().getSearchResults(query.toLowerCase(), sortOrder);
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<Workout>(this, android.R.layout.simple_list_item_1, searchResults));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == VIEW_REQUEST) {
            WorkoutList wl = WorkoutList.getInstance();
            if(resultCode == RESULT_CANCELED) {
                wl.removeWorkout(WorkoutList.current.key);
                searchResults.remove(WorkoutList.current);
                WorkoutList.current = null;
                ListView lv = (ListView)findViewById(R.id.listView);
                lv.setAdapter(new ArrayAdapter<Workout>(this, android.R.layout.simple_list_item_1, searchResults));
            }
        } else if(requestCode == VIEW_SKIP_REQUEST) {
            finish();
        }
    }

}
