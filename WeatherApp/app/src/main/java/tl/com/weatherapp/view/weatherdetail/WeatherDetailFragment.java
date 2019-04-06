package tl.com.weatherapp.view.weatherdetail;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tl.com.weatherapp.R;
import tl.com.weatherapp.adapter.ItemAttributeWeatherAdapter;
import tl.com.weatherapp.adapter.ItemDailyWeatherAdapter;
import tl.com.weatherapp.adapter.ItemHourlyWeatherAdapter;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.AttributeWeather;
import tl.com.weatherapp.model.WeatherResult;
import tl.com.weatherapp.presenter.weatherdetail.WeatherDetailPresenter;

import static tl.com.weatherapp.common.Common.convertUnixToDate;


@TargetApi(Build.VERSION_CODES.M)
public class WeatherDetailFragment extends Fragment implements View.OnScrollChangeListener {

    static WeatherDetailFragment instance;

    private SwipeRefreshLayout refreshLayout;
    private RoundedImageView imgWeather;
    private ImageView iconWeather;
    private TextView tvCityName, tvHumidity, tvPressure, tvTemperature, tvDateTime, tvWindSpeed, tvDescription, tvDewPoint, tvCloudCover, tvUVIndex, tvVisibility, tvOzone;
    private LinearLayout mLinerLayout1, mLinerLayout2, mLinerLayour3;
    private RecyclerView rcvDaily, rcvHourly,rcvAttributeWeather;
    private NestedScrollView scrollView1;
    private ScrollView scrollView2;
    private LinearLayout mLinearLayout4;
    private  RelativeLayout space;
    private LinearLayout weatherPanel;
    //private ProgressBar loading;
    private ImageView background;

    private AlertDialog dialogInternet;

    private BroadcastReceiver UIBroadcastReceiver;

    private WeatherResult weatherResult;
    private int countAddress;
    private float alphaLinerLayout2 = 1.0f;
    private WeatherDetailPresenter presenter;

    public WeatherDetailFragment() {
    }

    @SuppressLint("ValidFragment")
    public WeatherDetailFragment(WeatherResult weatherResult, int countAddress) {
        this.weatherResult = weatherResult;
        this.countAddress = countAddress;
        presenter = new WeatherDetailPresenter();

    }

    //    public WeatherDetailFragment() {
//
//    }
//
//    public static WeatherDetailFragment getInstance() {
//        if (instance == null) {
//            instance = new WeatherDetailFragment();
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

        View view = inflater.inflate(R.layout.fragment_weather_detail, container, false);


        // loading = view.findViewById(R.id.loading);
        background = view.findViewById(R.id.background_image_view);
        tvCityName = view.findViewById(R.id.tv_city_name);
//        tvHumidity = view.findViewById(R.id.tv_humidity);
//        tvPressure = view.findViewById(R.id.tv_pressure);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvDateTime = view.findViewById(R.id.tv_date_time);
       // tvWindSpeed = view.findViewById(R.id.tv_windSpeed);
        tvDescription = view.findViewById(R.id.tv_description);
//        tvDewPoint = view.findViewById(R.id.tv_dewPoint);
//        tvCloudCover = view.findViewById(R.id.tv_cloudCover);
//        tvUVIndex = view.findViewById(R.id.tv_uvIndex);
//        tvVisibility = view.findViewById(R.id.tv_visibility);
//        tvOzone = view.findViewById(R.id.tv_ozone);

        rcvDaily = view.findViewById(R.id.rcv_daily);
        rcvHourly = view.findViewById(R.id.rcv_hourly);
        rcvAttributeWeather = view.findViewById(R.id.rcv_attribute_weather);


        mLinerLayout1 = view.findViewById(R.id.liner_layout_1);
        mLinerLayout2 = view.findViewById(R.id.liner_layout_2);
        mLinerLayour3 = view.findViewById(R.id.liner_layout_3);
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
        mLinearLayout4 = view.findViewById(R.id.linear_layout_4);
        scrollView1.setOnScrollChangeListener(this);

        scrollView2.setOnScrollChangeListener(this);
        space = view.findViewById(R.id.space);

        setHeightForRecycleViewAttributeWeather(inflater,container);

