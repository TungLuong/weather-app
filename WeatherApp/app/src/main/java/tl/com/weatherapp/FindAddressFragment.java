package tl.com.weatherapp;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import tl.com.weatherapp.base.BaseFragment;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.WeatherResult;

import static android.content.Context.MODE_PRIVATE;

public class FindAddressFragment extends BaseFragment implements PlaceSelectionListener {

    PlaceAutocompleteFragment autocompleteFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_find_address, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        autocompleteFragment =
                (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(
                        R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
    }

    @Override
    public void onPlaceSelected(Place place) {
        LatLng latLng = place.getLatLng();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Common.DATA, MODE_PRIVATE);
        int totalAddress = sharedPreferences.getInt("TOTAL_ADDRESS", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("LAT" + totalAddress, (float) latLng.latitude);
        editor.putFloat("LNG" + totalAddress, (float) latLng.longitude);
        editor.putString("ADDRESS" + totalAddress, String.valueOf(place.getName()));
        editor.remove("TOTAL_ADDRESS");
        editor.putInt("TOTAL_ADDRESS", totalAddress + 1);
        editor.commit();
        ((MainActivity) getActivity()).getIsReceiver().add(false);
        ((MainActivity) getActivity()).getListWeatherResults().add(null);
        ((MainActivity) getActivity()).setTotalAddress(totalAddress + 1);
        ((MainActivity) getActivity()).sendRequestGetWeatherInfo(totalAddress);
        onBackPressed();

    }

    @Override
    public void onError(Status status) {
        Toast.makeText(getContext(), status.toString(), Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onBackPressed() {
        if (autocompleteFragment != null){
            getActivity().getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
        }
        ((MainActivity) getActivity()).openWeatherAddressFragment();
    }
}
