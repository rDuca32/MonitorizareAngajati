package domain;

import java.time.LocalTime;

public class Time {
    private int hours;
    private int minutes;

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public static Time fromLocalTime(LocalTime localTime) {
        return new Time(localTime.getHour(), localTime.getMinute());
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
