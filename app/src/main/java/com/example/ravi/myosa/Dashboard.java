package com.example.ravi.myosa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ravi.myosa.Database.DatabaseHelper;
import com.example.ravi.myosa.bluetooth_connectivity.BluetoothComService;
import com.example.ravi.myosa.bluetooth_connectivity.DeviceList;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Dashboard extends AppCompatActivity {

    // Message types sent from the BluetoothComService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private String to_store = "";
    private static String[] SensorNames;
    private static DatabaseHelper databaseHelper;

    // Key names received from the BluetoothComService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothComService mChatService = null;
    View[] view = new View[18];
    GraphView[] graphViews;
    LineGraphSeries[] lineGraphSeries;
    private boolean visi[];
    sensorDetails sns;
    double xAxis = 0;
    Uri uriofNotification;
    NotificationManagerCompat notMang;

    public boolean toFetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        notMang = NotificationManagerCompat.from(this);
        databaseHelper = new DatabaseHelper(this);
        deleteDatabase(databaseHelper.DATABASE_NAME);
        uriofNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        sns = new sensorDetails();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // 33 entries
        String[] p = {"*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"};
        databaseHelper.InsertData(p);
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menudash, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null) setupChat();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothComService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        mChatService = new BluetoothComService(this, mHandler);
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }

    private void ensureDiscoverable() {

        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    private void sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothComService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothComService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    String readMessage = (String) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    //String readMessage = new String(readBuf, 0, msg.arg1);

                    if (to_store.split(",").length > sensorDetails.TOTAL_VALUES) {
                        to_store = "";
                        break;
                    }

                    if (to_store.length() != 0) {
                        to_store = to_store + readMessage;
                    } else {
                        to_store = readMessage;
                    }

                    if ((to_store.split(",").length == sensorDetails.TOTAL_VALUES)) {
                        to_store = to_store.trim();
                        String s[] = to_store.split(",");
                        Log.e("msg", "rcvd " + to_store);
                        to_store = "";

                        if(!s[12].equals("*") || !s[14].equals("*") || !s[23].equals("*") || !s[24].equals("*") || !s[26].equals("*") || !s[27].equals("*"))
                            toFetch = true;
                        else
                            toFetch = false;
                        SharedPreferences sharedPreferences = getSharedPreferences("toFetch",MODE_PRIVATE);
                        sharedPreferences.edit().putBoolean("toFetch",toFetch).apply();

                        addData(s);
                        checkEvent(s);
                        databaseHelper.InsertData(s);
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Snackbar.make((ScrollView) findViewById(R.id.scrol), "Connected to " + mConnectedDeviceName,
                            Snackbar.LENGTH_SHORT).show();
                    findViewById(R.id.instuText).setVisibility(View.GONE);
                    break;
                case MESSAGE_TOAST:
                    Snackbar.make((ScrollView) findViewById(R.id.scrol), msg.getData().getString(TOAST),
                            Snackbar.LENGTH_SHORT).show();
                    if (msg.getData().equals("Unable to connect device")) {
                        findViewById(R.id.instuText).setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceList.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    mChatService.connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public void discoverable() {
        ensureDiscoverable();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to terminate this session?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNeutralButton("Save & Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHelper.Export();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Snackbar.make((ScrollView) findViewById(R.id.scrol), "Data won't be stored!",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent serverIntent;

        switch (item.getItemId()) {
            case R.id.action_connect:
                serverIntent = new Intent(this, DeviceList.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;

            case R.id.cEvent:
                if (sensorDetails.evntCreated == sensorDetails.lintEvent) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setMessage("Limit exceeded! You can create only three events");
                    builder.setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sensorDetails.clearEvents();
                        }
                    });
                    builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    android.support.v4.app.DialogFragment newFragment = new EventManagement();
                    newFragment.show(getSupportFragmentManager(), "eventmanagement");
                }
                return true;

            case R.id.seePredictions:
                Intent intent = new Intent(Dashboard.this, PredictionsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void addData(String[] s) {
        SharedPreferences sharedPreferences = getSharedPreferences("Data",MODE_PRIVATE);
        sharedPreferences.edit().putString("temp",s[12]).apply();
        sharedPreferences.edit().putString("humidity",s[14]).apply();
        sharedPreferences.edit().putString("gyroY",s[23]).apply();
        sharedPreferences.edit().putString("gyroZ",s[24]).apply();
        sharedPreferences.edit().putString("accY",s[26]).apply();
        sharedPreferences.edit().putString("accZ",s[27]).apply();
        for (int i = 0; i < 16; i++) {
            if (s[i].equals("*")) {
                if (visi[i]) {
                    visi[i] = false;
                    view[i].setVisibility(View.GONE);
                }
            } else {
                if (!visi[i]) {
                    visi[i] = true;
                    view[i].setVisibility(View.VISIBLE);
                }
                lineGraphSeries[i].appendData(new DataPoint(xAxis, Double.parseDouble(s[i])), true, 40);
                ((TextView) view[i].findViewById(R.id.tValue)).setText(s[i]);
            }
        }

        int stNum = 16;
        for (int i = 16; i < 20; i++) {
            if (s[stNum].equals("*")) {
                if (visi[i]) {
                    visi[i] = false;
                    view[i].setVisibility(View.GONE);
                }
                stNum = stNum + 3;
            } else {
                if (!visi[i]) {
                    visi[i] = true;
                    view[i].setVisibility(View.VISIBLE);
                }
                if (Double.valueOf(s[23]) >= 75) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    String number = "7574842044";
                    callIntent.setData(Uri.parse("tel:" + number));//change the number
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                }
                lineGraphSeries[stNum].appendData(new DataPoint(xAxis, Double.parseDouble(s[stNum])),true, 40);
                ((TextView)view[i].findViewById(R.id.tValue1)).setText(s[stNum]);
                stNum++;
                lineGraphSeries[stNum].appendData(new DataPoint(xAxis, Double.parseDouble(s[stNum])),true, 40);
                ((TextView)view[i].findViewById(R.id.tValue2)).setText(s[stNum]);
                stNum++;
                lineGraphSeries[stNum].appendData(new DataPoint(xAxis, Double.parseDouble(s[stNum])),true, 40);
                ((TextView)view[i].findViewById(R.id.tValue3)).setText(s[stNum]);
                stNum++;
            }
        }

        stNum=28;
        int curView=20;
        if(s[stNum].equals("*")) {
            if (visi[curView]) {
                visi[curView] = false;
                view[curView].setVisibility(View.GONE);
            }
            stNum=stNum+3;
        }
        else {
            if(!visi[curView]){
                visi[curView]=true;
                view[curView].setVisibility(View.VISIBLE);
            }
            ((TextView)view[curView].findViewById(R.id.tTime)).setText(s[stNum]);
            stNum++;
            ((TextView)view[curView].findViewById(R.id.tdate)).setText(s[stNum]);
            stNum++;
            ((TextView)view[curView].findViewById(R.id.tday)).setText(s[stNum]);
            stNum++;
        }

        curView++;
        if(s[stNum].equals("*")){
            if(visi[curView]){
                visi[curView]=false;
                view[curView].setVisibility(View.GONE);
            }
            stNum++;
        }
        else {
            if(!visi[curView]){
                visi[curView]=true;
                view[curView].setVisibility(View.VISIBLE);
            }
            ((TextView)view[curView].findViewById(R.id.tPresence)).setText(s[stNum]);
        }

        curView++;
        if(s[stNum].equals("*")) {
            if (visi[curView]) {
                visi[curView] = false;
                view[curView].setVisibility(View.GONE);
            }
            stNum++;
        }
        else {
            if(!visi[curView]){
                visi[curView]=true;
                view[curView].setVisibility(View.VISIBLE);
            }
            ((TextView)view[curView].findViewById(R.id.tGesture)).setText(s[stNum]);
        }
        xAxis=xAxis+0.1;
    }

    void initViews() {
        view = new View[23];
        graphViews = new GraphView[20];
        lineGraphSeries = new LineGraphSeries[28];
        view[0] = findViewById(R.id.sValue1);
        view[1] = findViewById(R.id.sValue2);
        view[2] = findViewById(R.id.sValue3);
        view[3] = findViewById(R.id.sValue4);
        view[4] = findViewById(R.id.sValue5);
        view[5] = findViewById(R.id.sValue6);
        view[6] = findViewById(R.id.sValue7);
        view[7] = findViewById(R.id.sValue8);
        view[8] = findViewById(R.id.sValue9);
        view[9] = findViewById(R.id.sValue10);
        view[10] = findViewById(R.id.sValue11);
        view[11] = findViewById(R.id.sValue12);
        view[12] = findViewById(R.id.sValue13);
        view[13] = findViewById(R.id.sValue14);
        view[14] = findViewById(R.id.sValue15);
        view[15] = findViewById(R.id.sValue16);

        view[16] = findViewById(R.id.sValue31);
        view[17] = findViewById(R.id.sValue32);
        view[18] = findViewById(R.id.sValue33);
        view[19] = findViewById(R.id.sValue34);

        view[20] = findViewById(R.id.rtc);
        view[21] = findViewById(R.id.ptclSns);
        view[22] = findViewById(R.id.rgbS);

        int snsT = 0;

        visi = new boolean[23];
        for (int i = 0; i < 23; i++) {
            visi[i] = false;
            view[i].setVisibility(View.GONE);
        }

        for(int i=0;i<28;i++){
            lineGraphSeries[i]=new LineGraphSeries();
            lineGraphSeries[i].setAnimated(true);
            lineGraphSeries[i].setDataPointsRadius(6);
            lineGraphSeries[i].setDrawDataPoints(true);
            lineGraphSeries[i].setThickness(5);
        }

        for (int i = 0; i < 16; i++) {
            TextView headofAt = view[i].findViewById(R.id.tSensorHead);
            TextView nameOfatt = view[i].findViewById(R.id.tAttribute);
            while (snsT < sns.TOTAL_SENSORS && sns.tiles.get(snsT).getType() != 1)
                snsT++;
            headofAt.setText(sns.tiles.get(snsT).getHead());
            nameOfatt.setText(sns.tiles.get(snsT).getAttName());
            snsT++;

            GraphView graph = view[i].findViewById(R.id.graph);
            graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
            graph.getGridLabelRenderer().setLabelVerticalWidth(25);
            graph.getGridLabelRenderer().setTextSize(25);
            graph.getGridLabelRenderer().setVerticalLabelsColor(R.color.secondaryTextColor);
            graph.getViewport().setMinX(0);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMaxX(3);

            graph.addSeries(lineGraphSeries[i]);
            lineGraphSeries[i].setColor(Color.argb(255,0, 177, 184));
        }

        snsT=0;
        int lnGnum=16;
        String attName;
        for (int i = 16; i < 20; i++) {
            TextView head = view[i].findViewById(R.id.tSensorHead);
            TextView att1 = view[i].findViewById(R.id.tAttribute1);
            TextView att2 = view[i].findViewById(R.id.tAttribute2);
            TextView att3 = view[i].findViewById(R.id.tAttribute3);
            GraphView graph = view[i].findViewById(R.id.graph);
            graph.getGridLabelRenderer().setLabelVerticalWidth(25);
            graph.getGridLabelRenderer().setTextSize(25);
            graph.getGridLabelRenderer().setVerticalLabelsColor(R.color.secondaryTextColor);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(3);
            graph.getViewport().setXAxisBoundsManual(true);

            while (snsT < sns.TOTAL_SENSORS && sns.tiles.get(snsT).getType() != 3)
                snsT++;

            head.setText(sns.tiles.get(snsT).getHead());

            attName = sns.tiles.get(snsT).getAtt1();
            att1.setText(attName);
            lineGraphSeries[lnGnum].setColor(Color.argb(255, 245, 124, 0));
            graph.addSeries(lineGraphSeries[lnGnum]);
            lineGraphSeries[lnGnum].setTitle(attName);
            lnGnum++;

            attName = sns.tiles.get(snsT).getAtt2();
            att2.setText(attName);
            lineGraphSeries[lnGnum].setColor(Color.argb(255,67, 160, 71));
            graph.addSeries(lineGraphSeries[lnGnum]);
            lineGraphSeries[lnGnum].setTitle(attName);
            lnGnum++;

            attName = sns.tiles.get(snsT).getAtt3();
            att3.setText(attName);
            lineGraphSeries[lnGnum].setColor(Color.argb(255,3, 155, 229));
            graph.addSeries(lineGraphSeries[lnGnum]);
            lineGraphSeries[lnGnum].setTitle(attName);
            lnGnum++;

            ((ImageView)view[i].findViewById(R.id.cat1)).setBackgroundColor(Color.rgb(245, 124, 0));
            ((ImageView)view[i].findViewById(R.id.cat2)).setBackgroundColor(Color.rgb(67, 160, 71));
            ((ImageView)view[i].findViewById(R.id.cat3)).setBackgroundColor(Color.rgb(3, 155, 229));
            snsT++;
        }
    }

    public void checkEvent(String[] s){
        if(sensorDetails.evntCreated!=0){
            for(int i=0;i<sensorDetails.evntCreated;i++){
                boolean event=true;
                for(int j=0;j<sensorDetails.TOTAL_VALUES;j++) {
                    EventAttributes temp = sensorDetails.eventAttributes.get(i)[j];
                    if (temp.selected) {
                        if (!s[j].equals("*")) {
                            if (temp.inclusive) {
                                if (sensorDetails.SensorAttributes.get(j).isNum()) {
                                    if (!(Double.parseDouble(s[j]) <= temp.getMax() && Double.parseDouble(s[j]) >= temp.getMin())) {
                                        Log.e("1", temp.getMax()+" "+temp.getMin()+" "+s[j]);
                                        event = false;
                                        break;
                                    }
                                } else {
                                    if (s[j].equals(temp.getS())) {
                                        Log.e("2", temp.getS()+" "+s[j]);
                                        event = false;
                                        break;
                                    }
                                }
                            } else {
                                if (sensorDetails.SensorAttributes.get(j).isNum()) {
                                    if (!(Double.parseDouble(s[j]) >= temp.getMax() || Double.parseDouble(s[j]) <= temp.getMin())) {
                                        Log.e("3", temp.getMax()+" "+temp.getMin()+" "+s[j]);
                                        event = false;
                                        break;
                                    }
                                } else {
                                    if (s[j].equals(temp.getS())) {
                                        Log.e("4", temp.getS()+" "+s[j]);
                                        event = false;
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            event=false;
                        }
                    }
                }
                Log.e("sensor",event+" ");
                if(event){
                    if(!sensorDetails.notified[i]) {
                        this.sendMessage(sensorDetails.charTosend.get(i));
                        NotificationCompat.Builder notbuilder = new NotificationCompat.Builder(this);
                        notbuilder.setSmallIcon(R.drawable.save);
                        notbuilder.setContentTitle(sensorDetails.eventName.get(i) + " " + "Occurred");
                        notbuilder.setContentText("Actuators are working now");
                        notbuilder.setSound(uriofNotification);
                        notMang.notify(i, notbuilder.build());
                        sendMessage(sensorDetails.charTosend.get(i));
                        sensorDetails.notified[i]=true;
                    }
                }
                else {
                    sensorDetails.notified[i]=false;
                }
            }
        }
    }
}
