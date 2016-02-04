package com.workouttracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.workouttracker.data.Exercise;
import com.workouttracker.data.Workout;
import com.workouttracker.data.WorkoutList;

import java.io.IOException;

public class EditExerciseActivity extends AppCompatActivity implements /*ImageUploadButtonView.ImageUploadButtonListener,*/ MyOptionPane.MyOptionPaneListener {

    private static final int IMAGE_REQUEST = 1;
    private static final String IMAGE_CLICK = "image click";
    private static final String IMAGE_CHOICE = "image choice";
    private static final int CAMERA_REQUEST = 2;

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(WorkoutList.current.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        index = extras.getInt(Workout.EXERCISE_INDEX);
        Exercise ex = WorkoutList.current.getExercise(index);

        EditText et = (EditText)findViewById(R.id.exercise_name);
        et.setText(ex.name);

        et = (EditText)findViewById(R.id.exercise_description);
        et.setText(ex.description);

        et = (EditText)findViewById(R.id.exercise_weight);
        et.setText(""+ex.weight);

        et = (EditText)findViewById(R.id.exercise_weight_jump);
        et.setText(""+ex.weightJump);

        et = (EditText)findViewById(R.id.exercise_repetitions);
        et.setText("" + ex.repetitions);

        et = (EditText)findViewById(R.id.exercise_repetitions_jump);
        et.setText(""+ex.repetitionJump);

        ImageUploadButtonView iubv = (ImageUploadButtonView)findViewById(R.id.imageUpload);
        //iubv.setOnClickListener(this);
        iubv.setImage(ex.image);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Exercise ex = WorkoutList.current.getExercise(index);
        EditText et = (EditText)findViewById(R.id.exercise_name);
        ex.name = et.getText().toString();
        et = (EditText)findViewById(R.id.exercise_description);
        ex.description = et.getText().toString();
        et = (EditText)findViewById(R.id.exercise_weight);
        ex.weight = Double.parseDouble(et.getText().toString());
        et = (EditText)findViewById(R.id.exercise_weight_jump);
        ex.weightJump = Double.parseDouble(et.getText().toString());
        et = (EditText)findViewById(R.id.exercise_repetitions);
        ex.repetitions = Integer.parseInt(et.getText().toString());
        et = (EditText)findViewById(R.id.exercise_repetitions_jump);
        ex.repetitionJump = Integer.parseInt(et.getText().toString());
        ex.makeHistory(false);

        //TODO: // FIXME: 2016/02/04
        ImageUploadButtonView iubv = (ImageUploadButtonView)findViewById(R.id.imageUpload);
        ex.image = iubv.getImage();

        setResult(RESULT_OK);
        finish();
    }

    /*@Override
    public void onTap(boolean alreadyHas) {
        if(alreadyHas) {
            MyOptionPane.showOptionDialog(getSupportFragmentManager(), IMAGE_CLICK, "Exercise Image", new String[]{"Change", "Clear"});
        } else {
            MyOptionPane.showOptionDialog(getSupportFragmentManager(), IMAGE_CHOICE, "Get image from", new String[]{"File", "Camera"});
        }
    }*/

    public void imageButtonClick(View view) {
        ImageUploadButtonView iubv = (ImageUploadButtonView)view;
        if(iubv.getImage() == null) {
            MyOptionPane.showOptionDialog(getSupportFragmentManager(), IMAGE_CHOICE, "Get image from", new String[]{"File", "Camera"});
        } else {
            MyOptionPane.showOptionDialog(getSupportFragmentManager(), IMAGE_CLICK, "Exercise Image", new String[]{"Change", "Clear"});
        }
    }

    //TODO: // FIXME: 2016/02/04
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_REQUEST:
                if(resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    ImageUploadButtonView iubv = (ImageUploadButtonView)findViewById(R.id.imageUpload);
                    try {
                        iubv.setImage(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));
                    } catch (IOException e) {
                        MyOptionPane.showMessageDialog(getSupportFragmentManager(), "FILE ERROR", "Could not find files.", "Error");
                    }
                }
                break;
            case CAMERA_REQUEST:
                if(resultCode == RESULT_OK) {
                    ImageUploadButtonView iubv = (ImageUploadButtonView)findViewById(R.id.imageUpload);
                    iubv.setImage((Bitmap)data.getExtras().get("data"));
                }
                break;
        }
    }

    private void openImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_REQUEST);
    }

    private void cameraImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onMyOptionPaneClick(String tag) {

    }

    //TODO: // FIXME: 2016/02/04
    @Override
    public void onMyOptionPaneClick(String tag, int result) {
        if(tag.compareTo(IMAGE_CLICK) == 0) {
            if(result == 0) {
                MyOptionPane.showOptionDialog(getSupportFragmentManager(), IMAGE_CHOICE, "Get image from", new String[]{"File", "Camera"});
            } else {
                ImageUploadButtonView iubv = (ImageUploadButtonView)findViewById(R.id.imageUpload);
                iubv.setImage(null);
            }
        } else if(tag.compareTo(IMAGE_CHOICE) == 0) {
            if(result == 0) {
                openImage();
            } else {
                cameraImage();
            }
        }
    }
}
