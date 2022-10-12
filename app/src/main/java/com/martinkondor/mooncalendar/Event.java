package com.martinkondor.mooncalendar;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class for the events table in the database
 */
public class Event {

    private int id;
    private int year;
    private int month;
    private int day;
    private String time;
    private String title;
    private String desc;

    /**
     * Empty constructor, to set variables by hand
     */
    public Event() {
        // Call the other constructor
        this(-1, 0, 0, 0, "", "", "");
    }

    /**
     * @param id Database Id of the event
     * @param year Year of the event
     * @param month Month of the event
     * @param day Day of the month
     * @param time In format: hh:mm - hh:mm
     * @param title Event's title
     * @param desc Event's description
     */
    public Event(int id, int year, int month, int day, String time, String title, String desc) {
        // Call the other constructor
        this(year, month, day, time, title, desc);
        this.id = id;
    }

    /**
     * Constructor without the ID param, this case
     * the Event's ID will be set to -1 by default
     *
     * @param year Year of the event
     * @param month Month of the event
     * @param day Day of the month
     * @param time In format: hh:mm - hh:mm
     * @param title Event's title
     * @param desc Event's description
     */
    public Event(int year, int month, int day, String time, String title, String desc) {
        this.id = -1;
        this.year = year;
        this.month = month;
        this.day = day;
        this.time = time;
        this.title = title;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean hasNull() {
        // this.desc can be ""
        return this.id == -1 ||
                this.title.length() == 0 ||
                this.time.length() == 0 ||
                this.day == 0 ||
                this.year == 0 ||
                this.month == 0;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
