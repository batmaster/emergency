package com.example.application.emergency.services;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by batmaster on 4/16/2017 AD.
 */

public class ReadableTime {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String get(String time) {
        String rt = "";

        try {
            Date d = SDF.parse(time);
            Date now = new Date();

            long diff = (now.getTime() - d.getTime()) / 1000;
            long seconds = diff % 60;
            diff /= 60;

            long minutes = diff % 60;
            diff /= 60;

            long hours = diff % 24;
            diff /= 24;

            long days = diff % 24;

            Log.d("ReadableTime", days + " " + hours + " " + minutes + " " + seconds) ;

            if (days > 0) {
                rt += days + " วัน";
                if (hours > 0) {
                    rt += " " + hours + " ชั่วโมง";
                }
            }
            else {
                if (hours > 0) {
                    rt += hours + " ชั่วโมง";
                    if (minutes > 0) {
                        rt += " " + minutes + " นาที";
                    }
                }
                else {
                    if (minutes > 0) {
                        rt += minutes + " นาที";
                        if (seconds > 0) {
                            rt += " " + seconds + " วินาที";
                        }
                    }
                    else {
                        rt += seconds + " วินาที";
                    }
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
            rt =  "ERROR";
        }

        return rt;
    }

}
