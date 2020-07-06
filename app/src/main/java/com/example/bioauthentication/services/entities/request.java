package com.example.bioauthentication.services.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import retrofit2.http.Field;


@Data
@NoArgsConstructor
@Builder
@ToString
public class request {
    @Setter @Getter
    @SerializedName("user_id")
    @Expose
    private int uid;
    @Setter @Getter
    @SerializedName("data")
    @Expose
    ArrayList<Double> data;

    public request(int userID, ArrayList<Double> data) {
        this.data = data;
        uid = userID;
    }
}
