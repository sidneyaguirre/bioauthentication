package com.example.bioauthentication.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bioauthentication.R;
import com.google.android.material.textview.MaterialTextView;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private int uid;
    private int pin4;
    private int pin6;
    private int pin8;
    private String name;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPin4() {
        return pin4;
    }

    public void setPin4(int pin4) {
        this.pin4 = pin4;
    }

    public int getPin6() {
        return pin6;
    }

    public void setPin6(int pin6) {
        this.pin6 = pin6;
    }

    public int getPin8() {
        return pin8;
    }

    public void setPin8(int pin8) {
        this.pin8 = pin8;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
