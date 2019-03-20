package tl.com.weatherapp.common;

import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;

public class Common {
    public static final String WEATHER_API_KEY = "015f333c2ca5576208dd63d15a91c5ba";
    public static final String GOOGLE_MAP_API_KEY = "AIzaSyAsvRrYBZ770RpBDQNsbaOCsz3XZjD9Vc8";
    public static final String COUNT_ADDRESS = "COUNT_ADDRESS" ;
    public static final String WEATHER_RESULT = "WEATHER_RESULT";
    public static final String DATA = "DATA";
    public static final int COUNT_CURRENT_ADDRESS = 0;
    public static final String LIST_WEATHER_RESULT = "LIST_WEATHER_RESULT";

    //    public static Location current_location = new Location("");
    public static String ACTION_SEND_REQUEST_FROM_FRAGMENT = "ACTION_SEND_REQUEST_FROM_FRAGMENT";
    public static String ACTION_RECEIVER_RESPONSE_FROM_WIDGET = "ACTION_RECEIVER_RESPONSE_FROM_WIDGET";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToDate(int dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
        String strDate = simpleDateFormat.format(date);
        return strDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToTime(int dt) {
        Date time = new Date(dt * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" HH:mm");
        String strTime = simpleDateFormat.format(time);
        return strTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToDay(int dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE");
        String strDate = simpleDateFormat.format(date);
        return strDate;
    }

    public static int covertFtoC(double temperature) {
        double temp = (temperature - 32) / 1.8;
        return (int) temp;
    }
}
