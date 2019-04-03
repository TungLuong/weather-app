package tl.com.weatherapp.presenter.weatheraddress;

import java.util.List;

import tl.com.weatherapp.model.WeatherResult;
import tl.com.weatherapp.model.modelnetwork.ModelNetwork;
import tl.com.weatherapp.view.weatheraddress.IWeatherAddressView;

public class WeatherAddressPresenter implements IWeatherAddressPresenter {
    private ModelNetwork modelNetwork;
    IWeatherAddressView iWeatherAddressView;

    public WeatherAddressPresenter(IWeatherAddressView iWeatherAddressView) {
        modelNetwork = ModelNetwork.getInstance();
        modelNetwork.setiWeatherAddressPresenter(this);
        this.iWeatherAddressView = iWeatherAddressView;
    }

    public void getResultWeather() {
        modelNetwork.getResultWeatherForWeatherAddressPresenter();
    }

    @Override
    public void getWeatherResult(List<WeatherResult> weatherResults) {
        iWeatherAddressView.getWeatherResult(weatherResults);
    }

    @Override
    public void notifyItemChange(int position) {
        iWeatherAddressView.notifyItemChange(position);
    }

    public void deleteItem(int position) {
        modelNetwork.deleteItem(position);
    }

    public void moveItem(int oldPo, int newPo) {
        modelNetwork.moveItem(oldPo,newPo);
    }
}
