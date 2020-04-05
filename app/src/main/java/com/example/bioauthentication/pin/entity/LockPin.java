package com.example.bioauthentication.pin.entity;


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
public class LockPin {
    @Setter private String digit;
    @Setter private Float x;
    @Setter private Float y;
    @Setter @Getter private Long timeEventDown, timeEventUp;
    @Setter private Long timeBetweenTouch;
    @Setter private Long timeLapsePress;
    @Setter private Float area;
    @Setter private Float press;

    public LockPin(LockPin clone){
        this.digit = clone.digit;
        this.x = clone.x;
        this.y = clone.y;
        this.timeEventDown = clone.timeEventDown;
        this.timeEventUp = clone.timeEventUp;
        this.timeBetweenTouch = clone.timeBetweenTouch;
        this.timeLapsePress = clone.timeLapsePress;
        this.area = clone.area;
        this.press = clone.press;
    }
}
