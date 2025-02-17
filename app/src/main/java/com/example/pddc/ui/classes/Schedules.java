package com.example.pddc.ui.classes;

public class Schedules {
    private final String scheduleName;
    private final String scheduleTime;
    private final String scheduleDate;
    private final Boolean isEnabled;

    public Schedules(String scheduleName, String scheduleTime, String scheduleDate, Boolean isEnabled){

        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
        this.scheduleDate = scheduleDate;
        this.isEnabled = isEnabled;

    }

    public String getScheduleName() {
        return scheduleName;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }
}
