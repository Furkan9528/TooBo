package com.example.weatherapp.Common;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    //f5af57257becd806859c81cf4f039aa1
    public static String API_KEY ="d293fd094ba23743cd8205c2ea54f25f";
    public static String API_LINK= "http://api.openweathermap.org/data/2.5/weather";



    @NonNull
    public static String apiRequest(String lat,String lon){
        StringBuilder sb= new StringBuilder(API_LINK);
        sb.append(String.format("?lat=%s&lon=%s&appid=%s&units=metric",lat,lon,API_KEY));
        return sb.toString();
    }

    public static String unixTimeStampToDateTime(double unixTimeStamp){
        DateFormat dateFormat=new SimpleDateFormat("HH:mm");
        Date date=new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateFormat.format(date);
    }

    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png",icon);
    }
    public static String getDateNow(){
        DateFormat dateFormat=new SimpleDateFormat("dd MM yyyy HH:mm");
        Date date=new Date();
        return dateFormat.format(date);
    }

}
