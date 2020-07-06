package com.example.bioauthentication.pattern.entity;


import com.example.bioauthentication.pin.entity.LockPin;

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
public class LockPattern {
    @Setter @Getter private String digit;
    @Setter @Getter private Float x;
    @Setter @Getter private Float y;
    @Setter @Getter private Float area;
    @Setter @Getter private Float press;
    @Setter private long currentTime;
    @Setter @Getter private long timeOnSelected;
    @Setter private long timeBegin;
    @Setter private long timeEnd;

}
