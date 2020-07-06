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
    @Setter @Getter private Float x;
    @Setter @Getter private Float y;
    @Setter @Getter private Long timeEventDown, timeEventUp;
    @Setter @Getter private Long timeBetweenTouch;
    @Setter @Getter private Long timeLapsePress;
    @Setter @Getter private Float area;
    @Setter @Getter private Float press;

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
