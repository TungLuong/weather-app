package tl.com.weatherapp.view.findaddress;

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

import tl.com.weatherapp.R;
import tl.com.weatherapp.base.BaseFragment;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.presenter.findaddress.FindAddressPresenter;
import tl.com.weatherapp.view.main.MainActivity;

import static android.content.Context.MODE_PRIVATE;

public class FindAddressFragment extends BaseFragment implements PlaceSelectionListener {

    private PlaceAutocompleteFragment autocompleteFragment;
    private FindAddressPresenter presenter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_find_address, container, false);
        initView(view);
        presenter = new FindAddressPresenter();
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
        presenter.addAddress(place);
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
