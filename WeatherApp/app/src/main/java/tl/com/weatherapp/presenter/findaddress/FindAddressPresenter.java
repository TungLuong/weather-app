package tl.com.weatherapp.presenter.findaddress;

import com.google.android.gms.location.places.Place;

import tl.com.weatherapp.model.modelnetwork.ModelNetwork;
import tl.com.weatherapp.view.findaddress.IFindAddressView;

public class FindAddressPresenter {
    private ModelNetwork modelNetwork;

    public FindAddressPresenter() {
        modelNetwork = ModelNetwork.getInstance();
    }

    public void addAddress(Place place) {
        modelNetwork.addAddress(place);
    }
}
