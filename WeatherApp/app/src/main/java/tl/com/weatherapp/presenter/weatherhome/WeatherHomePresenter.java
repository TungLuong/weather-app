package tl.com.weatherapp.presenter.weatherhome;

import java.util.List;

import tl.com.weatherapp.model.WeatherResult;
import tl.com.weatherapp.model.modelnetwork.ModelNetwork;
import tl.com.weatherapp.view.weatherhome.IWeatherHomeView;

public class WeatherHomePresenter implements IWeatherHomePresenter {
    private ModelNetwork modelNetwork;
    IWeatherHomeView iWeatherHomeView;

    public WeatherHomePresenter(IWeatherHomeView iWeatherHomeView) {
        modelNetwork = ModelNetwork.getInstance();
        modelNetwork.setiWeatherHomePresenter(this);
        this.iWeatherHomeView = iWeatherHomeView;
    }

    public void getResultWeather() {
        modelNetwork.getResultWeatherForWeatherHomePresenter();
    }

    public void setCurrPositionPagerForModel(int curPositionPager){
        modelNetwork.setCurrentPager(curPositionPager);

    }

    @Override
    public void getWeatherResult(List<WeatherResult> weatherResults) {
        iWeatherHomeView.getWeatherResult(weatherResults);
    }

    @Override
    public void setCurrPositionPagerForView(int curPositionPager) {
        iWeatherHomeView.setCurrPositionPager(curPositionPager);
    }


    @Override
    public void notifyItemChange(int position) {
        iWeatherHomeView.notifyItemChange(position);
    }
}
