package tl.com.weatherapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import tl.com.weatherapp.adapter.ItemDailyWeatherAdapter;
import tl.com.weatherapp.adapter.ItemHourlyWeatherAdapter;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.WeatherResult;

import static tl.com.weatherapp.common.Common.convertUnixToDate;


public class WeatherFragment extends Fragment {

    static WeatherFragment instance;

    private SwipeRefreshLayout refreshLayout;
    private RoundedImageView imgWeather;
    private ImageView iconWeather;
    private TextView tvCityName, tvHumidity, tvPressure, tvTemperature, tvDateTime, tvWindSpeed, tvDescription, tvDewPoint, tvCloudCover, tvUVIndex, tvVisibility, tvOzone;
    private LinearLayout tv1, tv2, tv3;
    private RecyclerView rcvDaily, rcvHourly;
    private ScrollView scrollView1;
    private NestedScrollView scrollView2;
    private LinearLayout linearLayout;
    private LinearLayout weatherPanel;
    //private ProgressBar loading;
    private ImageView background;

    private AlertDialog dialogInternet;

    private BroadcastReceiver UIBroadcastReceiver;

    private WeatherResult weatherResult;
    private int countAddress;

    public WeatherFragment() {
    }

    @SuppressLint("ValidFragment")
    public WeatherFragment(WeatherResult weatherResult,int countAddress) {
        this.weatherResult = weatherResult;
        this.countAddress = countAddress;
    }

    //    public WeatherFragment() {
//
//    }
//
//    public static WeatherFragment getInstance() {
//        if (instance == null) {
//            instance = new WeatherFragment();
//        }
//        return instance;
//    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather, container, false);


        // loading = view.findViewById(R.id.loading);
        background = view.findViewById(R.id.background_image_view);
        tvCityName = view.findViewById(R.id.tv_city_name);
        tvHumidity = view.findViewById(R.id.tv_humidity);
        tvPressure = view.findViewById(R.id.tv_pressure);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvDateTime = view.findViewById(R.id.tv_date_time);
        tvWindSpeed = view.findViewById(R.id.tv_windSpeed);
        tvDescription = view.findViewById(R.id.tv_description);
        tvDewPoint = view.findViewById(R.id.tv_dewPoint);
        tvCloudCover = view.findViewById(R.id.tv_cloudCover);
        tvUVIndex = view.findViewById(R.id.tv_uvIndex);
        tvVisibility = view.findViewById(R.id.tv_visibility);
        tvOzone = view.findViewById(R.id.tv_ozone);

        rcvDaily = view.findViewById(R.id.rcv_daily);
        rcvHourly = view.findViewById(R.id.rcv_hourly);

        tv1 = view.findViewById(R.id.liner_layout_1);
        tv2 = view.findViewById(R.id.liner_layout_2);
        tv3 = view.findViewById(R.id.liner_layout_3);
//        listView = findViewById(R.id.list_item);
//        List<String> modelList = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            modelList.add("List item " + i);
//        }
//
//        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_view, modelList);
//        listView.setAdapter(adapter);
        scrollView1 = view.findViewById(R.id.scrollView_1);
        //scrollView1.setVisibility(View.INVISIBLE);
        scrollView2 = view.findViewById(R.id.scrollView_2);
        linearLayout = view.findViewById(R.id.linear);
        scrollView1.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                tv1.setY(Math.max(0, scrollY));
                tv3.setY(Math.max(tv1.getHeight() + scrollY, tv1.getHeight() + tv2.getHeight() - scrollY));
                float al = 20.0f / (scrollY + 1) - 0.25f;
                tv2.setAlpha(al);
                linearLayout.setY(Math.max(tv1.getHeight() + tv3.getHeight() + scrollY, tv1.getHeight() + tv2.getHeight() + tv3.getHeight() - scrollY));
                int x = scrollView1.getScrollY();


            }
        });
        scrollView2.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (tv3.getY() > tv1.getHeight() + tv1.getY()) {
                    scrollView2.scrollTo(0, 0);
                } else scrollView2.scrollTo(0, scrollY);


            }
        });

        refreshLayout = view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                ((MainActivity)getActivity()).getIsReceiver().set(countAddress,false);
                ((MainActivity)getActivity()).sendRequestGetWeatherInfo(countAddress);

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getWeatherInfo(weatherResult);
        }
        refreshLayout.setRefreshing(false);
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getWeatherInfo(WeatherResult weatherResult) {
        if (weatherResult == null){
            refreshLayout.setRefreshing(true);
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getContext().getPackageName() + "/drawable/" + "blue_sky_2");
            Picasso.get()
                    .load(uri)
                    .fit()
                    .into(background);

            return;
        }
        String icon_name = weatherResult.getCurrently().getIcon().replace('-', '_');
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getActivity().getPackageName() + "/drawable/" + icon_name);
        Picasso.get()
                .load(uri)
                .fit()
                .into(background);

        //set RecyclerView Hourly
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ItemHourlyWeatherAdapter itemHourlyWeatherAdapter = new ItemHourlyWeatherAdapter(weatherResult);
        rcvHourly.setLayoutManager(layoutManager);
        rcvHourly.setAdapter(itemHourlyWeatherAdapter);

        //set RecyclerView Daily
        LinearLayoutManager layoutManager_2 = new LinearLayoutManager(getContext());
        layoutManager_2.setOrientation(LinearLayoutManager.VERTICAL);
        ItemDailyWeatherAdapter itemDailyWeatherAdapter = new ItemDailyWeatherAdapter(weatherResult);
        rcvDaily.setLayoutManager(layoutManager_2);
        rcvDaily.setAdapter(itemDailyWeatherAdapter);

        // load information
        if (weatherResult.getAddress() == null) {
            tvCityName.setText("unknown where");
        }else tvCityName.setText(weatherResult.getAddress());


        tvDateTime.setText(convertUnixToDate(weatherResult.getCurrently().getTime()) + "");
        tvHumidity.setText(weatherResult.getCurrently().getHumidity()*100 + "%");
        tvPressure.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getPressure())).append(" hPa"));
        tvTemperature.setText(new StringBuilder(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getTemperature()))).append("˚"));
        tvWindSpeed.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getWindSpeed())).append("m/s"));
        tvDescription.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getSummary())));
        tvDewPoint.setText(new StringBuilder(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getDewPoint()))).append("˚"));
        tvCloudCover.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getCloudCover())));
        tvUVIndex.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getUvIndex())));
        tvVisibility.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getVisibility())).append("+ km"));
        tvOzone.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getOzone())).append(""));

        // display
        //scrollView1.setVisibility(View.VISIBLE);
        //loading.setVisibility(View.GONE);

    }

    public void isDisConnected(){
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

