package tl.com.weatherapp.view.weatheraddress;

import java.util.List;

import tl.com.weatherapp.model.WeatherResult;

public interface IWeatherAddressView {
    void getWeatherResult(List<WeatherResult> weatherResults);

    void notifyItemChange(int position);
}
