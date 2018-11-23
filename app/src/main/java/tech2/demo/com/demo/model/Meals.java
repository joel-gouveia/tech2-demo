package tech2.demo.com.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Calendar;


/**
 * Created by Joel on 27-Jan-16.
 */
public class Meals {
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String id;
    private int numberCalories;
    private String description;
    private String date;
    private Calendar calendarDate;
    private String time;

    public Meals(){
        numberCalories = 0;
        description = "";
        date = "";
        time = "";
        calendarDate = Calendar.getInstance();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumberCalories() {
        return numberCalories;
    }

    public void setNumberCalories(int numberCalories) {
        this.numberCalories = numberCalories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        dateToCalendar(date);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        timeToCalendar(time);
    }

    public Calendar getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(Calendar calendarDate) {
        this.calendarDate = calendarDate;
    }

    public void dateToCalendar(String date){
        String[] parse = date.split("/");
        Calendar cal = calendarDate;

        cal.set(Integer.parseInt(parse[2]), Integer.parseInt(parse[1]) - 1, Integer.parseInt(parse[0]));

        calendarDate = cal;
    }

    public void timeToCalendar(String time){
        String[] parse = time.split(":");
        Calendar cal = calendarDate;

        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                Integer.parseInt(parse[0]),
                Integer.parseInt(parse[1]));

        calendarDate = cal;
    }
}
