package com.example.ravi.myosa;

import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.example.ravi.myosa.R;

import java.io.File;

public class GameActivity extends AppCompatActivity {

    Runnable runnable;
    Handler handler;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                if(videoView.isPlaying()) {
                    handler.post(runnable);
                }
                else {
                    videoView.start();
                    handler.post(runnable);
                }
            }
        };
        videoView = (VideoView) findViewById(R.id.videoView);
        File file = new File(Environment.getExternalStorageDirectory(), "main4.mp4");
        videoView.setVideoPath(file.getAbsolutePath());
        videoView.start();
        runnable.run();
    }
}
