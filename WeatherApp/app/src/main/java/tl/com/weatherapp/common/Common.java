package tl.com.weatherapp.common;

import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;

public class Common {
    public static final String APP_ID = "015f333c2ca5576208dd63d15a91c5ba";
    public static Location current_location = new Location("");
    public static String ACTION_SEND_REQUEST_FROM_FRAGMENT = "ACTION_SEND_REQUEST_FROM_FRAGMENT";
    public static String ACTION_SEND_RESPONSE_FROM_WIDGET = "ACTION_SEND_RESPONSE_FROM_WIDGET";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToDate(int dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        String strDate = simpleDateFormat.format(date);
        return strDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToTime(int dt) {
        Date time = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" HH:mm:ss");
        String strTime = simpleDateFormat.format(time);
        return strTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToDay(int dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE");
        String strDate = simpleDateFormat.format(date);
        return strDate;
    }

    public static int covertFtoC(double temperature) {
        double temp = (temperature - 32)/1.8;
        return (int) temp;
    }
}
