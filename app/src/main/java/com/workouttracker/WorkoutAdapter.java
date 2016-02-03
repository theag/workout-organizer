package com.workouttracker;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.workouttracker.data.WorkoutList;

/**
 * Created by nbp184 on 2016/01/22.
 */
public class WorkoutAdapter extends BaseAdapter implements ListAdapter {

    public static final int BASE_VIEW = 0;
    public static final int DOING_VIEW = 1;
    public static final int EDIT_VIEW = 2;
    public static final String CURRENT_INDEX = "current index";

    public static final int TOP_BUTTON = -1;
    public static final int BOTTOM_BUTTON = -2;

    private Context context;
    private int viewChoice;
    private int currentExercise;

    public WorkoutAdapter(int viewChoice, Context context) {
        this.context = context;
        this.viewChoice = viewChoice;
        if(viewChoice == DOING_VIEW) {
            currentExercise = 0;
        } else {
            currentExercise = -1;
        }
    }

    @Override
    public int getCount() {
        if(viewChoice == EDIT_VIEW) {
            return WorkoutList.current.exerciseCount() + 2;
        } else {
            return WorkoutList.current.exerciseCount();
        }
    }

    @Override
    public Object getItem(int position) {
        if(viewChoice == EDIT_VIEW) {
            if(position == 0 || position == WorkoutList.current.exerciseCount()+1) {
                return null;
            } else {
                return WorkoutList.current.getExercise(position-1);
            }
        } else {
            return WorkoutList.current.getExercise(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        switch(viewChoice) {
            case BASE_VIEW:
                if(view == null) {
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.exercise_list_item, null);
                }
                break;
            case DOING_VIEW:
                if(view == null) {
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.exercise_list_item_workout, null);
                }
                break;
            case EDIT_VIEW:
                if(position == 0 || position == WorkoutList.current.exerciseCount()+1) {
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.exercise_list_item_add, null);
                } else {
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.exercise_list_item_edit, null);
                }
                break;
        }

        if(viewChoice == EDIT_VIEW && position != 0 && position != WorkoutList.current.exerciseCount()+1) {
            TextView name = (TextView) view.findViewById(R.id.list_item_name);
            name.setText(WorkoutList.current.getExercise(position - 1).name);

            TextView tv = (TextView) view.findViewById(R.id.list_item_weight);
            tv.setText("" + WorkoutList.current.getExercise(position - 1).weight);

            tv = (TextView) view.findViewById(R.id.list_item_repetitions);
            tv.setText("" + WorkoutList.current.getExercise(position - 1).repetitions);

            tv = (TextView) view.findViewById(R.id.list_item_position);
            tv.setText("" + (position - 1));

            if (position == 1) {
                ImageButton ib = (ImageButton) view.findViewById(R.id.list_item_move_up_btn);
                ib.setEnabled(false);
            } else if (position == WorkoutList.current.exerciseCount()) {
                ImageButton ib = (ImageButton) view.findViewById(R.id.list_item_move_down_btn);
                ib.setEnabled(false);
            }
        } else if(viewChoice == EDIT_VIEW) {
            TextView tv = (TextView) view.findViewById(R.id.list_item_position);
            if(position == 0) {
                tv.setText("" + TOP_BUTTON);
            } else {
                tv.setText("" + BOTTOM_BUTTON);
            }
        } else if(viewChoice == BASE_VIEW) {
            TextView name = (TextView) view.findViewById(R.id.list_item_name);
            name.setText(WorkoutList.current.getExercise(position).name);

            TextView weight = (TextView) view.findViewById(R.id.list_item_weight);
            weight.setText(WorkoutList.current.getExercise(position).weight+" " +context.getString(R.string.text_weight_unit));

            TextView reps = (TextView) view.findViewById(R.id.list_item_repetitions);
            reps.setText(WorkoutList.current.getExercise(position).repetitions+" " +context.getString(R.string.text_repetitions_unit));

            TextView pos = (TextView) view.findViewById(R.id.list_item_position);
            pos.setText("" + position);
        } else if(viewChoice == DOING_VIEW) {
            TextView name = (TextView) view.findViewById(R.id.list_item_name);
            name.setText(WorkoutList.current.getExercise(position).name);

            TextView weight = (TextView) view.findViewById(R.id.list_item_weight);
            weight.setText(""+WorkoutList.current.getExercise(position).weight);

            TextView reps = (TextView) view.findViewById(R.id.list_item_repetitions);
            reps.setText(""+WorkoutList.current.getExercise(position).repetitions);

            TextView pos = (TextView) view.findViewById(R.id.list_item_position);
            pos.setText("" + position);

            if(position == currentExercise) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setBackgroundColor(context.getColor(R.color.exercise_history_during_workout));
                } else {
                    view.setBackgroundColor(context.getResources().getColor(R.color.exercise_history_during_workout));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setBackgroundColor(context.getColor(android.R.color.transparent));
                } else {
                    view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                }
            }
        }

        return view;
    }

    public int getCurrentExercise() {
        return currentExercise;
    }

    public void setCurrentExercise(int currentExercise) {
        this.currentExercise = currentExercise;
        this.notifyDataSetInvalidated();
    }

}
