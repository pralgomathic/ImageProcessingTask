package com.bjit.interview.imageprocessingtask.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bjit.interview.imageprocessingtask.R;
import com.bjit.interview.imageprocessingtask.Utilities.Constants;
import com.bjit.interview.imageprocessingtask.Utilities.PathUtils;
import com.bjit.interview.imageprocessingtask.Utilities.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;


public class AddTextActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddTextActivity";
    private static final int SELECT_VIDEO = 200;
    private static final int FILE_SELECT_CODE = 1030;
    private ProgressDialog progressDialog;
    private Button selectTextButton, selectVideoButton, addTextToVideButton;
    private EditText textInputEditText;
    private VideoView addTextVideoView;
    private String selectedVideoPath = "";
    private String selectedFontPath = "";
    private String outputVideoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        initUI();

    }

    private void initUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Text to Video");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        addTextToVideButton = (Button) findViewById(R.id.addTextToVideButton);
        addTextToVideButton.setOnClickListener(this);
        selectTextButton = (Button) findViewById(R.id.selectTextButton);
        selectTextButton.setOnClickListener(this);
        selectVideoButton = (Button) findViewById(R.id.selectVideoButton);
        selectVideoButton.setOnClickListener(this);
        textInputEditText = (EditText)findViewById(R.id.textInputEditText);
        addTextVideoView = (VideoView)findViewById(R.id.addTextVideoView);
        addTextVideoView.setVisibility(View.INVISIBLE);
    }

    private void addTextToVideo() {
        if (!textInputEditText.getText().toString().isEmpty() && !selectedFontPath.isEmpty() && !selectedVideoPath.isEmpty()) {

            runAddTextCommand();
        } else {
            Toast.makeText(this, "Please add Text, Font(TTF) and Video...", Toast.LENGTH_LONG).show();
        }

    }

    private void runAddTextCommand() {
        outputVideoPath = Utils.getExternalStoragePath()+"/"+Utils.getOutputFileName("add_text",Utils.getFileExtension(selectedVideoPath));
        String position = "x=(w-text_w)/2: y=(h-text_h)/3";
        String border =  ": box=1: boxcolor=black@0.5:boxborderw=5";
        int size = 36;
        String text = textInputEditText.getText().toString();
        String color = "white";
        String[] command = new String[]{"-y","-i", selectedVideoPath, "-vf", "drawtext=fontfile=" + selectedFontPath + ":text=" + text + ": fontcolor=" + color + ": fontsize=" + size + border + ": " + position, "-c:v", "libx264", "-c:a", "copy", "-movflags", "+faststart",outputVideoPath};
        if (command.length != 0) {
            runFFmpegCommand(command);
        } else {
            Toast.makeText(this, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
        }
    }
    private void runFFmpegCommand(String[] command){
        FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {

            @Override
            public void onStart() {
                progressDialog.setMessage("Processing...");
                progressDialog.show();
            }

            @Override
            public void onSuccess(String message) {
                progressDialog.setMessage("Processing\n" + message);
                Log.d("FFMPEG NEW",message);
            }

            @Override
            public void onProgress(String message) {
                progressDialog.setMessage("Processing..Please Wait..");
                Log.d("FFMPEG NEW",message);
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                if(!outputVideoPath.isEmpty()) {
                    addTextVideoView.setVisibility(View.VISIBLE);
                    playVideo(outputVideoPath);
                }
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addTextToVideButton:
                addTextToVideo();
                break;
            case R.id.selectTextButton:
                selectFontFile();
                break;
            case R.id.selectVideoButton:
                selectVideo();
                break;
        }
    }

    private void selectFontFile() {
        if (Utils.checkAndRequestPermissions(getApplicationContext(), this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(
                    Intent.createChooser(intent, "Select Font File (TTF)"),
                    FILE_SELECT_CODE);
        } else {
            Utils.checkAndRequestPermissions(getApplicationContext(), this);
        }


    }

    private void selectVideo() {
        if (Utils.checkAndRequestPermissions(getApplicationContext(), this)) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_VIDEO);
        } else {
            Utils.checkAndRequestPermissions(getApplicationContext(), this);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FILE_SELECT_CODE) {
                Uri uri = data.getData();
                try {
                    selectedFontPath = PathUtils.getPath(getApplicationContext(),uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                if (selectedFontPath == null) {
                    Log.d(TAG, "selected file path = null!");

                } else {

                    Toast.makeText(this, selectedFontPath, Toast.LENGTH_LONG).show();
                    Log.d(TAG, selectedFontPath);

                }
            } else if (requestCode == SELECT_VIDEO) {
                selectedVideoPath = getVideoPath(data.getData());
                if (selectedVideoPath == null) {
                    Log.d(TAG, "selected video path = null!");

                } else {
                    Toast.makeText(this, selectedVideoPath, Toast.LENGTH_LONG).show();
                    Log.d(TAG, selectedVideoPath);

                }
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case Constants.REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            ) {
                        Log.d(TAG, "read & write permission granted");
                        Toast.makeText(this, "read & write permission granted", Toast.LENGTH_LONG).show();

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                    }
                }
            }
        }

    }
    private String getVideoPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }
    public void playVideo(String videoPath) {
        MediaController mediaController = new MediaController(this);
        addTextVideoView.setMediaController(mediaController);
        addTextVideoView.setVideoPath(videoPath);
        addTextVideoView.start();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
