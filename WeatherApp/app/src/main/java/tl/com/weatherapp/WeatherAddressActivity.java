package tl.com.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import tl.com.weatherapp.adapter.ItemWeatherAddressAdapter;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.ListWeatherResult;
import tl.com.weatherapp.model.WeatherResult;

public class WeatherAddressActivity extends AppCompatActivity implements IListenerDelete {

    private RecyclerView rcvWeatherAddress;
    private ImageView btnAddAddress;
    private List<WeatherResult> weatherResultList = new ArrayList<>();
    private ItemWeatherAddressAdapter addressAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ListWeatherResult listWeatherResult = (ListWeatherResult) extras.getSerializable(Common.LIST_WEATHER_RESULT);
            weatherResultList = listWeatherResult.getWeatherResultList();
        }
        setContentView(R.layout.acitvity_weather_address);
        init();
    }

    private void init() {
        rcvWeatherAddress = findViewById(R.id.rcv_weather_address);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvWeatherAddress.setLayoutManager(layoutManager);
        addressAdapter= new ItemWeatherAddressAdapter(weatherResultList,this,this);
        rcvWeatherAddress.setAdapter(addressAdapter);
        btnAddAddress = findViewById(R.id.btn_add_address);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherAddressActivity.this,FindAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void deleteItem(int position) {

        SharedPreferences sharedPreferences = getSharedPreferences(Common.DATA,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i = 1 ; i < weatherResultList.size();i++){
            editor.remove("LAT"+i);
            editor.remove("LNG"+i);
            editor.remove("ADDRESS"+i);
        }
        int totalAddress = sharedPreferences.getInt("TOTAL_ADDRESS",1);
        editor.remove("TOTAL_ADDRESS");
        editor.putInt("TOTAL_ADDRESS", totalAddress - 1);
        editor.commit();
        editor = sharedPreferences.edit();
        weatherResultList.remove(position);
        for (int i = 1; i < weatherResultList.size();i++){
            editor.putFloat("LAT"+i, (float) weatherResultList.get(i).getLatitude());
            editor.putFloat("LNG"+i, (float)  weatherResultList.get(i).getLongitude());
            editor.putString("ADDRESS"+i, (String)  weatherResultList.get(i).getAddress());
        }
        editor.commit();
        addressAdapter.notifyDataSetChanged();

    }
}
