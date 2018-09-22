package com.bjit.interview.imageprocessingtask.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bjit.interview.imageprocessingtask.R;
import com.bjit.interview.imageprocessingtask.Utilities.Constants;
import com.bjit.interview.imageprocessingtask.Utilities.Utils;

import java.util.HashMap;
import java.util.Map;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

public class AddAnimationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "AddAnimationActivity";
    private static final int SELECT_VIDEO = 200;
    private String selectedVideoPath = "";
    private Uri selectedVideoUri;
    private Button selectVideoButton, addAnimationToVideoButton;
    private ProgressDialog progressDialog;
    private VideoView addAnimationVideoView;
    private String videoOutputDestination = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animation);
        initUI();
    }

    private void initUI(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Animation to Video");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        selectVideoButton = (Button)findViewById(R.id.selectVideoButton);
        selectVideoButton.setOnClickListener(this);
        addAnimationToVideoButton = (Button)findViewById(R.id.addAnimationToVideoButton);
        addAnimationToVideoButton.setOnClickListener(this);
        addAnimationVideoView = (VideoView)findViewById(R.id.addAnimationVideoView);
        addAnimationVideoView.setVisibility(View.INVISIBLE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selectVideoButton:
                selectVideo();
                break;
            case R.id.addAnimationToVideoButton:
                addAnimationToVideo();
                break;

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

    private void addAnimationToVideo() {
        if (!selectedVideoPath.isEmpty() && getMediaDuration() > 5) {

            runAddAnimationCommand();
        } else {
            Toast.makeText(this, "Please select a Video of at least 5 sec...", Toast.LENGTH_LONG).show();
        }
    }

    private void runAddAnimationCommand() {
        String[] command = {"-y", "-i", selectedVideoPath, "-acodec", "copy", "-vf", "fade=t=in:st=0:d=3,fade=t=out:st=" + String.valueOf(getMediaDuration() - 3) + ":d=3", videoOutputDestination};
        videoOutputDestination = Utils.getExternalStoragePath() + "/" + Utils.getOutputFileName("add_animation", Utils.getFileExtension(selectedVideoPath));
        //String[] command = new String[]{"-y", "-i", selectedVideoPath, "-i", selectedAudioPath, "-c", "copy", "-map", "0:0", "-map", "1:0", videoOutputDestination};

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
//                if (!videoOutputDestination.isEmpty()) {
//
//                    addAnimationVideoView.setVisibility(View.VISIBLE);
//                    playVideo(videoOutputDestination);
//                }
            }
        });

    }

    private void playVideo(String videoPath) {
        MediaController mediaController = new MediaController(this);
        addAnimationVideoView.setMediaController(mediaController);
        addAnimationVideoView.setVideoPath(videoPath);
        addAnimationVideoView.start();

    }
    private long getMediaDuration(){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getApplicationContext(), selectedVideoUri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInSec = Long.parseLong(time)/1000;
        Constants.debugLog(TAG, "video duration : "+timeInSec);
        retriever.release();
        return timeInSec;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                selectedVideoUri = data.getData();
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
}
