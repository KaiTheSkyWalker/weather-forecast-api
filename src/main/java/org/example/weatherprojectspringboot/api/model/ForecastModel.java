package org.example.weatherprojectspringboot.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForecastModel {
    private String ymd;
    private String high;
    private String sunrise;
    private String fx;
    private String week;
    private String low;
    private String fl;
    private String sunset;
    private String aqi;
    private String type;
    private String notice;

    @Override
    public String toString() {
        return "{" +
                "ymd= " + ymd + ", " +
                "high= " + high + ", " +
                "sunrise= " + sunrise + ", " +
                "fx= " + fx + ", " +
                "week= " + week + ", " +
                "low= " + low + ", " +
                "fl= " + fl + ", " +
                "sunset= " + sunset + ", " +
                "aqi= " + aqi + ", " +
                "type= " + type + ", " +
                "notice=    " + notice + " " +
                '}';
    }
}
