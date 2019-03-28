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
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.victor.loading.book.BookLoading;
import com.victor.loading.newton.NewtonCradleLoading;

import java.util.ArrayList;
import java.util.List;

import tl.com.weatherapp.base.BaseActivity;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.WeatherResult;

import static tl.com.weatherapp.common.Common.ACTION_GET_WEATHER_RESULT_BY_ADDRESS_ID;
import static tl.com.weatherapp.common.Common.ACTION_RECEIVER_RESPONSE_FROM_WIDGET;
import static tl.com.weatherapp.common.Common.CURRENT_ADDRESS_ID;
import static tl.com.weatherapp.common.Common.SHARE_PREF_ADDRESS_ID_KEY_AT;

public class MainActivity extends BaseActivity {

    private AlertDialog dialogInternet;
    private AlertDialog dialogLocation;

    private BroadcastReceiver UIBroadcastReceiver;

    public static final String TAG = MainActivity.class.getSimpleName();
    private int totalAddress = 0;
    private int positionPager = CURRENT_ADDRESS_ID;
    private List<WeatherResult> listWeatherResults = new ArrayList<>();
    private List<Boolean> isReceiver = new ArrayList<>();
    private RelativeLayout loadingView;

    private boolean fragmentHomeIsVisible = false;
    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Common.DATA, MODE_PRIVATE);
        totalAddress = sharedPreferences.getInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, 1);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int addressId = extras.getInt(Common.INTENT_ADDRESS_ID);
            for (int position = 0; position < totalAddress; position++) {
                if (sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + position, -1) == addressId) positionPager = position;
            }
        }
        setContentView(R.layout.activity_main);
        initView();
        //Request permission
        Dexter.withActivity(this).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            init();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();
    }

    private void initView() {
        loadingView = findViewById(R.id.loading_view);
        loadingView.setVisibility(View.VISIBLE);
        BookLoading bookLoading = findViewById(R.id.bookloading);
        bookLoading.start();
        NewtonCradleLoading newtonCradleLoading = findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();
    }


    private void init() {
        if (!isConnected(MainActivity.this)) {
            dialogInternet = buildDialogInternet(MainActivity.this).show();
        }

        // register broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVER_RESPONSE_FROM_WIDGET);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.location.PROVIDERS_CHANGED");

        UIBroadcastReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_RECEIVER_RESPONSE_FROM_WIDGET)) {
                    WeatherResult weatherResult = (WeatherResult) intent.getSerializableExtra(Common.INTENT_WEATHER_RESULT);
                    int addressId = intent.getIntExtra(Common.INTENT_ADDRESS_ID, 0);
                    int position = 0;
                    for (int i = 0; i < getTotalAddress(); i++) {
                        if (sharedPreferences.getInt(SHARE_PREF_ADDRESS_ID_KEY_AT + i, -1) == addressId)
                            position = i;
                    }
                    if (!isReceiver.get(position) && isReceiver.size()>0) {
                        isReceiver.set(position, true);
                        listWeatherResults.set(position, weatherResult);
                        if (position == positionPager && !fragmentHomeIsVisible) {
                            openWeatherHomeFragment(positionPager);
                            fragmentHomeIsVisible = true;
                            loadingView.setVisibility(View.GONE);
                        }
                        checkFragmentVisible(position);

                    }


                } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    if (isConnected(MainActivity.this)) {
                        if(!isEnabledLocation(MainActivity.this)){
                            dialogLocation = buildDialogLocation(MainActivity.this).show();
                        }
                        if (dialogInternet != null) {
                            dialogInternet.cancel();
                        }
                        listWeatherResults = new ArrayList<>();
                        isReceiver = new ArrayList<>();
                        for (int i = 0; i < totalAddress; i++) {
                            listWeatherResults.add(null);
                            isReceiver.add(false);
                            sendRequestGetWeatherInfo(i);
                        }
                    }

                }else if (intent.getAction().equals("android.location.PROVIDERS_CHANGED")){
                    if (isEnabledLocation(MainActivity.this)){
                        isReceiver.set(0,false);
                        sendRequestGetWeatherInfo(CURRENT_ADDRESS_ID);
                    }

                }
            }
        };
        registerReceiver(UIBroadcastReceiver, filter);
    }

    private void checkFragmentVisible(int position) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment != null) {
                    if (fragment.isVisible()) {
                        if (fragment instanceof WeatherAddressFragment) {
                            ((WeatherAddressFragment) fragment).getAddressAdapter().notifyItemChanged(position);
                            return;
                        } else if (fragment instanceof WeatherHomeFragment) {
                            ((WeatherHomeFragment) fragment).getAdapter().notifyDataSetChanged();
                            return;
                        }

                    }
                }
            }
        }
    }

    private boolean checkFragmentCanOpen() {
        for (int i = 0; i < isReceiver.size(); i++) {
            if (!isReceiver.get(i)) {
                return false;
            }
        }
        return true;
    }

    public void sendRequestGetWeatherInfo(int countAddress) {
        if (isConnected(this)) {
            Intent intent = new Intent(MainActivity.this, WeatherWidget.class);
            intent.setAction(ACTION_GET_WEATHER_RESULT_BY_ADDRESS_ID);
            intent.putExtra(Common.INTENT_ADDRESS_ID, countAddress);
            sendBroadcast(intent);
        }
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

    public AlertDialog.Builder buildDialogInternet(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this.");

        builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
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

    public AlertDialog.Builder buildDialogLocation(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(R.string.gps_network_not_enabled);

        builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    public boolean isEnabledLocation(Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

       return gps_enabled||network_enabled;
    }

    public List<Boolean> getIsReceiver() {
        return isReceiver;
    }

    public int getTotalAddress() {
        return totalAddress;
    }

    public void setTotalAddress(int totalAddress) {
        this.totalAddress = totalAddress;
    }

    public List<WeatherResult> getListWeatherResults() {
        return listWeatherResults;
    }

    public void openWeatherHomeFragment(int positionPager) {
        WeatherHomeFragment fragment = new WeatherHomeFragment();
        fragment.setWeatherResultList(listWeatherResults);
        fragment.setCurPositionPager(positionPager);
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commitAllowingStateLoss();
    }

    public void openWeatherAddressFragment() {
        WeatherAddressFragment fragment = new WeatherAddressFragment();
        fragment.setWeatherResultList(listWeatherResults);
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commitAllowingStateLoss();
    }

    public void openFindAddressFragment() {
        FindAddressFragment fragment = new FindAddressFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
