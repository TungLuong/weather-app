package tl.com.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import tl.com.weatherapp.base.BaseFragment;
import tl.com.weatherapp.common.Common;

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
        int totalAddress = sharedPreferences.getInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, 1);
        int maxId = sharedPreferences.getInt(Common.SHARE_PREF_MAX_ID_KEY, 0);
        int newId = maxId+1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT+totalAddress,newId);

        editor.putFloat(Common.SHARE_PREF_LAT_KEY_AT + newId, (float) latLng.latitude);
        editor.putFloat(Common.SHARE_PREF_LNG_KEY_AT  + newId, (float) latLng.longitude);
        editor.putString(Common.SHARE_PREF_ADDRESS_NAME_KEY_AT  + newId, String.valueOf(place.getName()));

        editor.putInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, totalAddress + 1);
        editor.putInt(Common.SHARE_PREF_MAX_ID_KEY, newId);
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
