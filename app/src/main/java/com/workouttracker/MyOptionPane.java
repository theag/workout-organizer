package com.workouttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by nbp184 on 2016/01/25.
 */
public class MyOptionPane extends DialogFragment {

    public interface MyOptionPaneListener {
        public void onMyOptionPaneClick(String tag);
        public void onMyOptionPaneClick(String tag, int result);
    }

    public static final int YES_OPTION = 0;
    public static final int NO_OPTION = 1;

    private static final String MESSAGE_KEY = "message";
    private static final String TITLE_KEY = "title";
    private static final String TYPE_KEY = "type";
    private static final String OPTIONS_KEY = "options";

    private static final int MESSAGE_DIALOG = 0;
    private static final int CONFIRM_DIALOG = 1;
    private static final int OPTION_DIALOG = 2;

    public static void showMessageDialog(FragmentManager fragmentManager, String tag, String message) {
        DialogFragment frag = new MyOptionPane();
        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, MESSAGE_DIALOG);
        args.putCharSequence(MESSAGE_KEY, message);
        frag.setArguments(args);
        frag.show(fragmentManager, tag);
    }

    public static void showMessageDialog(FragmentManager fragmentManager, String tag, String message, String title) {
        DialogFragment frag = new MyOptionPane();
        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, MESSAGE_DIALOG);
        args.putCharSequence(MESSAGE_KEY, message);
        args.putCharSequence(TITLE_KEY, title);
        frag.setArguments(args);
        frag.show(fragmentManager, tag);
    }

    public static void showConfirmDialog(FragmentManager fragmentManager, String tag, String message) {
        DialogFragment frag = new MyOptionPane();
        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, CONFIRM_DIALOG);
        args.putCharSequence(MESSAGE_KEY, message);
        frag.setArguments(args);
        frag.show(fragmentManager, tag);
    }

    public static void showConfirmDialog(FragmentManager fragmentManager, String tag, String message, String title) {
        DialogFragment frag = new MyOptionPane();
        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, CONFIRM_DIALOG);
        args.putCharSequence(MESSAGE_KEY, message);
        args.putCharSequence(TITLE_KEY, title);
        frag.setArguments(args);
        frag.show(fragmentManager, tag);
    }

    public static void showOptionDialog(FragmentManager fragmentManager, String tag, String title, String[] options) {
        DialogFragment frag = new MyOptionPane();
        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, OPTION_DIALOG);
        args.putCharSequence(TITLE_KEY, title);
        args.putStringArray(OPTIONS_KEY, options);
        frag.setArguments(args);
        frag.show(fragmentManager, tag);
    }

    private MyOptionPaneListener listener;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof  MyOptionPaneListener) {
            listener = (MyOptionPaneListener)activity;
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();
        switch(args.getInt(TYPE_KEY)) {
            case MESSAGE_DIALOG:
                builder.setMessage(args.getCharSequence(MESSAGE_KEY))
                    .setTitle(args.getCharSequence(TITLE_KEY, "Message"))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onMyOptionPaneClick(getTag());
                        }
                    });
                break;
            case CONFIRM_DIALOG:
                builder.setMessage(args.getCharSequence(MESSAGE_KEY))
                        .setTitle(args.getCharSequence(TITLE_KEY, "Confirm"))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onMyOptionPaneClick(getTag(), YES_OPTION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onMyOptionPaneClick(getTag(), NO_OPTION);
                            }
                        });
                break;
            case OPTION_DIALOG:
                builder.setTitle(args.getCharSequence(TITLE_KEY, "Options"))
                        .setItems(args.getStringArray(OPTIONS_KEY), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onMyOptionPaneClick(getTag(), which);
                            }
                        });
                break;
        }

        return builder.create();
    }

}
