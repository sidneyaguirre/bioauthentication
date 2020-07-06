package com.example.bioauthentication.services;

import com.example.bioauthentication.services.entities.request;
import com.example.bioauthentication.services.entities.response;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.Call;



public interface PostService {

    String ROUTE_PREDICT_PIN = "/predict/pin";
    String ROUTE_PREDICT_PATTERN = "/predict/pattern";

    String BASE_URL = "http://34.66.140.59:8000";

    @POST(ROUTE_PREDICT_PIN)
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call <response> PostPredictPin(@Body request data);

    @POST(ROUTE_PREDICT_PATTERN)
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call <response> PostPredictPattern(@Body request data);
}
