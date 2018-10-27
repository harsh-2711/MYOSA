package com.example.ravi.myosa;

import android.os.AsyncTask;
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

import java.util.ArrayList;

public class PredictionsActivity extends AppCompatActivity {

    Button startPrediction, stopPrediction;
    boolean isRunning = false;

    Runnable runnable;
    Handler handler;

    ProgressBar progressBar;
    TextView predictingText;

    TextView predictionHeading, GyroXText, GyroYText, GyroZText, AccYText, AccZText, temperatureText, HumidityText,
            fearLevelText, fearPercentage, accuracyText, whatToDo, predictedScene;
    ProgressBar fearLevel;

    boolean toFetch;

    ArrayList<Double> predictionDataX, predictionDataY, predictionDataZ, previousDataX, previousDataY, previousDataZ;

    double predictionMeanX = 0, predictionMeanY = 0, predictionMeanZ = 0, previousMeanX = 0, previousMeanY = 0, previousMeanZ = 0;

    private String[] scenes;

    private double walkingXLow = -100, walkingXHigh = 100, walkingYLow = -100, walkingYHigh = 100, walkingZLow = -20, walkingZHigh = 20,
            normalXLow = -200, normalXHigh = 200, normalYLow = -100, normalYHigh = 100, normalZLow = -20, normalZHigh = 20,
            slightXLow = -200, slightXHigh = 200, slightYLow = -200, slightYHigh = 200, slightZLow = -40, slightZHigh = 40,
            highXLow = -300, highXHigh = 300, highYLow = -300, highYHigh = 300, highZLow = -60, highZHigh = 60,
            extremeXLow = -300, extremeXHigh = 300, extremeYLow = -300, extremeYHigh = 300, extremeZLow = -150, extremeZHigh = 150;

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

        predictionDataX = new ArrayList<>();
        predictionDataY = new ArrayList<>();
        predictionDataZ = new ArrayList<>();
        previousDataX = new ArrayList<>();
        previousDataY = new ArrayList<>();
        previousDataZ = new ArrayList<>();

        startPrediction = (Button) findViewById(R.id.startPrediction);
        stopPrediction = (Button) findViewById(R.id.stopPrediction);

        scenes = new String[6];
        scenes[0] = "Person is simply walking. So, progressing the game with showing slightly difficult scene and will then increase the difficulty if the fear level still remains low";
        scenes[1] = "Improving the difficulty of the scene as the person is still normal watching previous scene";
        scenes[2] = "Increasing slight difficulty of the scene than previous one";
        scenes[3] = "Showing the previous scene again and testing the fear level again to bring it down to almost 30%";
        scenes[4] = "Stopping the game as the fear level has increased tremendously";
        scenes[5] = "The game is not yet started";

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        predictingText = (TextView) findViewById(R.id.predictingText);

        predictionHeading = (TextView) findViewById(R.id.predictionHeading);
        //temperatureText = (TextView) findViewById(R.id.temperature);
        //HumidityText = (TextView) findViewById(R.id.humidity);
        GyroXText = (TextView) findViewById(R.id.gyroX);
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
                    String gyroX = getSharedPreferences("Data",MODE_PRIVATE).getString("gyroX","");
                    String gyroY = getSharedPreferences("Data",MODE_PRIVATE).getString("gyroY","");
                    String gyroZ = getSharedPreferences("Data",MODE_PRIVATE).getString("gyroZ","");
                    String accY = getSharedPreferences("Data",MODE_PRIVATE).getString("accY","");
                    String accZ = getSharedPreferences("Data",MODE_PRIVATE).getString("accZ","");
                    setVisibility(true);
                    GyroXText.setText("Gyroscope(Y-axis) : " + gyroX);
                    GyroYText.setText("Gyroscope(Y-axis) : " + gyroY);
                    GyroZText.setText("Gyroscope(Z-axis) : " + gyroZ);
                    AccYText.setText("Accelerometer(Y-axis) : " + accY);
                    AccZText.setText("Accelerometer(Z-axis) : " + accZ);
                    makePrediction(Float.valueOf(gyroX), Float.valueOf(gyroY), Float.valueOf(gyroZ), Float.valueOf(accY), Float.valueOf(accZ));
                    handler.postDelayed(runnable,10);
                }
            }
        };
    }

    private void makePrediction(double gyroX, double gyroY, double gyroZ, double accY, double accZ) {

    }

    private void predictionData() {

        // Set range for each sensor

        /*
           Type of fear - Fear Level                                                                                X-reading   Y-reading   Z-reading       Scene to be shown

           Walking - 0-5%          ->  All the readings at bottom edge                                              (-100,100)  (-100,100)  (-20,20)        Progress the game and start with showing slightly difficult scene and then increase the difficulty as game progresses
           Normal Fear - 5-25%     -> Almost all readings at bottom edge                                            (-200,200)  (-100,100)  (-20,20)        Difficult scene then previous one and repeat that scene again and again
           Slight Fear - 25-50%    -> X and Y axis of Gyroscope reaches to higher edge                              (-200,200)  (-200,200)  (-40,40)        Slightly difficult scene than previous one and slowly increase the level of difficulty
           High Fear - 50-80%      -> X and y axis of gyroscope at extreme edge and slight change in Z axis         (-300,300)  (-300,300)  (-60,60)        Show the previous scene again and test again
           Extreme Fear - 80-100%  -> All X, Y and Z changes suddenly and reaches maximum edge                      (-300,300)  (-300,300)  (-150,150)      Stop the game and call doctor if fear level about 90%

         */

        InitiateData initiateData = new InitiateData();
        initiateData.execute();
    }

    private void setVisibility(boolean toSet) {
        if(toSet) {
            predictionHeading.setVisibility(View.VISIBLE);
            //temperatureText.setVisibility(View.VISIBLE);
            //HumidityText.setVisibility(View.VISIBLE);
            GyroXText.setVisibility(View.VISIBLE);
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
            //temperatureText.setVisibility(View.GONE);
            //HumidityText.setVisibility(View.GONE);
            GyroXText.setVisibility(View.GONE);
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

    private class InitiateData extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            // Just to increase the flavor of the app
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            double maxXY = 100;
            double minXY = -100;
            double maxZ = 20;
            double minZ = -20;

            for(int i = 0; i < 1000; i++) {
                predictionDataX.add(Math.random() * ((maxXY - minXY) + 1) + minXY);
                predictionDataY.add(Math.random() * ((maxXY - minXY) + 1) + minXY);
                predictionDataZ.add(Math.random() * ((maxZ - minZ) + 1) + minZ);

                predictionMeanX += predictionDataX.get(i);
                predictionMeanY += predictionDataY.get(i);
                predictionMeanZ += predictionDataZ.get(i);
            }

            for(int i = 0; i < 10000; i++) {
                previousDataX.add(Math.random() * ((maxXY - minXY) + 1) + minXY);
                previousDataY.add(Math.random() * ((maxXY - minXY) + 1) + minXY);
                previousDataZ.add(Math.random() * ((maxZ - minZ) + 1) + minZ);

                previousMeanX += previousDataX.get(i);
                previousMeanY += previousDataY.get(i);
                previousMeanZ += previousDataZ.get(i);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            predictingText.setVisibility(View.GONE);
            stopPrediction.setClickable(true);
            runnable.run();
            super.onPostExecute(aVoid);
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
