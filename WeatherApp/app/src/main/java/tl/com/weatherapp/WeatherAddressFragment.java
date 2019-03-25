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

        ItemTouchHelper helper = new ItemTouchHelper(createCallBack());
        helper.attachToRecyclerView(rcvWeatherAddress);
//        SwipeController swipeController = new SwipeController();
//
//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(rcvWeatherAddress);

        btnAddAddress = view.findViewById(R.id.btn_add_address);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openFindAddressFragment();
            }
        });
    }

    private ItemTouchHelper.Callback createCallBack() {
        ItemTouchHelper.SimpleCallback simpleCallback;
        simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int oldPo = viewHolder.getAdapterPosition();
                int newPo = target.getAdapterPosition();
                if (oldPo == 0 || newPo == 0) return false;
                WeatherResult weatherResult = weatherResultList.remove(oldPo);
                weatherResultList.add(newPo, weatherResult);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Common.DATA, MODE_PRIVATE);
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
                Boolean isReceiver = ((MainActivity) getActivity()).getIsReceiver().remove(oldPo);
                ((MainActivity) getActivity()).getIsReceiver().add(newPo, isReceiver);
                addressAdapter.notifyItemMoved(oldPo, newPo);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        return simpleCallback;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (addressAdapter != null) {
            addressAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteItem(int position) {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Common.DATA, MODE_PRIVATE);
        int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + position, -1);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(Common.SHARE_PREF_LAT_KEY_AT + addressId);
        editor.remove(Common.SHARE_PREF_LAT_KEY_AT + addressId);
        editor.remove(Common.SHARE_PREF_ADDRESS_NAME_KEY_AT + addressId);
        editor.remove(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + position);

        for (int i = position; i < weatherResultList.size() - 1; i++) {
//            editor.remove("INTENT_ADDRESS_ID"+i);
            int newAddressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + (i + 1), -1);
            editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT + i, newAddressId);
        }
        int totalAddress = sharedPreferences.getInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, 1);
        editor.putInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, totalAddress - 1);
        editor.commit();

//        editor = sharedPreferences.edit();
        weatherResultList.remove(position);
        ((MainActivity) getActivity()).getIsReceiver().remove(position);
        ((MainActivity) getActivity()).setTotalAddress(totalAddress - 1);
//        for (int i = position; i < weatherResultList.size(); i++) {
//            editor.putFloat("LAT" + i, (float) weatherResultList.get(i).getLatitude());
//            editor.putFloat("LNG" + i, (float) weatherResultList.get(i).getLongitude());
//            editor.putString("ADDRESS_NAME" + i, (String) weatherResultList.get(i).getAddress());
//        }
//        editor.commit();
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
        ((MainActivity) getActivity()).openWeatherHomeFragment(Common.CURRENT_ADDRESS_ID);
    }


}
