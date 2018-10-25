package com.example.ravi.myosa;


import java.util.ArrayList;

public class EventAttributes {

    boolean selected;
    double max;
    double min;
    boolean inclusive;
    String s;
    boolean string;
    public EventAttributes() {
    }
    public EventAttributes(boolean selected, double max, double min, boolean inclusive, String s) {
        this.selected = selected;
        this.max = max;
        this.min = min;
        this.inclusive = inclusive;
        this.s = s;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    public void setInclusive(boolean inclusive) {
        this.inclusive = inclusive;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
