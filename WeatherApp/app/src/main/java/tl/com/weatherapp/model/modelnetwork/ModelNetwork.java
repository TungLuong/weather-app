package tl.com.weatherapp.model.modelnetwork;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import tl.com.weatherapp.presenter.weatheraddress.IWeatherAddressPresenter;
import tl.com.weatherapp.presenter.weatherhome.IWeatherHomePresenter;
import tl.com.weatherapp.retrofit.IOpenWeatherMap;
import tl.com.weatherapp.retrofit.RetrofitClient;
import tl.com.weatherapp.view.main.MainActivity;
import tl.com.weatherapp.R;
import tl.com.weatherapp.WeatherWidget;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.WeatherResult;
import tl.com.weatherapp.presenter.main.IMainPresenter;

import static android.content.Context.MODE_PRIVATE;
import static tl.com.weatherapp.common.Common.CURRENT_ADDRESS_ID;
import static tl.com.weatherapp.common.Common.NO_UPDATE_WIDGET;
import static tl.com.weatherapp.common.Common.SHARE_PREF_ADDRESS_ID_KEY_AT;
import static tl.com.weatherapp.common.Common.UPDATE_ALL_WIDGET;

public class ModelNetwork {
    private static ModelNetwork instance = new ModelNetwork();
    static SharedPreferences sharedPreferences;
    private static List<WeatherResult> listWeatherResults = new ArrayList<>();
    private static Context mContext;
    private static int totalAddress;
    private static boolean isMainActivityReceiver = false;
    private static int curPositionPager = 0;
    private MyLocation myLocation;
    private static IMainPresenter iMainPresenter;
    private static IWeatherHomePresenter iWeatherHomePresenter;
    private static IWeatherAddressPresenter iWeatherAddressPresenter;
    //  private IFindAddressPresenter iFindAddressPresenter;
    private static Gson gson;

    private AsyncTask<String, Void, Void> asyncGetWeatherResult;
//    private Executor executor;

    public static ModelNetwork getInstance() {
        return instance;
    }


    public void setiMainPresenter(IMainPresenter iMainPresenter) {
        this.iMainPresenter = iMainPresenter;
    }

    public void setiWeatherHomePresenter(IWeatherHomePresenter iWeatherHomePresenter) {
        this.iWeatherHomePresenter = iWeatherHomePresenter;
    }

    public void setiWeatherAddressPresenter(IWeatherAddressPresenter iWeatherAddressPresenter) {
        this.iWeatherAddressPresenter = iWeatherAddressPresenter;
    }

    public void create(Context context) {
        if (mContext == null) {
            this.mContext = context;
        }
        if (gson == null) {
            gson = new Gson();
        }
        sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);
    }

    public void loadDataForMainPresenter() {
        totalAddress = sharedPreferences.getInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, 1);

        listWeatherResults = new ArrayList<>();
        for (int i = 0; i < totalAddress; i++) {
            listWeatherResults.add(null);
            int addressId = sharedPreferences.getInt(SHARE_PREF_ADDRESS_ID_KEY_AT + i, CURRENT_ADDRESS_ID);
            updateInformationByAddressId(addressId, UPDATE_ALL_WIDGET);
        }
    }

    private void updateWeatherInformation(final float lat, final float lng, final int addressId, String address, int appWidgetId) {

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
                        try {
                            int position = 0;
                            for (int i = 0; i < totalAddress; i++) {
                                if (sharedPreferences.getInt(SHARE_PREF_ADDRESS_ID_KEY_AT + i, -1) == addressId) {
                                    position = i;
                                    break;
                                }
                            }
                            String strWeatherResult = gson.toJson(weatherResult);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Common.SHARE_PREF_WEATHER_KEY_AT + addressId, strWeatherResult);
                            editor.commit();
                            listWeatherResults.set(position, weatherResult);
                            if (!isMainActivityReceiver && position == curPositionPager) {
                                isMainActivityReceiver = true;
                                iMainPresenter.loadDataFinish();
                            }
                            if (iWeatherAddressPresenter != null) {
                                iWeatherAddressPresenter.notifyItemChange(position);
                            }
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        ;
                        updateWeatherWidget(weatherResult, addressId, appWidgetId);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        int position = 0;
                        for (int i = 0; i < totalAddress; i++) {
                            if (sharedPreferences.getInt(SHARE_PREF_ADDRESS_ID_KEY_AT + i, -1) == addressId) {
                                position = i;
                                break;
                            }
                        }

                        String strWeatherResult = sharedPreferences.getString(Common.SHARE_PREF_WEATHER_KEY_AT + addressId, "");
                        WeatherResult weatherResult = gson.fromJson(strWeatherResult, WeatherResult.class);
                        listWeatherResults.set(position, weatherResult);
                        if (position == curPositionPager) {
                            //isMainActivityReceiver = true;
                            iMainPresenter.loadDataFinish();
                        }
                    }
                }));

