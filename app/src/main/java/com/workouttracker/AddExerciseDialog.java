package com.workouttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by nbp184 on 2016/01/26.
 */
public class AddExerciseDialog extends DialogFragment {

    public interface AddExerciseDialogListener {
        public void onAddExerciseDialogClick(Bundle data);
    }

    public static final String WHICH = "which";
    public static final String NAME = "name";
    public static final String WEIGHT = "weight";
    public static final String WEIGHT_JUMP = "weight jump";
    public static final String REPETITIONS = "repetitions";
    public static final String REPETITIONS_JUMP = "repetitions jump";
    public static final String ERROR = "error";
    public static final String ADD_AT_START = "add at start";

    public static final int POSITIVE_BUTTON = 1;
    public static final int NEUTRAL_BUTTON = 0;
    public static final int NEGATIVE_BUTTON = -1;

    public static void showDialog(FragmentManager fragmentManager, String tag, boolean addAtStart) {
        AddExerciseDialog frag = new AddExerciseDialog();
        frag.addAtStart = addAtStart;
        frag.show(fragmentManager, tag);
    }

    public static void showDialog(FragmentManager fragmentManager, String tag, Bundle data) {
        AddExerciseDialog frag = new AddExerciseDialog();
        frag.setArguments(data);
        frag.show(fragmentManager, tag);
    }

    private AddExerciseDialogListener listener;
    private boolean addAtStart;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof AddExerciseDialogListener) {
            listener = (AddExerciseDialogListener)activity;
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflated = inflater.inflate(R.layout.dialog_add_exercise, null);
        Bundle data = getArguments();
        if(data != null) {
            addAtStart = data.getBoolean(ADD_AT_START);
            EditText et = (EditText)inflated.findViewById(R.id.exercise_name);
            et.setText(data.getString(NAME));
            if(data.containsKey(WEIGHT)) {
                et = (EditText)inflated.findViewById(R.id.exercise_weight);
                et.setText(""+data.getDouble(WEIGHT));
            }
            if(data.containsKey(WEIGHT_JUMP)) {
                et = (EditText)inflated.findViewById(R.id.exercise_weight_jump);
                et.setText(""+data.getDouble(WEIGHT_JUMP));
            }
            if(data.containsKey(REPETITIONS)) {
                et = (EditText)inflated.findViewById(R.id.exercise_repetitions);
                et.setText(""+data.getInt(REPETITIONS));
            }
            if(data.containsKey(REPETITIONS_JUMP)) {
                et = (EditText)inflated.findViewById(R.id.exercise_repetitions_jump);
                et.setText(""+data.getInt(REPETITIONS_JUMP));
            }
        }
        builder.setView(inflated)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                bundle.putInt(WHICH, POSITIVE_BUTTON);
                bundle.putBoolean(ADD_AT_START, addAtStart);
                Dialog aed = AddExerciseDialog.this.getDialog();
                EditText et = (EditText) aed.findViewById(R.id.exercise_name);
                bundle.putString(NAME, et.getText().toString());
                boolean alreadyError = false;
                try {
                    et = (EditText) aed.findViewById(R.id.exercise_weight);
                    bundle.putDouble(WEIGHT, Double.parseDouble(et.getText().toString()));
                } catch (NumberFormatException ex) {
                    bundle.putString(ERROR, "Weight must be a number.");
                    alreadyError = true;
                }
                try {
                    et = (EditText) aed.findViewById(R.id.exercise_weight_jump);
                    bundle.putDouble(WEIGHT_JUMP, Double.parseDouble(et.getText().toString()));
                } catch (NumberFormatException ex) {
                    if(!alreadyError) {
                        bundle.putString(ERROR, "Weight jump must be a number.");
                        alreadyError = true;
                    }
                }
                try {
                    et = (EditText) aed.findViewById(R.id.exercise_repetitions);
                    bundle.putInt(REPETITIONS, Integer.parseInt(et.getText().toString()));
                } catch (NumberFormatException ex) {
                    if(!alreadyError) {
                        bundle.putString(ERROR, "Repetitions must be an integer.");
                        alreadyError = true;
                    }
                }
                try {
                    et = (EditText) aed.findViewById(R.id.exercise_repetitions_jump);
                    bundle.putInt(REPETITIONS_JUMP, Integer.parseInt(et.getText().toString()));
                } catch (NumberFormatException ex) {
                    if(!alreadyError) {
                        bundle.putString(ERROR, "Repetition jump must be an integer.");
                        alreadyError = true;
                    }
                }
                listener.onAddExerciseDialogClick(bundle);
            }
        })
            .setNeutralButton("Next", new DialogInterface.OnClickListener() {
                @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                bundle.putInt(WHICH, NEUTRAL_BUTTON);
                bundle.putBoolean(ADD_AT_START, addAtStart);
                Dialog aed = AddExerciseDialog.this.getDialog();
                EditText et = (EditText) aed.findViewById(R.id.exercise_name);
                bundle.putString(NAME, et.getText().toString());
                boolean alreadyError = false;
                try {
                    et = (EditText) aed.findViewById(R.id.exercise_weight);
                    bundle.putDouble(WEIGHT, Double.parseDouble(et.getText().toString()));
                } catch (NumberFormatException ex) {
                    bundle.putString(ERROR, "Weight must be a number.");
                    alreadyError = true;
                }
                try {
                    et = (EditText) aed.findViewById(R.id.exercise_weight_jump);
                    bundle.putDouble(WEIGHT_JUMP, Double.parseDouble(et.getText().toString()));
                } catch (NumberFormatException ex) {
                    if(!alreadyError) {
                        bundle.putString(ERROR, "Weight jump must be a number.");
                        alreadyError = true;
                    }
                }
                try {
                    et = (EditText) aed.findViewById(R.id.exercise_repetitions);
                    bundle.putInt(REPETITIONS, Integer.parseInt(et.getText().toString()));
                } catch (NumberFormatException ex) {
                    if(!alreadyError) {
                        bundle.putString(ERROR, "Repetitions must be an integer.");
                        alreadyError = true;
                    }
                }
                try {
                    et = (EditText) aed.findViewById(R.id.exercise_repetitions_jump);
                    bundle.putInt(REPETITIONS_JUMP, Integer.parseInt(et.getText().toString()));
                } catch (NumberFormatException ex) {
                    if(!alreadyError) {
                        bundle.putString(ERROR, "Repetition jump must be an integer.");
                        alreadyError = true;
                    }
                }
                listener.onAddExerciseDialogClick(bundle);
            }
        })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(WHICH, NEGATIVE_BUTTON);
                    listener.onAddExerciseDialogClick(bundle);
                }
        });
        return builder.create();
    }

}
