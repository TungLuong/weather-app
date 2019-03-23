package tl.com.weatherapp;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.WeatherResult;
import tl.com.weatherapp.retrofit.IOpenWeatherMap;
import tl.com.weatherapp.retrofit.RetrofitClient;

import static tl.com.weatherapp.common.Common.ACTION_UPDATE_CONFIG_WEATHER;
import static tl.com.weatherapp.common.Common.ACTION_SEND_REQUEST_FROM_FRAGMENT;
import static tl.com.weatherapp.common.Common.ACTION_RECEIVER_RESPONSE_FROM_WIDGET;
import static tl.com.weatherapp.common.Common.CURRENT_ADDRESS_ID;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    private static final int UPDATE_ALL_WIDGET = -1;
    private static Context context;
    private MyLocation myLocation;
    static SharedPreferences sharedPreferences;
    private List<WeatherResult> weatherResultList = new ArrayList<>();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Common.DATA, Context.MODE_PRIVATE);
        for (int appWidgetId : appWidgetIds) {
            int addressID = sharedPreferences.getInt("WIDGET_ADDRESS_ID" + appWidgetId, -1);
            if (addressID == CURRENT_ADDRESS_ID) {
                myLocation = new MyLocation();
                myLocation.getDeviceLocation(addressID);
            } else if (addressID != -1) {
                float lat = sharedPreferences.getFloat("LAT" + addressID, 0f);
                float lng = sharedPreferences.getFloat("LNG" + addressID, 0f);
                String address = sharedPreferences.getString("ADDRESS_NAME" + addressID, "unknown");
                // lang phi tai nguyen
                getWeatherInformation(lat, lng, addressID, address, appWidgetId);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
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
                int addressID = intent.getIntExtra(Common.ADDRESS_ID, -1);
                int x = addressID;
                if (addressID == CURRENT_ADDRESS_ID) {
                    myLocation = new MyLocation();
                    myLocation.getDeviceLocation(UPDATE_ALL_WIDGET);
                } else if (addressID != -1) {
                    sharedPreferences = context.getSharedPreferences(Common.DATA, Context.MODE_PRIVATE);
                    float lat = sharedPreferences.getFloat("LAT" + addressID, 0f);
                    float lng = sharedPreferences.getFloat("LNG" + addressID, 0f);
                    String address = sharedPreferences.getString("ADDRESS_NAME" + addressID, "unknown");
                    getWeatherInformation(lat, lng, addressID, address, UPDATE_ALL_WIDGET);
                }
            } else if (intent.getAction().equals(ACTION_UPDATE_CONFIG_WEATHER)) {
                int appWidgetId = intent.getIntExtra("APP_WIDGET_ID", -1);
                sharedPreferences = context.getSharedPreferences(Common.DATA, Context.MODE_PRIVATE);
                if (appWidgetId != -1) {
                    int addressID = sharedPreferences.getInt("WIDGET_ADDRESS_ID" + appWidgetId, -1);
                    if (addressID == CURRENT_ADDRESS_ID) {
                        myLocation = new MyLocation();
                        myLocation.getDeviceLocation(appWidgetId);
                    } else if (addressID != -1) {
                        float lat = sharedPreferences.getFloat("LAT" + addressID, 0f);
                        float lng = sharedPreferences.getFloat("LNG" + addressID, 0f);
                        String address = sharedPreferences.getString("ADDRESS_NAME" + addressID, "unknown");
                        getWeatherInformation(lat, lng, addressID, address, appWidgetId);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        Toast.makeText(context,intent.getAction(),Toast.LENGTH_LONG);
    }

    private static void getWeatherInformation(final float lat, final float lng, final int addressId, String address, int appWidgetId) {

        Retrofit retrofit = RetrofitClient.getInstance();
        IOpenWeatherMap mService = retrofit.create(IOpenWeatherMap.class);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mService.getWeatherByLatIng(
                Common.WEATHER_API_KEY
                , String.valueOf(lat)
                , String.valueOf(lng))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        weatherResult.setAddress(address);
                        Intent local = new Intent();
                        local.setAction(ACTION_RECEIVER_RESPONSE_FROM_WIDGET);
                        local.putExtra(Common.ADDRESS_ID, addressId);
                        local.putExtra(Common.WEATHER_RESULT, weatherResult);
                        context.sendBroadcast(local);
                        updateWeatherWidget(weatherResult, addressId, appWidgetId);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Intent local = new Intent();
                        local.setAction(ACTION_RECEIVER_RESPONSE_FROM_WIDGET);
                        local.putExtra(Common.ADDRESS_ID, addressId);
                        local.putExtra(Common.WEATHER_RESULT, (Bundle) null);
                        context.sendBroadcast(local);
                    }
                }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void updateWeatherWidget(WeatherResult weatherResult, int addressID, int appWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        sharedPreferences = context.getSharedPreferences(Common.DATA, Context.MODE_PRIVATE);
        if (appWidgetId == UPDATE_ALL_WIDGET) {
            ComponentName name = new ComponentName(context.getPackageName(), WeatherWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);
            for (int id : appWidgetIds) {
                int wgAddressId = sharedPreferences.getInt("WIDGET_ADDRESS_ID" + id, -1);
                if (weatherResult != null && wgAddressId != -1 && wgAddressId == addressID) {
                    String temp = Common.covertFtoC(weatherResult.getCurrently().getTemperature()) + "˚";
                    Picasso.get().load(new StringBuilder("https://darksky.net/images/weather-icons/")
                            .append(weatherResult.getCurrently().getIcon())
                            .append(".png").toString()).into(views, R.id.img_icon, new int[]{id});
                    views.setTextViewText(R.id.tv_temp, temp);
                    String lastTimeUpdate = "Last update " + Common.convertUnixToTime(weatherResult.getCurrently().getTime());
                    views.setTextViewText(R.id.tv_time_update, lastTimeUpdate);
                    String lastLocation = weatherResult.getAddress();
                    views.setTextViewText(R.id.tv_location, lastLocation);

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("ADDRESS_ID", addressID);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, 0);
                    views.setOnClickPendingIntent(R.id.linear_layout, pendingIntent);
                    appWidgetManager.updateAppWidget(id, views);
                }
            }
        } else {
            if (weatherResult != null) {
                String temp = Common.covertFtoC(weatherResult.getCurrently().getTemperature()) + "˚";
                Picasso.get().load(new StringBuilder("https://darksky.net/images/weather-icons/")
                        .append(weatherResult.getCurrently().getIcon())
                        .append(".png").toString()).into(views, R.id.img_icon, new int[]{appWidgetId});
                views.setTextViewText(R.id.tv_temp, temp);
                String lastTimeUpdate = "Last update " + Common.convertUnixToTime(weatherResult.getCurrently().getTime());
                views.setTextViewText(R.id.tv_time_update, lastTimeUpdate);
                String lastLocation = weatherResult.getAddress();
                views.setTextViewText(R.id.tv_location, lastLocation);

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("ADDRESS_ID", addressID);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
                views.setOnClickPendingIntent(R.id.linear_layout, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }

    }

    public static class MyLocation {
        private FusedLocationProviderClient mFusedLocationProviderClient;
        private LocationCallback locationCallback;
        private LocationRequest locationRequest;


//        public void getLocation(Context mContext){
//            buildLocationRequest();
//            buildLocationCallBack();
//            fusedLocationProviderClient = new FusedLocationProviderClient(mContext);
//            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return ;
//            }
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//        }
//
//        private void buildLocationCallBack() {
//            locationCallback = new LocationCallback(){
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//                    super.onLocationResult(locationResult);
//                    Location location = locationResult.getLastLocation();
//                    getWeatherInformation((float) location.getLatitude(),(float) location.getLongitude(),Common.CURRENT_ADDRESS_ID);
//                }
//            };
//
//        }
//
//        private void buildLocationRequest() {
//            locationRequest = new LocationRequest();
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setInterval(5000);
//            locationRequest.setFastestInterval(3000);
//            locationRequest.setSmallestDisplacement(10.0f);
//
//        }

        private void getDeviceLocation(int appWidgetId) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location curLocation = task.getResult();
                            float cLat = 0;
                            float cLng = 0;
                            String cAddress = "";
                            sharedPreferences = context.getSharedPreferences(Common.DATA, Context.MODE_PRIVATE);
                            if (curLocation != null) {
                                cLat = (float) curLocation.getLatitude();
                                cLng = (float) curLocation.getLongitude();
                                cAddress = getAddress(cLat, cLng);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("LAT" + CURRENT_ADDRESS_ID);
                                editor.remove("LNG" + CURRENT_ADDRESS_ID);
                                editor.remove("ADDRESS_NAME" + CURRENT_ADDRESS_ID);
                                editor.putFloat("LAT" + CURRENT_ADDRESS_ID, cLat);
                                editor.putFloat("LNG" + CURRENT_ADDRESS_ID, cLng);
                                editor.putString("ADDRESS_NAME" + CURRENT_ADDRESS_ID, cAddress);
                                editor.commit();
                            } else {
                                cLat = sharedPreferences.getFloat("LAT" + CURRENT_ADDRESS_ID, 0f);
                                cLng = sharedPreferences.getFloat("LNG" + CURRENT_ADDRESS_ID, 0f);
                                cAddress = sharedPreferences.getString("ADDRESS_NAME" + CURRENT_ADDRESS_ID, "unknown");
                            }
                            getWeatherInformation(cLat, cLng, CURRENT_ADDRESS_ID, cAddress, appWidgetId);
                        } else {
                        }
                    }
                });
            } catch (final Exception e) {
            }

        }

    }

    private static String getAddress(float lat, float lng) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                if (addresses.get(0).getLocality() == null) {
                    return "unknown";
                }
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown";
    }


}