//        asyncGetWeatherResult = new AsyncTask<String, Void, Void>() {
//            @Override
//            protected Void doInBackground(String... strings) {
//                StringBuffer content = new StringBuffer();
//                try {
//                    URL url = new URL(strings[0]);
//                    InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream());
//                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                    String line = "";
//                    while ((line = bufferedReader.readLine()) != null) {
//                        content.append(line);
//                    }
//                    bufferedReader.close();
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                String s = content.toString();
//                int position = 0;
//                Toast.makeText(mContext, "Toast : " + s.toString(), Toast.LENGTH_SHORT);
//                if (s.equals("")) {
//                    WeatherResult weatherResult = gson.fromJson(s, WeatherResult.class);
//                    weatherResult.setAddress(address);
//                    try {
//                        for (int i = 0; i < totalAddress; i++) {
//                            if (sharedPreferences.getInt(SHARE_PREF_ADDRESS_ID_KEY_AT + i, -1) == addressId) {
//                                position = i;
//                                break;
//                            }
//                        }
//                        String strWeatherResult = gson.toJson(weatherResult);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString(Common.SHARE_PREF_WEATHER_KEY_AT + addressId, strWeatherResult);
//                        editor.commit();
//                        listWeatherResults.set(position, weatherResult);
//                        if (!isMainActivityReceiver && position == curPositionPager) {
//                            isMainActivityReceiver = true;
//                            iMainPresenter.loadDataFinish();
//                        }
//                        if (iWeatherAddressPresenter != null) {
//                            iWeatherAddressPresenter.notifyItemChange(position);
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                    ;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        updateWeatherWidget(weatherResult, addressId, appWidgetId);
//                    }
//                } else {
//                    for (int i = 0; i < totalAddress; i++) {
//                        if (sharedPreferences.getInt(SHARE_PREF_ADDRESS_ID_KEY_AT + i, -1) == addressId) {
//                            position = i;
//                            break;
//                        }
//                    }
//                    String strWeatherResult = sharedPreferences.getString(Common.SHARE_PREF_WEATHER_KEY_AT + addressId, "");
//                    WeatherResult weatherResult = gson.fromJson(strWeatherResult, WeatherResult.class);
//                    listWeatherResults.set(position, weatherResult);
//                    if (position == curPositionPager) {
//                        //isMainActivityReceiver = true;
//                        iMainPresenter.loadDataFinish();
//                    }
//                }
//                return null;
//            }
//        };
//
//        StringBuilder link = new StringBuilder("https://api.darksky.net/").append("forecast").append("/").append(Common.WEATHER_API_KEY).append("/").append(lat).append(",").append(lng);
//        asyncGetWeatherResult.execute(link.toString());


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void updateWeatherWidget(WeatherResult weatherResult, int addressID, int appWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.weather_widget);
        sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);
        if (appWidgetId == UPDATE_ALL_WIDGET) {
            ComponentName name = new ComponentName(mContext.getPackageName(), WeatherWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);
            for (int id : appWidgetIds) {
                int wgAddressId = sharedPreferences.getInt(Common.SHARE_PREF_WIDGET_ADDRESS_ID_KEY_AT + id, -1);
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

                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra(Common.INTENT_ADDRESS_ID, addressID);
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, id, intent, 0);
                    views.setOnClickPendingIntent(R.id.linear_layout, pendingIntent);
                    appWidgetManager.updateAppWidget(id, views);
                }
            }
        } else if (appWidgetId != Common.NO_UPDATE_WIDGET) {
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

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(Common.INTENT_ADDRESS_ID, addressID);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, appWidgetId, intent, 0);
                views.setOnClickPendingIntent(R.id.linear_layout, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }

    }

    public void updateWidgetAndInformation(int appWidgetId) {
        int addressID = sharedPreferences.getInt(Common.SHARE_PREF_WIDGET_ADDRESS_ID_KEY_AT + appWidgetId, -1);
        // neu la location thi kiem tra toa do hien gio
        updateInformationByAddressId(addressID, appWidgetId);

    }

    public void updateInformationByAddressId(int addressID, int appWidgetId) {
        if (addressID == CURRENT_ADDRESS_ID) {
            myLocation = new MyLocation();
            myLocation.updateWeatherDeviceLocation(appWidgetId);
        } else if (addressID != -1) {
            // cap nhat weatherResult va widget
            float lat = sharedPreferences.getFloat(Common.SHARE_PREF_LAT_KEY_AT + addressID, 0f);
            float lng = sharedPreferences.getFloat(Common.SHARE_PREF_LNG_KEY_AT + addressID, 0f);
            String address = sharedPreferences.getString(Common.SHARE_PREF_ADDRESS_NAME_KEY_AT + addressID, "unknown");
            updateWeatherInformation(lat, lng, addressID, address, appWidgetId);
        }
    }


    public void getResultWeatherForWeatherHomePresenter() {
        iWeatherHomePresenter.getWeatherResult(listWeatherResults);
        iWeatherHomePresenter.setCurrPositionPagerForView(curPositionPager);
    }


    public void getResultWeatherForWeatherAddressPresenter() {
        iWeatherAddressPresenter.getWeatherResult(listWeatherResults);
    }

    public void deleteItem(int position) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);
        int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + position, -1);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(Common.SHARE_PREF_LAT_KEY_AT + addressId);
        editor.remove(Common.SHARE_PREF_LAT_KEY_AT + addressId);
        editor.remove(Common.SHARE_PREF_ADDRESS_NAME_KEY_AT + addressId);
        editor.remove(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + position);

        for (int i = position; i < listWeatherResults.size() - 1; i++) {
//            editor.remove("INTENT_ADDRESS_ID"+i);
            int newAddressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + (i + 1), -1);
            editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + i, newAddressId);
        }
        int totalAddress = sharedPreferences.getInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, 1);
        editor.putInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, totalAddress - 1);
        editor.commit();

