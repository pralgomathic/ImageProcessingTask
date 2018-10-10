package com.jumman.pralgomathic.imageprocessingtask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.jumman.pralgomathic.imageprocessingtask.R;
import com.jumman.pralgomathic.imageprocessingtask.Utilities.Constants;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private ImageButton imageCropButton, videoMergeButton, addAudioButton, addTextButton, addAnimationToVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FFmpegSupportCheck();
        initUI();
    }

    private void initUI(){
        imageCropButton = (ImageButton)findViewById(R.id.imageCropButton);
        imageCropButton.setOnClickListener(this);
        videoMergeButton = (ImageButton)findViewById(R.id.videoMergeButton);
        videoMergeButton.setOnClickListener(this);
        addAudioButton = (ImageButton)findViewById(R.id.addAudioButton);
        addAudioButton.setOnClickListener(this);
        addTextButton = (ImageButton)findViewById(R.id.addTextToVideButton);
        addTextButton.setOnClickListener(this);
        addAnimationToVideoButton = (ImageButton)findViewById(R.id.addAnimationToVideoImageButton);
        addAnimationToVideoButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.imageCropButton:
                Intent imageCropIntent = new Intent(this, ImageCroppingActivity.class);
                startActivity(imageCropIntent);
                break;
            case R.id.videoMergeButton:
                Intent videoMergeIntent = new Intent(this, VideoMergeActivity.class);
                startActivity(videoMergeIntent);
                break;
            case R.id.addAudioButton:
                Intent addAudioIntent = new Intent(this, AddAudioActivity.class);
                startActivity(addAudioIntent);
                break;
            case R.id.addTextToVideButton:
                Intent addTextIntent = new Intent(this, AddTextActivity.class);
                startActivity(addTextIntent);
                break;
            case R.id.addAnimationToVideoImageButton:
                Intent addAnimationIntent = new Intent(this, AddAnimationActivity.class);
                startActivity(addAnimationIntent);
                break;
        }

    }

    private void FFmpegSupportCheck() {
        if (FFmpeg.getInstance(this).isSupported()) {
            // ffmpeg is supported
            versionFFmpeg();

        } else {
            // ffmpeg is not supported
            Constants.errorLog(TAG, "ffmpeg not supported!");
        }
    }
    private void versionFFmpeg() {
        FFmpeg.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                Constants.debugLog(TAG, message);
            }

            @Override
            public void onProgress(String message) {
                Constants.debugLog(TAG, message);
            }
        });

    }
}
