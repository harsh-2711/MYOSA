package com.example.ravi.myosa;

public class Phobia {

    public String phobia;
    public int index;

    public Phobia(String phobia, int index) {
        this.phobia = phobia;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getPhobia() {
        return phobia;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPhobia(String phobia) {
        this.phobia = phobia;
    }
}
