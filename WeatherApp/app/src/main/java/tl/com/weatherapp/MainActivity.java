package tl.com.weatherapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import tl.com.weatherapp.adapter.WeatherFragmentAdapter;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.ListWeatherResult;
import tl.com.weatherapp.model.WeatherResult;

import static tl.com.weatherapp.common.Common.ACTION_SEND_REQUEST_FROM_FRAGMENT;
import static tl.com.weatherapp.common.Common.ACTION_SEND_RESPONSE_FROM_WIDGET;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AlertDialog dialogInternet;

    private BroadcastReceiver UIBroadcastReceiver;
    private SwipeRefreshLayout refreshLayout;
    public static final String TAG = MainActivity.class.getSimpleName();
    private int totalAddress = 0;
    List<WeatherResult> listWeatherResults = new ArrayList<>();
    List<Boolean> isReceiver = new ArrayList<>();
    private WeatherFragmentAdapter adapter;

    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.view_pager_weather_fragment);

        sharedPreferences = getSharedPreferences(Common.DATA,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("TOTAL_ADDRESS", 10);
//        editor.commit();
//        //address 1
//        editor.putFloat("LAT1",41.11f);
//        editor.putFloat("LNG1",114.11f);
//        //address 2
//        editor.putFloat("LAT2",52.11f);
//        editor.putFloat("LNG2",24.11f);
//        //address 3
//        editor.putFloat("LAT3",13.45f);
//        editor.putFloat("LNG3",100.31f);
//        //address 4
//        editor.putFloat("LAT4",40.71f);
//        editor.putFloat("LNG4",74.00f);
//        //address 5
//        editor.putFloat("LAT5",39.57f);
//        editor.putFloat("LNG5",116.23f);
//        //address 6
//        editor.putFloat("LAT6",10.46f);
//        editor.putFloat("LNG6",106.40f);
//        //address 7
//        editor.putFloat("LAT7",41.54f);
//        editor.putFloat("LNG7",12.30f);
//        //address 8
//        editor.putFloat("LAT8",35.41f);
//        editor.putFloat("LNG8",139.41f);
//        editor.commit();

        totalAddress = sharedPreferences.getInt("TOTAL_ADDRESS",1);
        int x = totalAddress;


        //Request permission

        Dexter.withActivity(this).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                            init();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Snackbar.make(collapsingToolbarLayout,"Permission denied", Snackbar.LENGTH_LONG).show();
                    }
                }).check();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        if (!isConnected(MainActivity.this)) {
            dialogInternet = buildDialog(MainActivity.this).show();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SEND_RESPONSE_FROM_WIDGET);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        UIBroadcastReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_SEND_RESPONSE_FROM_WIDGET)) {
                     WeatherResult weatherResult = ( WeatherResult ) intent.getSerializableExtra(Common.WEATHER_RESULT);
                     int countAddress = intent.getIntExtra(Common.COUNT_ADDRESS,0);
                     if(!isReceiver.get(countAddress)) {
                         isReceiver.set(countAddress,true);
                         listWeatherResults.set(countAddress, weatherResult);
                         adapter.setWeatherResultList(listWeatherResults);
                         adapter.notifyDataSetChanged();
                     }


                } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    if (isConnected(MainActivity.this)) {
                        if (dialogInternet != null) {
                            dialogInternet.cancel();
                        }
                        listWeatherResults = new ArrayList<>();
                        isReceiver = new ArrayList<>();
                        for(int i = 0; i< totalAddress ; i++) {
                            listWeatherResults.add(null);
                            isReceiver.add(false);
                            sendRequestGetWeatherInfo(i);
                        }
                    }

                }
            }
        };

        registerReceiver(UIBroadcastReceiver, filter);
        listWeatherResults = new ArrayList<>();
        for(int i = 0; i< totalAddress ; i++) {
            listWeatherResults.add(null);
            isReceiver.add(false);
            sendRequestGetWeatherInfo(i);
        }

//        refreshLayout = findViewById(R.id.refresh_layout);
//        refreshLayout.setRefreshing(true);
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                sendRequestGetWeatherInfo();
//            }
//        });

        adapter = new WeatherFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        final CircleIndicator indicator = findViewById(R.id.circle_indicator);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());

        ImageView btnOpenWeatherAddressActivity = findViewById(R.id.btn_open_weather_address_activity);
        btnOpenWeatherAddressActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i =0; i< totalAddress ;i++){
                    if (!isReceiver.get(i)) return;
                }
                Intent intent = new Intent(MainActivity.this,WeatherAddressActivity.class);
                ListWeatherResult listWeatherResult = new ListWeatherResult();
                listWeatherResult.setWeatherResultList(listWeatherResults);
                intent.putExtra(Common.LIST_WEATHER_RESULT, listWeatherResult);
                startActivity(intent);
            }
        });
    }

    public void sendRequestGetWeatherInfo(int countAddress) {
        if(isConnected(this)) {
            Intent intent = new Intent(MainActivity.this, WeatherWidget.class);
            intent.setAction(ACTION_SEND_REQUEST_FROM_FRAGMENT);
            intent.putExtra(Common.COUNT_ADDRESS, countAddress);
            sendBroadcast(intent);
        }
    }

//    private void openTodayWeatherFragment() {
//            Fragment newFragment = WeatherFragment.getInstance();
//            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.root_view, newFragment);
//            transaction.commitAllowingStateLoss();
//
//    }

    @Override
    protected void onPause() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments){
            fragment.onPause();
        }
        super.onPause();
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this.");

        builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS );
                startActivity(intent);

            }
        });

        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        return builder;
    }

    public List<Boolean> getIsReceiver() {
        return isReceiver;
    }

    public void setIsReceiver(List<Boolean> isReceiver) {
        this.isReceiver = isReceiver;
    }
}
