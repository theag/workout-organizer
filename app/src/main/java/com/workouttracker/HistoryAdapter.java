package com.workouttracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.workouttracker.data.Exercise;
import com.workouttracker.data.ExerciseHistory;

import java.text.SimpleDateFormat;

/**
 * Created by nbp184 on 2016/01/26.
 */
public class HistoryAdapter extends BaseAdapter implements ListAdapter {

    private Exercise ex;
    private Context context;

    public HistoryAdapter(Context context, Exercise ex) {
        this.ex = ex;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ex.historyCount();
    }

    @Override
    public Object getItem(int position) {
        return ex.getHistory(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.exercise_history_list_item, null);
        }

        ExerciseHistory eh = ex.getHistory(position);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        TextView tv = (TextView)view.findViewById(R.id.history_date);
        tv.setText(sdf.format(eh.date.getTime()).toString());

        tv = (TextView)view.findViewById(R.id.history_weight);
        tv.setText(eh.weight +" lbs");

        tv = (TextView)view.findViewById(R.id.history_repetitions);
        tv.setText(eh.repetitions +" reps");

        if(eh.duringWorkout) {
            view.setBackgroundResource(R.color.exercise_history_during_workout);
        }

        return view;
    }
}