//        editor = sharedPreferences.edit();
        listWeatherResults.remove(position);
//        ((MainActivity) getActivity()).getIsReceiver().remove(position);
//        ((MainActivity) getActivity()).setTotalAddress(totalAddress - 1);
//        for (int i = position; i < weatherResultList.size(); i++) {
//            editor.putFloat("LAT" + i, (float) weatherResultList.get(i).getLatitude());
//            editor.putFloat("LNG" + i, (float) weatherResultList.get(i).getLongitude());
//            editor.putString("ADDRESS_NAME" + i, (String) weatherResultList.get(i).getAddress());
//        }
//        editor.commit();
    }

    public void moveItem(int oldPo, int newPo) {
        WeatherResult weatherResult = listWeatherResults.remove(oldPo);
        listWeatherResults.add(newPo, weatherResult);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);
        int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + oldPo, -1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (newPo < oldPo) {
            for (int i = newPo; i < oldPo; i++) {
                int newAddressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + i, -1);
                editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + (i + 1), newAddressId);
            }
            editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + newPo, addressId);
        } else if (newPo > oldPo) {
            for (int i = newPo; i > oldPo; i--) {
                int newAddressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + i, -1);
                editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + (i - 1), newAddressId);
            }
            editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + newPo, addressId);
        }
        editor.commit();
    }

    public void addAddress(Place place) {
        LatLng latLng = place.getLatLng();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);

        int maxId = sharedPreferences.getInt(Common.SHARE_PREF_MAX_ID_KEY, 0);
        int newId = maxId + 1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + totalAddress, newId);

        Float lat = (float) latLng.latitude;
        Float lng = (float) latLng.longitude;
        String addressName = String.valueOf(place.getName());
        editor.putFloat(Common.SHARE_PREF_LAT_KEY_AT + newId, lat);
        editor.putFloat(Common.SHARE_PREF_LNG_KEY_AT + newId, lng);
        editor.putString(Common.SHARE_PREF_ADDRESS_NAME_KEY_AT + newId, addressName);

        totalAddress++;
        editor.putInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, totalAddress);
        editor.putInt(Common.SHARE_PREF_MAX_ID_KEY, newId);
        editor.commit();

        listWeatherResults.add(null);
        updateWeatherInformation(lat, lng, newId, addressName, NO_UPDATE_WIDGET);
    }

    public void setCurrentPagerByAddressId(int addressId) {
        for (int position = 0; position < totalAddress; position++) {
            if (sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + position, -1) == addressId)
                curPositionPager = position;
        }
    }

    public void setCurrentPager(int positionPager) {
        curPositionPager = positionPager;
    }

    public void updateInformationByPosition(int position) {
        int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + position, -1);
        updateInformationByAddressId(addressId, UPDATE_ALL_WIDGET);
        iWeatherHomePresenter.notifyItemChange(position);
    }

    public void isNotReceiver() {
        isMainActivityReceiver = false;
    }

    class MyLocation {
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
//                    updateWeatherInformation((float) location.getLatitude(),(float) location.getLongitude(),Common.CURRENT_ADDRESS_ID);
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

        private void updateWeatherDeviceLocation(int appWidgetId) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
            try {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            sharedPreferences = mContext.getSharedPreferences(Common.DATA, Context.MODE_PRIVATE);
                            if (curLocation != null) {
                                cLat = (float) curLocation.getLatitude();
                                cLng = (float) curLocation.getLongitude();
                                cAddress = getAddress(cLat, cLng);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(SHARE_PREF_ADDRESS_ID_KEY_AT + 0, CURRENT_ADDRESS_ID);
                                editor.putFloat(Common.SHARE_PREF_LAT_KEY_AT + CURRENT_ADDRESS_ID, cLat);
                                editor.putFloat(Common.SHARE_PREF_LNG_KEY_AT + CURRENT_ADDRESS_ID, cLng);
                                editor.putString(Common.SHARE_PREF_ADDRESS_NAME_KEY_AT + CURRENT_ADDRESS_ID, cAddress);
                                editor.commit();
                            } else {
                                cLat = sharedPreferences.getFloat(Common.SHARE_PREF_LAT_KEY_AT + CURRENT_ADDRESS_ID, 0f);
                                cLng = sharedPreferences.getFloat(Common.SHARE_PREF_LNG_KEY_AT + CURRENT_ADDRESS_ID, 0f);
                                cAddress = sharedPreferences.getString(Common.SHARE_PREF_ADDRESS_NAME_KEY_AT + CURRENT_ADDRESS_ID, "unknown");
                            }
                            updateWeatherInformation(cLat, cLng, CURRENT_ADDRESS_ID, cAddress, appWidgetId);
                        } else {
                        }
                    }
                });
            } catch (final Exception e) {
            }

        }

        private String getAddress(float lat, float lng) {
            Geocoder geocoder = new Geocoder(mContext);
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

}
