package tl.com.weatherapp.view.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.victor.loading.book.BookLoading;
import com.victor.loading.newton.NewtonCradleLoading;

import java.util.List;

import tl.com.weatherapp.R;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.view.findaddress.FindAddressFragment;
import tl.com.weatherapp.view.weatheraddress.WeatherAddressFragment;
import tl.com.weatherapp.view.weatherhome.WeatherHomeFragment;
import tl.com.weatherapp.base.BaseActivity;
import tl.com.weatherapp.presenter.main.MainPresenter;

public class MainActivity extends BaseActivity implements IMainView {


//    private BroadcastReceiver UIBroadcastReceiver;

    public static final String TAG = MainActivity.class.getSimpleName();
//    private int totalAddress = 0;
//    private int positionPager = CURRENT_ADDRESS_ID;
//    private List<WeatherResult> listWeatherResults = new ArrayList<>();
//    private List<Boolean> isReceiver = new ArrayList<>();
    private RelativeLayout loadingView;
    private MainPresenter mainPresenter;
//    private boolean fragmentHomeIsVisible = false;
//    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sharedPreferences = getSharedPreferences(Common.DATA, MODE_PRIVATE);
//        totalAddress = sharedPreferences.getInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, 1);
        mainPresenter = new MainPresenter(this);
        mainPresenter.setiMainView(this);
        Bundle extras = getIntent().getExtras();
        mainPresenter.isNotReceiver();
        if (extras != null) {
            int addressId = extras.getInt(Common.INTENT_ADDRESS_ID);
            mainPresenter.setCurrentPagerByAddressId(addressId);
        }
        setContentView(R.layout.activity_main);
        initView();
        //Request permission
        requestPermission();
    }

    private void requestPermission() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mainPresenter.start();
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

    @Override
    public void openWeatherHomeFragment() {
        WeatherHomeFragment fragment = new WeatherHomeFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commitAllowingStateLoss();
        loadingView.setVisibility(View.GONE);
    }

    public void openWeatherAddressFragment() {
        WeatherAddressFragment fragment = new WeatherAddressFragment();
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
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.unregisterReceiver();
    }

}
