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
//import static tl.com.weatherapp.WeatherWidget.ACTION_SEND_REQUEST_FROM_WIDGET;
//import static tl.com.weatherapp.common.Common.ACTION_SEND_REQUEST_FROM_FRAGMENT;
//import static tl.com.weatherapp.common.Common.ACTION_SEND_RESPONSE_FROM_BROADSCAST;
//
//public class WeatherBroadcastReceiver extends BroadcastReceiver {
//    CompositeDisposable compositeDisposable;
//    IOpenWeatherMap mService;
//    Context context;
//    int appWidgetId ;
//    public WeatherBroadcastReceiver() {
//        compositeDisposable = new CompositeDisposable();
//        Retrofit retrofit = RetrofitClient.getInstance();
//        mService = retrofit.create(IOpenWeatherMap.class);
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        this.context = context;
//        try {
//            if (intent.getAction().equals(ACTION_SEND_REQUEST_FROM_FRAGMENT)) {
//                getWeatherInformation();
//            }else if(intent.getAction().equals(ACTION_SEND_REQUEST_FROM_WIDGET)){
//                appWidgetId = intent.getIntExtra("appWidgetId",0);
//                getWeatherInformation();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void getWeatherInformation() {
//        compositeDisposable.add(mService.getWeatherByLatIng(
//                Common.APP_ID
//                , String.valueOf(Common.current_location.getLatitude())
//                , String.valueOf(Common.current_location.getLongitude()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<WeatherResult>() {
//                    @RequiresApi(api = Build.VERSION_CODES.N)
//                    @Override
//                    public void accept(WeatherResult weatherResult) throws Exception {
//                        Intent local = new Intent();
//                        local.setAction(ACTION_SEND_RESPONSE_FROM_BROADSCAST);
//                        local.putExtra(ACTION_SEND_RESPONSE_FROM_BROADSCAST, weatherResult);
//                        context.sendBroadcast(local);
//
//                        updateWeatherWidget(weatherResult);
//
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                    }
//                }));
//    }
//
//    private void updateWeatherWidget(WeatherResult weatherResult) {
//        AppWidgetManager appWidgetManager =  AppWidgetManager.getInstance(context);
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
//        String temp = "null";
//        if(weatherResult != null) {
//            temp = Common.covertFtoC(weatherResult.getCurrently().getTemperature()) + "˚";
//        }
//        views.setTextViewText(R.id.tv_temp, temp);
//
//        Picasso.get().load(new StringBuilder("https://darksky.net/images/weather-icons/")
//                .append(weatherResult.getCurrently().getIcon())
//                .append(".png").toString()).into(views, R.id.img_icon,new int[] { appWidgetId });
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//    }
//}
