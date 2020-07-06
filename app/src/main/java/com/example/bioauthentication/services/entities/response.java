package com.example.bioauthentication.services.entities;


import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class response {
    @Setter
    @Getter
    @SerializedName("real")
    @Expose
    private int real;

}
