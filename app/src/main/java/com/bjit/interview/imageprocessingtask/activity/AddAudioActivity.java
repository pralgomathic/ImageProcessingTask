package com.bjit.interview.imageprocessingtask.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bjit.interview.imageprocessingtask.R;
import com.bjit.interview.imageprocessingtask.Utilities.Utils;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;


public class AddAudioActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "AddAudioActivity";
    private static final int SELECT_AUDIO = 100;
    private static final int SELECT_VIDEO = 200;
    private String selectedAudioPath = "";
    private String selectedVideoPath = "";
    private Button selectAudioButton, selectVideoButton, addAudioToVideoButton;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        initUI();
    }
    private void initUI(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        selectAudioButton = (Button)findViewById(R.id.selectAudioButton);
        selectAudioButton.setOnClickListener(this);
        selectVideoButton = (Button)findViewById(R.id.selectVideoButton);
        selectVideoButton.setOnClickListener(this);
        addAudioToVideoButton = (Button)findViewById(R.id.addAudioToVideoButton);
        addAudioToVideoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
    private void selectAudio(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_AUDIO);
    }
    private void selectVideo(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_VIDEO);
    }
    private void addAudioToVideo(){
        if(!selectedAudioPath.isEmpty() && !selectedVideoPath.isEmpty()){

            runAddAudioCommand();
        }else {
            Toast.makeText(this,"Please select Audio and Video...",Toast.LENGTH_LONG).show();
        }
    }
    @ Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_AUDIO) {
                selectedAudioPath = getAudioPath(data.getData());
                if(selectedAudioPath == null) {
                    Log.d( TAG,"selected audio path = null!");

                } else {
                    Toast.makeText(this,selectedAudioPath,Toast.LENGTH_LONG).show();
                    Log.d(TAG, selectedAudioPath);

                }
            }else if (requestCode == SELECT_VIDEO) {
                selectedVideoPath = getVideoPath(data.getData());
                if(selectedVideoPath == null) {
                    Log.d( TAG,"selected video path = null!");

                } else {
                    Toast.makeText(this,selectedVideoPath,Toast.LENGTH_LONG).show();
                    Log.d(TAG, selectedVideoPath);

                }
            }
        }

    }
    private String getVideoPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
    public String getAudioPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
    private void runAddAudioCommand(){
        String cmd = "ffmpeg -i Input_Video.mp4 -i Added-Audio-Track.mp3 -c copy -map 0:0 -map 1:0 Output.mp4";
        String outputPath = Utils.getExternalStoragePath()+"/"+Utils.getOutputFileName("add_audio",Utils.getFileExtension(selectedVideoPath));
        String[] command = new String[]{"-y", "-i", selectedVideoPath, "-i", selectedAudioPath,"-c","copy","-map","0:0","-map","1:0",outputPath};

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
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
