package com.example.ravi.myosa;

/**
 * Created by ravi on 11-04-2018.
 */

public class Attributes {

    public String getHead() {
        return head;
    }

    public String getAttName() {
        return attName;
    }

    public int getType() {
        return type;
    }

    public String getAtt1() {
        return att1;
    }

    public String getAtt2() {
        return att2;
    }

    public String getAtt3() {
        return att3;
    }

    String head;
    String attName;
    String att1;
    String att2;
    String att3;
    int type;    // 1 = single value, 2 = single String value, 3 = triplet value, 4 = triplet String value

    Attributes(String h, String an, int n){
        this.attName = an;
        this.head = h;
        this.type= n;
    }

    Attributes(String h, String at1, String at2, String at3, int n){
        this.att1 = at1;
        this.att2 = at2;
        this.att3 = at3;
        this.head = h;
        this.type= n;
    }
}
