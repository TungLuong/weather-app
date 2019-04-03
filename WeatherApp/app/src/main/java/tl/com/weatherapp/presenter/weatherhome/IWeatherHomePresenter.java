package tl.com.weatherapp.presenter.weatherhome;

import java.util.List;

import tl.com.weatherapp.model.WeatherResult;

public interface IWeatherHomePresenter {
    void getWeatherResult(List<WeatherResult> weatherResults);

    void setCurrPositionPagerForView(int curPositionPager);

    void notifyItemChange(int position);
}
