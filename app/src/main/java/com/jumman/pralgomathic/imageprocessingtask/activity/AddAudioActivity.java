package com.jumman.pralgomathic.imageprocessingtask.activity;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.jumman.pralgomathic.imageprocessingtask.R;
import com.jumman.pralgomathic.imageprocessingtask.Utilities.Constants;
import com.jumman.pralgomathic.imageprocessingtask.Utilities.Utils;

import java.util.HashMap;
import java.util.Map;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;


public class AddAudioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddAudioActivity";
    private static final int SELECT_AUDIO = 100;
    private static final int SELECT_VIDEO = 200;
    private String selectedAudioPath = "";
    private String selectedVideoPath = "";
    private Button selectAudioButton, selectVideoButton, addAudioToVideoButton;
    private ProgressDialog progressDialog;
    private VideoView addAudioVideoView;
    private String videoOutputDestination = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        initUI();
    }

    private void initUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Audio to Video");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        selectAudioButton = (Button) findViewById(R.id.selectAudioButton);
        selectAudioButton.setOnClickListener(this);
        selectVideoButton = (Button) findViewById(R.id.selectVideoButton);
        selectVideoButton.setOnClickListener(this);
        addAudioToVideoButton = (Button) findViewById(R.id.addAudioToVideoButton);
        addAudioToVideoButton.setOnClickListener(this);
        addAudioVideoView = (VideoView) findViewById(R.id.addAudioVideoView);
        addAudioVideoView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectAudioButton:
                selectAudio();
                break;
            case R.id.selectVideoButton:
                selectVideo();
                break;
            case R.id.addAudioToVideoButton:
                addAudioToVideo();
                break;
        }
    }

    private void selectAudio() {
        if (Utils.checkAndRequestPermissions(getApplicationContext(), this)) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_AUDIO);
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

    private void addAudioToVideo() {
        if (!selectedAudioPath.isEmpty() && !selectedVideoPath.isEmpty()) {

            runAddAudioCommand();
        } else {
            Toast.makeText(this, "Please select Audio and Video...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_AUDIO) {
                selectedAudioPath = getAudioPath(data.getData());
                if (selectedAudioPath == null) {
                    Constants.errorLog(TAG, "selected audio path = null!");

                } else {
                    Toast.makeText(this, selectedAudioPath, Toast.LENGTH_LONG).show();
                    Constants.debugLog(TAG, selectedAudioPath);

                }
            } else if (requestCode == SELECT_VIDEO) {
                selectedVideoPath = getVideoPath(data.getData());
                if (selectedVideoPath == null) {
                    Constants.errorLog(TAG, "selected video path = null!");

                } else {
                    Toast.makeText(this, selectedVideoPath, Toast.LENGTH_LONG).show();
                    Constants.debugLog(TAG, selectedVideoPath);

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
                        Constants.debugLog(TAG, "read & write permission granted");
                        Toast.makeText(this, "read & write permission granted", Toast.LENGTH_LONG).show();

                    } else {
                        Constants.errorLog(TAG, "Some permissions are not granted ask again ");
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

    public String getAudioPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    private void runAddAudioCommand() {
        //String cmd = "ffmpeg -i Input_Video.mp4 -i Added-Audio-Track.mp3 -c copy -map 0:0 -map 1:0 Output.mp4";
        videoOutputDestination = Utils.getExternalStoragePath() + "/" + Utils.getOutputFileName("add_audio", Utils.getFileExtension(selectedVideoPath));
        String[] command = new String[]{"-y", "-i", selectedVideoPath, "-i", selectedAudioPath, "-c", "copy", "-map", "0:0", "-map", "1:0", videoOutputDestination};

        if (command.length != 0) {
            runFFmpegCommand(command);
        } else {
            Toast.makeText(this, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
        }
    }

    private void runFFmpegCommand(String[] command) {
        FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {

            @Override
            public void onStart() {
                progressDialog.setMessage("Processing...");
                progressDialog.show();
            }

            @Override
            public void onSuccess(String message) {
                progressDialog.setMessage("Processing\n" + message);
                Constants.debugLog(TAG, message);
            }

            @Override
            public void onProgress(String message) {
                progressDialog.setMessage("Processing..Please Wait..");
                Constants.debugLog(TAG, message);
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                if (!videoOutputDestination.isEmpty()) {

                    addAudioVideoView.setVisibility(View.VISIBLE);
                    playVideo(videoOutputDestination);
                }
            }
        });

    }

    public void playVideo(String videoPath) {
        MediaController mediaController = new MediaController(this);
        addAudioVideoView.setMediaController(mediaController);
        addAudioVideoView.setVideoPath(videoPath);
        addAudioVideoView.start();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
