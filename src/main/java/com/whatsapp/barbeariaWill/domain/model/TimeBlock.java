package com.whatsapp.barbeariaWill.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeBlock {
    private String id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;

    public TimeBlock(String id, LocalDate date, LocalTime startTime, LocalTime endTime, String reason) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getReason() {
        return reason;
    }
} 