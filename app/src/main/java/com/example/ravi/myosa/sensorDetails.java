package com.example.ravi.myosa;

import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by Pratik on 03-04-2018.
 */

public class sensorDetails {
    static int TOTAL_SENSORS = 0;
    static int TOTAL_VALUES = 33;
    public ArrayList<Attributes> tiles;
    public static ArrayList<attr> SensorAttributes;
    public static ArrayList<EventAttributes[]> eventAttributes = new ArrayList<EventAttributes[]>();
    public static int evntCreated = 0;
    public static int lintEvent = 3;
    public static ArrayList<String> eventName = new ArrayList<String>();
    static boolean eventAdded = false;
    public static ArrayList<String> charTosend = new ArrayList<>();
    public static boolean[] notified = new boolean[3];
    public sensorDetails(){
        this.tiles=new ArrayList<>();

        this.SensorAttributes=new ArrayList<attr>();
        tiles.add(new Attributes("Real Time Clock", "Date","Time","Day",4));

        tiles.add(new Attributes("Luminousity Sensor","Visibility(Raw Data)", 1));
        tiles.add(new Attributes("Luminousity Sensor","Infrared (Raw Data)", 1));
        tiles.add(new Attributes("Luminousity Sensor","Illuminance(LUX)", 1));

        tiles.add(new Attributes("Berometric Pressure sensor","Temprature (\u00B0C)", 1));
        tiles.add(new Attributes("Berometric Pressure sensor","Pressure (mbar)", 1));
        tiles.add(new Attributes("Berometric Pressure sensor","Pressure (mmHg)", 1));
        tiles.add(new Attributes("Berometric Pressure sensor","Altitude (meter)", 1));

        tiles.add(new Attributes("Air Quality Snensor","CO2 (ppm)", 1));
        tiles.add(new Attributes("Air Quality Snensor","TVOC (ppb)", 1));

        tiles.add(new Attributes("Particle Sensor","Heart Rate (BPM)", 1));
        tiles.add(new Attributes("Particle Sensor","Avg Heart Rate (BPM)", 1));
        tiles.add(new Attributes("Particle Sensor","Oxygenated blood (%)", 1));
        tiles.add(new Attributes("Particle Sensor","Presence Detection", 2));

        tiles.add(new Attributes("Temperature and Humidity Sensor", "Temperature (\u00B0C)",1));
        tiles.add(new Attributes("Temperature and Humidity Sensor", "Temperature (\u00B0F)",1));
        tiles.add(new Attributes("Temperature and Humidity Sensor", "Humidity (%)",1));

        tiles.add(new Attributes("Magnetometer", "X (mgauss)", "Y (mgauss)", "Z (mgauss)", 3));

        tiles.add(new Attributes("RGB and Gesture Sensor", "Ambient Light (Raw data)",1));
        tiles.add(new Attributes("RGB and Gesture Sensor", "Gesture",2));
        tiles.add(new Attributes("RGB and Gesture Sensor", "Red (Raw data)", "Green (Raw data)", "Blue (Raw data)",3));

        tiles.add(new Attributes("MPU6050", "Gx (degree)","Gy (degree)","Gz (degree)",3));
        tiles.add(new Attributes("MPU6050", "Ax (m/(s)Square)","Ay (m/(s)Square)","Az (m/(s)Square)",3));

        TOTAL_SENSORS=tiles.size();
        for(int i=0;i<tiles.size();i++){
            if(tiles.get(i).getType()==1)
                SensorAttributes.add(new attr(tiles.get(i).getAttName() +" ("+ tiles.get(i).getHead()+")", true));
        }

        for(int i=0;i<tiles.size();i++){
            if(tiles.get(i).getType()==3) {
                SensorAttributes.add(new attr(tiles.get(i).getAtt1()+" ("+ tiles.get(i).getHead()+")", true));
                SensorAttributes.add(new attr(tiles.get(i).getAtt2()+" ("+ tiles.get(i).getHead()+")", true));
                SensorAttributes.add(new attr(tiles.get(i).getAtt3()+" ("+ tiles.get(i).getHead()+")", true));
            }
        }

        for(int i=0;i<tiles.size();i++){
            if(tiles.get(i).getType()==4) {
                SensorAttributes.add(new attr(tiles.get(i).getAtt1()+" ("+ tiles.get(i).getHead()+")", false));
                SensorAttributes.add(new attr(tiles.get(i).getAtt2()+" ("+ tiles.get(i).getHead()+")", false));
                SensorAttributes.add(new attr(tiles.get(i).getAtt3()+" ("+ tiles.get(i).getHead()+")", false));
            }
        }

        for(int i=0;i<tiles.size();i++){
            if(tiles.get(i).getType()==2)
                SensorAttributes.add(new attr(tiles.get(i).getAttName()+" ("+ tiles.get(i).getHead()+")", false));
        }
    }

    public static void clearEvents(){
        evntCreated=0;
        eventAttributes.clear();
        eventName.clear();
        eventAdded=false;
        charTosend.clear();
        notified[0]=false;
        notified[1]=false;
        notified[2]=false;
    }

    public static void setupNewEvent(){
        if(!eventAdded) {
            eventAttributes.add(new EventAttributes[TOTAL_VALUES]);
            for (int i = 0; i < TOTAL_VALUES; i++) {
                EventAttributes e = new EventAttributes();
                eventAttributes.get(evntCreated)[i] = e;
            }
            eventAdded=true;
        }
    }


    public class attr{
        private String attName;
        private boolean num;

        public String getAttName() {
            return attName;
        }

        public void setAttName(String attName) {
            this.attName = attName;
        }

        public boolean isNum() {
            return num;
        }

        public void setNum(boolean num) {
            this.num = num;
        }

        public attr(String name, boolean num){
            this.attName = name;
            this.num=num;
        }
    }
}
