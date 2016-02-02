package com.workouttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by nbp184 on 2016/01/28.
 */
public class SortDialog extends DialogFragment {

    public static final int RECENTLY_USED = 0;
    public static final int RECENTLY_VIEWED = 1;
    public static final int CREATION = 2;
    public static final int ALPHABETICALLY = 3;

    private static final String[] sort_array = {"by recently used", "by recently viewed", "by creation", "alphabetically"};

    public interface SortDialogListener {
        public void onSortDialogClick(int which);
    }

    private SortDialogListener listener = null;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof SortDialogListener) {
            listener = (SortDialogListener)activity;
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_sort)
                .setItems(sort_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener != null) {
                            listener.onSortDialogClick(which);
                        }
                    }
                });
        return builder.create();

    }

}
