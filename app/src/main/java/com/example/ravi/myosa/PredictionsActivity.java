package com.example.ravi.myosa;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ravi.myosa.R;

import org.w3c.dom.Text;

public class PredictionsActivity extends AppCompatActivity {

    Button startPrediction, stopPrediction;
    boolean isRunning = false;

    Runnable runnable;
    Handler handler;

    float predictionData[];

    ProgressBar progressBar;
    TextView predictingText;

    TextView predictionHeading, temperatureText, HumidityText, GyroYText, GyroZText, AccYText, AccZText,
            fearLevelText, fearPercentage, accuracyText, whatToDo, predictedScene;
    ProgressBar fearLevel;

    boolean toFetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictions);

        // 12 - Temp C
        // 14 - Humidity
        // 23 - Gyro Y-axis
        // 24 - Gyro Z- axis
        // 26 - Acc Y-axis
        // 27 - Acc Z-axis

        startPrediction = (Button) findViewById(R.id.startPrediction);
        stopPrediction = (Button) findViewById(R.id.stopPrediction);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        predictingText = (TextView) findViewById(R.id.predictingText);

        predictionHeading = (TextView) findViewById(R.id.predictionHeading);
        temperatureText = (TextView) findViewById(R.id.temperature);
        HumidityText = (TextView) findViewById(R.id.humidity);
        GyroYText = (TextView) findViewById(R.id.gyroY);
        GyroZText = (TextView) findViewById(R.id.gyroZ);
        AccYText = (TextView) findViewById(R.id.accY);
        AccZText = (TextView) findViewById(R.id.accZ);
        fearLevelText = (TextView) findViewById(R.id.fearLevelText);
        fearPercentage = (TextView) findViewById(R.id.fearPercentage);
        accuracyText = (TextView) findViewById(R.id.accuracyText);
        whatToDo = (TextView) findViewById(R.id.whatToDo);
        predictedScene = (TextView) findViewById(R.id.predictedScene);
        fearLevel = (ProgressBar) findViewById(R.id.fearLevel);

        setVisibility(false);

        startPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRunning) {
                    toFetch = getSharedPreferences("toFetch",MODE_PRIVATE).getBoolean("toFetch",true);
                    if(toFetch) {
                        stopPrediction.setClickable(false);
                        setVisibility(false);
                        progressBar.setVisibility(View.VISIBLE);
                        predictingText.setVisibility(View.VISIBLE);
                        predictionData = new float[10000];
                        predictionData();
                        isRunning = true;
                    }
                    else {
                        Toast.makeText(PredictionsActivity.this, "Required sensors not connected", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(PredictionsActivity.this, "Already predicting..", Toast.LENGTH_SHORT).show();
            }
        });

        stopPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.GONE);
                predictingText.setVisibility(View.GONE);
                handler.removeCallbacksAndMessages(null);
                isRunning = false;
            }
        });

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                toFetch = getSharedPreferences("toFetch",MODE_PRIVATE).getBoolean("toFetch",true);
                if(toFetch) {
                    String temp = getSharedPreferences("Data",MODE_PRIVATE).getString("temp","");
                    String humidity = getSharedPreferences("Data",MODE_PRIVATE).getString("humidity","");
                    String gyroY = getSharedPreferences("Data",MODE_PRIVATE).getString("gyroY","");
                    String gyroZ = getSharedPreferences("Data",MODE_PRIVATE).getString("gyroZ","");
                    String accY = getSharedPreferences("Data",MODE_PRIVATE).getString("accY","");
                    String accZ = getSharedPreferences("Data",MODE_PRIVATE).getString("accZ","");
                    Log.i("Predictions",gyroY);
                    Log.i("Predictions",accY);
                    setVisibility(true);
                    makePrediction(Float.valueOf(temp), Float.valueOf(humidity), Float.valueOf(gyroY), Float.valueOf(gyroZ),
                            Float.valueOf(accY), Float.valueOf(accZ));
                    handler.postDelayed(runnable,10);
                }
            }
        };
    }

    private void makePrediction(float temperature, float humidity, float gyroY, float gyroZ, float accY, float accZ) {


    }

    private float[] predictionData() {



        progressBar.setVisibility(View.GONE);
        predictingText.setVisibility(View.GONE);
        stopPrediction.setClickable(true);
        runnable.run();
        return predictionData;
    }

    private void setVisibility(boolean toSet) {
        if(toSet) {
            predictionHeading.setVisibility(View.VISIBLE);
            temperatureText.setVisibility(View.VISIBLE);
            HumidityText.setVisibility(View.VISIBLE);
            GyroYText.setVisibility(View.VISIBLE);
            GyroZText.setVisibility(View.VISIBLE);
            AccYText.setVisibility(View.VISIBLE);
            AccZText.setVisibility(View.VISIBLE);
            fearLevelText.setVisibility(View.VISIBLE);
            fearPercentage.setVisibility(View.VISIBLE);
            accuracyText.setVisibility(View.VISIBLE);
            whatToDo.setVisibility(View.VISIBLE);
            predictedScene.setVisibility(View.VISIBLE);
            fearLevel.setVisibility(View.VISIBLE);
        }
        else {
            predictionHeading.setVisibility(View.GONE);
            temperatureText.setVisibility(View.GONE);
            HumidityText.setVisibility(View.GONE);
            GyroYText.setVisibility(View.GONE);
            GyroZText.setVisibility(View.GONE);
            AccYText.setVisibility(View.GONE);
            AccZText.setVisibility(View.GONE);
            fearLevelText.setVisibility(View.GONE);
            fearPercentage.setVisibility(View.GONE);
            accuracyText.setVisibility(View.GONE);
            whatToDo.setVisibility(View.GONE);
            predictedScene.setVisibility(View.GONE);
            fearLevel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
        isRunning = false;
        finish();
    }
}
