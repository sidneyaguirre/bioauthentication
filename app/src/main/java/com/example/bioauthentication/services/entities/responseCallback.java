package com.example.bioauthentication.services.entities;

public interface responseCallback {

    void updateView(int response);
    void errorInRequest(int message);
}
