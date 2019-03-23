package tl.com.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.List;

import tl.com.weatherapp.adapter.ItemWeatherAddressAdapter;
import tl.com.weatherapp.base.BaseFragment;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.ListWeatherResult;
import tl.com.weatherapp.model.WeatherResult;

import static android.content.Context.MODE_PRIVATE;

public class WeatherAddressFragment extends BaseFragment implements IListenerDelete {

    private RecyclerView rcvWeatherAddress;
    private ImageView btnAddAddress;
    private List<WeatherResult> weatherResultList = new ArrayList<>();
    private ItemWeatherAddressAdapter addressAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_address, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        rcvWeatherAddress = view.findViewById(R.id.rcv_weather_address);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvWeatherAddress.setLayoutManager(layoutManager);

        addressAdapter = new ItemWeatherAddressAdapter(weatherResultList, getContext(), this);
        ((ItemWeatherAddressAdapter) addressAdapter).setMode(Attributes.Mode.Single);
        rcvWeatherAddress.setAdapter(addressAdapter);

//        SwipeController swipeController = new SwipeController();
//
//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(rcvWeatherAddress);

        btnAddAddress = view.findViewById(R.id.btn_add_address);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).openFindAddressFragment();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (addressAdapter != null){
            addressAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteItem(int position) {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Common.DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = position; i < weatherResultList.size(); i++) {
            editor.remove("LAT" + i);
            editor.remove("LNG" + i);
            editor.remove("ADDRESS_NAME" + i);
        }
        int totalAddress = sharedPreferences.getInt("TOTAL_ADDRESS", 1);
        editor.remove("TOTAL_ADDRESS");
        editor.putInt("TOTAL_ADDRESS", totalAddress - 1);
        editor.commit();
        editor = sharedPreferences.edit();
        weatherResultList.remove(position);
        ((MainActivity)getActivity()).getIsReceiver().remove(position);
        ((MainActivity)getActivity()).setTotalAddress(totalAddress-1);
        for (int i = position; i < weatherResultList.size(); i++) {
            editor.putFloat("LAT" + i, (float) weatherResultList.get(i).getLatitude());
            editor.putFloat("LNG" + i, (float) weatherResultList.get(i).getLongitude());
            editor.putString("ADDRESS_NAME" + i, (String) weatherResultList.get(i).getAddress());
        }
        editor.commit();
        addressAdapter.notifyDataSetChanged();
    }

    public void setWeatherResultList(List<WeatherResult> weatherResultList) {
        this.weatherResultList = weatherResultList;
    }

    public ItemWeatherAddressAdapter getAddressAdapter() {
        return addressAdapter;
    }

    @Override
    public void onBackPressed() {
        ((MainActivity)getActivity()).openWeatherHomeFragment(Common.CURRENT_ADDRESS_ID);
    }


}
