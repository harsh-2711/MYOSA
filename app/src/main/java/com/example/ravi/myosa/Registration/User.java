package com.example.ravi.myosa.Registration;

public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private String DrCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDrCode() {
        return DrCode;
    }

    public void setDrCode(String drCode) {
        DrCode = drCode;
    }
}