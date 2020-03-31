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
    private String pin4;
    private String pin6;
    private String pin8;
    private String pattern4;
    private String pattern6;
    private String pattern8;
    private String name;

    public int getUid() {
        return uid;
    }

    public String getpattern4() {
        return pattern4;
    }

    public String getpattern6() {
        return pattern6;
    }

    public String getpattern8() { return pattern8; }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPin4() {
        return pin4;
    }

    public void setPin4(String pin4) {
        this.pin4 = pin4;
    }

    public String getPin6() {
        return pin6;
    }

    public void setPin6(String pin6) {
        this.pin6 = pin6;
    }

    public String getPin8() {
        return pin8;
    }

    public void setPin8(String pin8) {
        this.pin8 = pin8;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
