package tl.com.weatherapp.presenter.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import tl.com.weatherapp.R;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.modelnetwork.ModelNetwork;
import tl.com.weatherapp.view.main.IMainView;

public class MainPresenter implements IMainPresenter {
    private ModelNetwork modelNetwork;
    private Context mContext;
    private AlertDialog dialogInternet;
    private AlertDialog dialogLocation;
    private BroadcastReceiver receiver;
    private IMainView iMainView;

    public MainPresenter(Context mContext) {
        this.mContext = mContext;
        modelNetwork = ModelNetwork.getInstance();
        modelNetwork.setiMainPresenter(this);
    }

    public void setiMainView(IMainView iMainView) {
        this.iMainView = iMainView;
    }

    public void start() {
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
       filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.location.PROVIDERS_CHANGED");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
//                    if (isConnected(mContext)) {
                    if (!isConnected(mContext)) {
                        dialogInternet = buildDialogInternet(mContext).show();
                        Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(mContext, "Internet Connection", Toast.LENGTH_SHORT).show();
                        if (!isEnabledLocation(mContext)) {
                            Toast.makeText(mContext, R.string.gps_network_not_enabled, Toast.LENGTH_SHORT).show();
                        }
                    }

//                        if (dialogInternet != null) {
//                            dialogInternet.cancel();
//                        }
//                    }
                    modelNetwork.create(mContext);
                    modelNetwork.loadDataForMainPresenter();


                } else if (intent.getAction().equals("android.location.PROVIDERS_CHANGED")) {
                    if (isEnabledLocation(mContext)) {
                        modelNetwork.updateInformationByAddressId(Common.CURRENT_ADDRESS_ID, Common.UPDATE_ALL_WIDGET);
                        Toast.makeText(mContext, R.string.gps_network_enabled, Toast.LENGTH_SHORT).show();
                    } else {
                        //dialogLocation = buildDialogLocation(mContext).show();
                        Toast.makeText(mContext, R.string.gps_network_not_enabled, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };
        mContext.registerReceiver(receiver, filter);
    }

//    private void init() {

    // register broadcast
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_RECEIVER_RESPONSE_FROM_WIDGET);
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.addAction("android.location.PROVIDERS_CHANGED");
//
//        UIBroadcastReceiver = new BroadcastReceiver() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals(ACTION_RECEIVER_RESPONSE_FROM_WIDGET)) {
//                    WeatherResult weatherResult = (WeatherResult) intent.getSerializableExtra(Common.INTENT_WEATHER_RESULT);
//                    int addressId = intent.getIntExtra(Common.INTENT_ADDRESS_ID, 0);
//                    int position = 0;
//                    for (int i = 0; i < getTotalAddress(); i++) {
//                        if (sharedPreferences.getInt(SHARE_PREF_ADDRESS_ID_KEY_AT + i, -1) == addressId)
//                            position = i;
//                    }
//                    if (!isReceiver.get(position) && isReceiver.size()>0) {
//                        isReceiver.set(position, true);
//                        listWeatherResults.set(position, weatherResult);
//                        if (position == positionPager && !fragmentHomeIsVisible) {
//                            openWeatherHomeFragment(positionPager);
//                            fragmentHomeIsVisible = true;
//                            loadingView.setVisibility(View.GONE);
//                        }
//                        checkFragmentVisible(position);
//
//                    }
//
//
//                } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
//                    if (isConnected(MainActivity.this)) {
//                        if(!isEnabledLocation(MainActivity.this)){
//                            dialogLocation = buildDialogLocation(MainActivity.this).show();
//                        }
//                        if (dialogInternet != null) {
//                            dialogInternet.cancel();
//                        }
////                        listWeatherResults = new ArrayList<>();
////                        isReceiver = new ArrayList<>();
////                        for (int i = 0; i < totalAddress; i++) {
////                            listWeatherResults.add(null);
////                            isReceiver.add(false);
////                            sendRequestGetWeatherInfo(i);
////                        }
//                    }
//
//                }else if (intent.getAction().equals("android.location.PROVIDERS_CHANGED")){
//                    if (isEnabledLocation(MainActivity.this)){
//                        isReceiver.set(0,false);
//                        sendRequestGetWeatherInfo(CURRENT_ADDRESS_ID);
//                    }
//
//                }
//            }
//        };
//        registerReceiver(UIBroadcastReceiver, filter);
//    }
//
//    private void checkFragmentVisible(int position) {
//        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
//        if (fragmentList != null) {
//            for (Fragment fragment : fragmentList) {
//                if (fragment != null) {
//                    if (fragment.isVisible()) {
//                        if (fragment instanceof WeatherAddressFragment) {
//                            ((WeatherAddressFragment) fragment).getAddressAdapter().notifyItemChanged(position);
//                            return;
//                        } else if (fragment instanceof WeatherHomeFragment) {
//                            ((WeatherHomeFragment) fragment).getAdapter().notifyDataSetChanged();
//                            return;
//                        }
//
//                    }
//                }
//            }
//        }
//    }
//
//    private boolean checkFragmentCanOpen() {
//        for (int i = 0; i < isReceiver.size(); i++) {
//            if (!isReceiver.get(i)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public void sendRequestGetWeatherInfo(int countAddress) {
//        if (isConnected(this)) {
//            Intent intent = new Intent(MainActivity.this, WeatherWidget.class);
//            intent.setAction(ACTION_GET_WEATHER_RESULT_BY_ADDRESS_ID);
//            intent.putExtra(Common.INTENT_ADDRESS_ID, countAddress);
//            sendBroadcast(intent);
//        }
//    }


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
                mContext.startActivity(intent);

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
                mContext.startActivity(intent);

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

    public boolean isEnabledLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }

    @Override
    public void loadDataFinish() {

        iMainView.openWeatherHomeFragment();

    }

    public void setCurrentPagerByAddressId(int addressId) {
        modelNetwork.setCurrentPagerByAddressId(addressId);
    }

    public void setCurrentPager(int positionPager) {
        modelNetwork.setCurrentPager(positionPager);
    }

    public void isNotReceiver() {
        modelNetwork.isNotReceiver();
    }

    public void unregisterReceiver() {
        try {
            mContext.unregisterReceiver(receiver);
        }catch (Exception e){
            Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT);
        }
    }
}
