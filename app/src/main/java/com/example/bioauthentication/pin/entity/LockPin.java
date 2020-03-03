package com.example.bioauthentication.pin.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LockPin {
    private String digit;
    private Float x;
    private Float y;
    private Long timeEvent;
    private Long timeDown;
}
