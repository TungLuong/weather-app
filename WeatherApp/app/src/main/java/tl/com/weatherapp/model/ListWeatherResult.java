package tl.com.weatherapp.model;

import java.io.Serializable;
import java.util.List;

public class ListWeatherResult implements Serializable {
    private List<WeatherResult> weatherResultList;

    public List<WeatherResult> getWeatherResultList() {
        return weatherResultList;
    }

    public void setWeatherResultList(List<WeatherResult> weatherResultList) {
        this.weatherResultList = weatherResultList;
    }
}
