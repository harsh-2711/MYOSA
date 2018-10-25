package com.example.ravi.myosa.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Switch;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.ravi.myosa.Attributes;
import com.example.ravi.myosa.sensorDetails;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ravi on 04-04-2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="SensorData.db";
    private static final String TABLE_NAME="Sensors";
    private static final String COL_1="_id";


    private static Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT )");
        int n;
        for (int i = 0; i < sensorDetails.SensorAttributes.size(); i++) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + "`"+sensorDetails.SensorAttributes.get(i).getAttName()+"`" + " VARCHAR(300) ");
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

    public boolean InsertData(String s[])
    {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        for(int i=0;i<s.length;i++) {
            contentValues.put("`"+sensorDetails.SensorAttributes.get(i).getAttName()+"`",s[i]);
        }
        database.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public void Export()
    {
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Myosa/";

        File file = new File(directory_path);

        if (!file.exists()) {

            file.mkdirs();

        }

        SQLiteToExcel sqLiteToExcel=new SQLiteToExcel(context.getApplicationContext(),DATABASE_NAME,directory_path);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        sqLiteToExcel.exportAllTables("Myosa"+ dateFormat.format(new Date())+".xls", new SQLiteToExcel.ExportListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String filePath) {




            }



            @Override
            public void onError(Exception e) {

            }

        });

    }

}
