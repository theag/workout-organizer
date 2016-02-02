package com.workouttracker;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.workouttracker.data.WorkoutList;

import java.util.ArrayList;

/**
 * Created by nbp184 on 2016/01/28.
 */
public class SearchSuggestionProvider extends ContentProvider {

    public static final String[] columnNames = {"_ID", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA};
    public static final Uri URI = Uri.parse("content://com.workouttracker.SearchSuggestionProvider");

    private ArrayList<Object[]> rows;

    @Override
    public boolean onCreate() {
        rows = WorkoutList.getInstance(getContext()).getSearchData(SortDialog.ALPHABETICALLY);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor mc = new MatrixCursor(columnNames);
        String query = selectionArgs[0].toLowerCase();
        String name;
        for(Object[] row : rows) {
            name = ((String)row[1]).toLowerCase();
            if(name.startsWith(query)) {
                mc.addRow(row);
            }
        }
        for(Object[] row : rows) {
            name = ((String)row[1]).toLowerCase();
            if(!name.startsWith(query) && name.contains(query)) {
                mc.addRow(row);
            }
        }
        return mc;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Object[] row = new Object[3];
        row[0] = values.get(columnNames[0]);
        row[1] = values.get(columnNames[1]);
        row[2] = row[0];
        if(((String)row[1]).compareTo((String)rows.get(0)[1]) < 0) {
            rows.add(0, row);
        } else {
            int i = 0;
            for (; i < rows.size(); i++) {
                if (((String) row[1]).compareTo((String) rows.get(i)[1]) > 0) {
                    break;
                }
            }
            rows.add(i + 1, row);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Integer key = Integer.parseInt(selectionArgs[0]);
        int i = 0;
        for(; i < rows.size(); i++) {
            if(rows.get(i)[0] == key) {
                break;
            }
        }
        if(i < rows.size()) {
            rows.remove(i);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values != null) {
            Integer key = values.getAsInteger(columnNames[0]);
            for (Object[] row : rows) {
                if (row[0] == key) {
                    row[1] = values.get(columnNames[1]);
                    break;
                }
            }
        } else {
            rows = WorkoutList.getInstance(getContext()).getSearchData(SortDialog.ALPHABETICALLY);
        }
        return 0;
    }
}
