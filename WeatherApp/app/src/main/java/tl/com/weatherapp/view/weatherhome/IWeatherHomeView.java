package tl.com.weatherapp.view.weatherhome;

import java.util.List;

import tl.com.weatherapp.model.WeatherResult;

public interface IWeatherHomeView {
    void getWeatherResult(List<WeatherResult> weatherResults);

    void setCurrPositionPager(int curPositionPager);

    void notifyItemChange(int position);
}
