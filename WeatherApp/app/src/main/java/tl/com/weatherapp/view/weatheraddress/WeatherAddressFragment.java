package tl.com.weatherapp.view.weatheraddress;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;
import tl.com.weatherapp.IListenerWeatherAddressAdapter;
import tl.com.weatherapp.R;
import tl.com.weatherapp.adapter.ItemWeatherAddressAdapter;
import tl.com.weatherapp.base.BaseFragment;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.WeatherResult;
import tl.com.weatherapp.presenter.weatheraddress.WeatherAddressPresenter;
import tl.com.weatherapp.view.main.MainActivity;

import static android.content.Context.MODE_PRIVATE;

public class WeatherAddressFragment extends BaseFragment implements IWeatherAddressView, IListenerWeatherAddressAdapter {

    private RecyclerView rcvWeatherAddress;
    private ImageView btnAddAddress;
    private ItemWeatherAddressAdapter addressAdapter;
    private WeatherAddressPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_address, container, false);
        initView(view);
        presenter = new WeatherAddressPresenter(this);
        presenter.getResultWeather();
        return view;
    }


    private void initView(View view) {
        rcvWeatherAddress = view.findViewById(R.id.rcv_weather_address);

        btnAddAddress = view.findViewById(R.id.btn_add_address);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openFindAddressFragment();
            }
        });
    }

    private ItemTouchHelper.Callback createCallBack() {
        ItemTouchHelper.Callback myCallback;
        myCallback = new ItemTouchHelper.Callback() {

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }


            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int oldPo = viewHolder.getAdapterPosition();
                int newPo = target.getAdapterPosition();
                if (oldPo == 0 || newPo == 0) return false;
                presenter.moveItem(oldPo,newPo);
                addressAdapter.notifyItemMoved(oldPo, newPo);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        return myCallback;
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
        presenter.deleteItem(position);
        addressAdapter.notifyDataSetChanged();
    }

    @Override
    public void openWeatherHomeFragment(int position) {
        ((MainActivity) getActivity()).openWeatherHomeFragment();
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).openWeatherHomeFragment();
    }


    @Override
    public void getWeatherResult(List<WeatherResult> weatherResults) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvWeatherAddress.setLayoutManager(layoutManager);

        addressAdapter = new ItemWeatherAddressAdapter(weatherResults, getContext(), this);
        //     ((ItemWeatherAddressAdapter) addressAdapter).setMode(Attributes.Mode.Single);
        rcvWeatherAddress.setAdapter(addressAdapter);
        //OverScrollDecoratorHelper.setUpOverScroll(rcvWeatherAddress,OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        ItemTouchHelper.Callback myCallback = createCallBack();
        //ItemTouchHelper helper = new ItemTouchHelper(myCallback);
        //helper.attachToRecyclerView(rcvWeatherAddress);
        rcvWeatherAddress.setOverScrollMode(View.OVER_SCROLL_NEVER);
        new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(rcvWeatherAddress, myCallback));
    }

    @Override
    public void notifyItemChange(int position) {
        addressAdapter.notifyItemChanged(position);
    }
}
