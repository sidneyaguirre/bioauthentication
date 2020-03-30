package com.example.bioauthentication.pattern.entity;


import com.example.bioauthentication.pin.entity.LockPin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LockPattern {
    @Setter
    private String digit;
    @Setter private Float x;
    @Setter private Float y;
    @Setter private Float area;
    @Setter private Float press;
    @Setter private long currentTime;
    @Setter private long timeOnSelected;
    @Setter private long timeBegin;
    @Setter private long timeEnd;

}
