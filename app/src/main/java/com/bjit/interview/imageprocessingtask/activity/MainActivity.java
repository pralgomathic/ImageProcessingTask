package com.bjit.interview.imageprocessingtask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.bjit.interview.imageprocessingtask.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton imageCropButton, videoMergeButton, addAudioButton, addTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        }

    }
}
