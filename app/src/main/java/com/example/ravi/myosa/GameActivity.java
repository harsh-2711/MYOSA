package com.example.ravi.myosa;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.example.ravi.myosa.R;

import java.io.File;

public class GameActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        videoView = (VideoView) findViewById(R.id.videoView);
        File file = new File(Environment.getExternalStorageDirectory(), "recordmaster/simple.mp4");
        videoView.setVideoPath(file.getAbsolutePath());
        videoView.start();
    }
}
