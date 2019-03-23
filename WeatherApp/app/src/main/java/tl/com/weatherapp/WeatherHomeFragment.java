package tl.com.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import tl.com.weatherapp.adapter.WeatherFragmentAdapter;
import tl.com.weatherapp.base.BaseFragment;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.ListWeatherResult;
import tl.com.weatherapp.model.WeatherResult;

public class WeatherHomeFragment extends BaseFragment {


    private ViewPager viewPager;
    private int curPositionPager = 0;
    private CircleIndicator indicator;
    private ImageView btnOpenWeatherAddressActivity;

    private WeatherFragmentAdapter adapter;
    private List<WeatherResult> weatherResultList;

    public WeatherHomeFragment() {
    }

    public static WeatherHomeFragment newInstance() {
        WeatherHomeFragment fragment = new WeatherHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        viewPager = view.findViewById(R.id.view_pager_weather_fragment);
        adapter = new WeatherFragmentAdapter(getChildFragmentManager());
        adapter.setWeatherResultList(weatherResultList);
        viewPager.setAdapter(adapter);
        indicator = view.findViewById(R.id.circle_indicator);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());
        viewPager.setCurrentItem(curPositionPager);

        btnOpenWeatherAddressActivity = view.findViewById(R.id.btn_open_weather_address_activity);
        btnOpenWeatherAddressActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).openWeatherAddressFragment();
            }
        });

    }

    public List<WeatherResult> getWeatherResultList() {
        return weatherResultList;
    }

    public void setWeatherResultList(List<WeatherResult> weatherResultList) {
        this.weatherResultList = weatherResultList;
    }

    @Override
    public void onBackPressed() {
        ((MainActivity)getActivity()).showDialogDelete();
    }

    public WeatherFragmentAdapter getAdapter() {
        return adapter;
    }

    public void setCurPositionPager(int curPositionPager) {
        this.curPositionPager = curPositionPager;
    }
}
