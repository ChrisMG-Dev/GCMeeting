package com.guide.cordobatourplus;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chris on 28/05/2015.
 */
public class Noticia implements Comparable<Noticia>{
    private String date;
    private String time;
    private String info;
    private String author;

    public Noticia(String date, String time, String info, String author) {
        this.date = date;
        this.time = time;
        this.info = info;
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public int compareTo(Noticia another) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaEvento = null;
        Date fechaOtro = null;
        try {
            fechaEvento = df.parse(getDate());
            fechaOtro = df.parse(another.getDate());
        } catch (Exception e) {
            Log.e("Error fecha", e.getMessage());
        }
        if (fechaOtro.after(fechaEvento)) {
            return 1;
        } else if (fechaOtro.before(fechaEvento)) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return getDate() + getTime() + getInfo() + getAuthor();
    }
}
