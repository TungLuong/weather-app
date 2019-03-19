//package tl.com.weatherapp;
//
//import android.appwidget.AppWidgetManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.widget.RemoteViews;
//import android.widget.Toast;
//
//import com.squareup.picasso.Picasso;
//
//import java.io.Serializable;
//
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//import retrofit2.Retrofit;
//import tl.com.weatherapp.common.Common;
//import tl.com.weatherapp.model.WeatherResult;
//import tl.com.weatherapp.retrofit.IOpenWeatherMap;
//import tl.com.weatherapp.retrofit.RetrofitClient;
//
//
//public class InternetBroadcastReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
//            WeatherWidget.MyLocation myLocation = new WeatherWidget.MyLocation();
//            myLocation.getLocation(context);
//        }
//    }
//
//}