        setHeightForScrollView2();
        refreshLayout = view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                presenter.updateInformationByPosition(countAddress);
//                ((MainActivity) getActivity()).getIsReceiver().set(countAddress, false);
//                ((MainActivity) getActivity()).sendRequestGetWeatherInfo(countAddress);

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getWeatherInfo(weatherResult);
        }
        refreshLayout.setRefreshing(false);
       // scrollView();
        return view;
    }

    private void setHeightForRecycleViewAttributeWeather(LayoutInflater inflater, ViewGroup container) {
        View itemView = inflater.inflate(R.layout.item_attribute_weather,container,false);
        rcvAttributeWeather.getLayoutParams().height = Common.TOTAL_ATTRIBUTE_WEATHER*itemView.findViewById(R.id.item_view).getLayoutParams().height;
    }

    private void setHeightForScrollView2() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        int scrollViewHeight2 = 0;
        scrollViewHeight2 = (int) (height  - mLinerLayout1.getLayoutParams().height - mLinerLayour3.getLayoutParams().height - 1.57*space.getLayoutParams().height);
        scrollView2.getLayoutParams().height = scrollViewHeight2  ;
    }

    private void scrollView() {
        scrollView1.smoothScrollBy(0,0);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getWeatherInfo(WeatherResult weatherResult) {
        if (weatherResult == null) {
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
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        ItemDailyWeatherAdapter itemDailyWeatherAdapter = new ItemDailyWeatherAdapter(weatherResult);
        rcvDaily.setLayoutManager(layoutManager2);
        rcvDaily.setAdapter(itemDailyWeatherAdapter);
        //rcvDaily.setNestedScrollingEnabled(false);

        // load information
        if (weatherResult.getAddress() == null) {
            tvCityName.setText("unknown where");
        } else tvCityName.setText(weatherResult.getAddress());


        List<AttributeWeather> list= new ArrayList<>();
        list.add(new AttributeWeather(getString(R.string.Humidity),new StringBuffer(String.valueOf(weatherResult.getCurrently().getHumidity() * 100)).append("%").toString()));
        list.add(new AttributeWeather(getString(R.string.Pressure),new StringBuilder(String.valueOf(weatherResult.getCurrently().getPressure())).append(" hPa").toString()));
        list.add(new AttributeWeather(getString(R.string.WindSpeed),new StringBuilder(String.valueOf(weatherResult.getCurrently().getWindSpeed())).append("m/s").toString()));
        list.add(new AttributeWeather(getString(R.string.DewPoint),new StringBuilder(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getDewPoint()))).append("˚").toString()));
        list.add(new AttributeWeather(getString(R.string.CloudCover),new StringBuilder(String.valueOf(weatherResult.getCurrently().getCloudCover())).toString()));
        list.add(new AttributeWeather(getString(R.string.UVIndex),new StringBuilder(String.valueOf(weatherResult.getCurrently().getUvIndex())).toString()));
        list.add(new AttributeWeather(getString(R.string.Visibility),new StringBuilder(String.valueOf(weatherResult.getCurrently().getVisibility())).append("+ km").toString()));
        list.add(new AttributeWeather(getString(R.string.Ozone),new StringBuilder(String.valueOf(weatherResult.getCurrently().getOzone())).toString()));
        //set RecyclerView Attribute Weather

        ItemAttributeWeatherAdapter itemAttributeWeatherAdapter = new ItemAttributeWeatherAdapter(list);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        layoutManager3.setOrientation(LinearLayoutManager.VERTICAL);
        //rcvAttributeWeather.setNestedScrollingEnabled(false);
        rcvAttributeWeather.setLayoutManager(layoutManager3);
        rcvAttributeWeather.setAdapter(itemAttributeWeatherAdapter);


        tvDateTime.setText(convertUnixToDate(weatherResult.getCurrently().getTime()) + "");
       // tvHumidity.setText(weatherResult.getCurrently().getHumidity() * 100 + "%");
       // tvPressure.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getPressure())).append(" hPa"));
        tvTemperature.setText(new StringBuilder(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getTemperature()))).append("˚"));
       //tvWindSpeed.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getWindSpeed())).append("m/s"));
        tvDescription.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getSummary())));
       // tvDewPoint.setText(new StringBuilder(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getDewPoint()))).append("˚"));
       // tvCloudCover.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getCloudCover())));
       // tvUVIndex.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getUvIndex())));
        //tvVisibility.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getVisibility())).append("+ km"));
       // tvOzone.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getOzone())).append(""));

        // display
        //scrollView1.setVisibility(View.VISIBLE);
        //loading.setVisibility(View.GONE);



    }

    public void isDisConnected() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        switch (v.getId()) {
            case R.id.scrollView_1:
                mLinerLayout1.setY(Math.max(0, scrollY));
                mLinerLayour3.setY(Math.max(mLinerLayout1.getHeight() + scrollY, mLinerLayout1.getHeight() + mLinerLayout2.getHeight() - scrollY));

                alphaLinerLayout2 = 20.0f / (scrollY + 1) - 0.25f;
                mLinerLayout2.setAlpha(alphaLinerLayout2);
                mLinearLayout4.setY(Math.max(mLinerLayout1.getHeight() + mLinerLayour3.getHeight() + scrollY
                        , mLinerLayout1.getHeight() + mLinerLayout2.getHeight() + mLinerLayour3.getHeight() - scrollY));
                break;
            case R.id.scrollView_2:
                if (scrollView1.canScrollVertically(1)){
                    scrollView2.scrollTo(0, 0);
                } else scrollView2.scrollTo(0, scrollY);
                break;

        }

    }

}

