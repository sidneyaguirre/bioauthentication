package com.example.bioauthentication.services;

import android.util.Log;

import com.example.bioauthentication.R;
import com.example.bioauthentication.services.entities.request;
import com.example.bioauthentication.services.entities.response;
import com.example.bioauthentication.services.entities.responseCallback;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.bioauthentication.services.PostService.BASE_URL;

public class PredictML{

    private Retrofit m_retrofit;
    private responseCallback m_responseCallback;

    public PredictML(responseCallback responseCallback) {
        m_retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        m_responseCallback = responseCallback;
    }

    public void PostPredictPin(final int userId, final ArrayList<Double> userData) {
        PostService postService = m_retrofit.create(PostService.class);
        Call<response> call= postService.PostPredictPin(new request(userId, userData));

        call.enqueue(new Callback<response>() {
            @Override
            public void onResponse(Call<response> call, Response<response> response) {
                if(response.code() == 200) {
                    Log.d("RETROFIT", "onResponse: "+  response.body().toString());
                    m_responseCallback.updateView(response.body().getReal());
                }else {
                    Log.d("RETROFIT", "onResponse: "+  response.code());
                    m_responseCallback.errorInRequest(R.string.error_request);
                }
            }

            @Override
            public void onFailure(Call<response> call, Throwable t) {
                Log.d("RETROFIT", "onFailure: "+  t.toString());
                m_responseCallback.errorInRequest(R.string.error_request);
            }
        });
    }

    public void PostPredictPatter(int userId, ArrayList<Double> userData) {
        PostService postService = m_retrofit.create(PostService.class);
        Call<response> call= postService.PostPredictPattern(new request(userId, userData));

        call.enqueue(new Callback<response>() {
            @Override
            public void onResponse(Call<response> call, Response<response> response) {
                if(response.code() == 200) {
                    Log.d("RETROFIT", "onResponse: "+  response.body().toString());
                    m_responseCallback.updateView(response.body().getReal());
                }else {
                    Log.d("RETROFIT", "onResponse: "+  response.code());
                    m_responseCallback.errorInRequest(R.string.error_request);
                }
            }

            @Override
            public void onFailure(Call<response> call, Throwable t) {
                Log.d("RETROFIT", "onFailure: "+  t.toString());
                m_responseCallback.errorInRequest(R.string.error_request);
            }
        });
    }

}
