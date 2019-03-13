package tl.com.weatherapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.WeatherResult;
import tl.com.weatherapp.retrofit.IOpenWeatherMap;
import tl.com.weatherapp.retrofit.RetrofitClient;

import static tl.com.weatherapp.common.Common.ACTION_SEND_REQUEST_FROM_FRAGMENT;
import static tl.com.weatherapp.common.Common.ACTION_SEND_RESPONSE_FROM_WIDGET;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    private static Context context;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        this.context = context;
        getWeatherInformation();
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        this.context = context;
        try {
            if (intent.getAction().equals(ACTION_SEND_REQUEST_FROM_FRAGMENT)) {
                getWeatherInformation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getWeatherInformation() {
        Retrofit retrofit = RetrofitClient.getInstance();
        IOpenWeatherMap mService = retrofit.create(IOpenWeatherMap.class);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mService.getWeatherByLatIng(
                Common.APP_ID
                , String.valueOf(Common.current_location.getLatitude())
                , String.valueOf(Common.current_location.getLongitude()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        Intent local = new Intent();
                        local.setAction(ACTION_SEND_RESPONSE_FROM_WIDGET);
                        local.putExtra(ACTION_SEND_RESPONSE_FROM_WIDGET, weatherResult);
                        context.sendBroadcast(local);
                        updateWeatherWidget(weatherResult);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }

    private static void updateWeatherWidget(WeatherResult weatherResult) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context.getPackageName(), WeatherWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        for (int appWidgetId : appWidgetIds) {
            if (weatherResult != null) {
                String temp = Common.covertFtoC(weatherResult.getCurrently().getTemperature()) + "˚";
                Picasso.get().load(new StringBuilder("https://darksky.net/images/weather-icons/")
                        .append(weatherResult.getCurrently().getIcon())
                        .append(".png").toString()).into(views, R.id.img_icon, new int[]{appWidgetId});
                views.setTextViewText(R.id.tv_temp, temp);
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

}

