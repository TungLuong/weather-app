package tl.com.weatherapp.retrofit;

import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tl.com.weatherapp.model.WeatherResult;

public interface IOpenWeatherMap {
    @GET("forecast/{id}/{lat},{lng}")
    io.reactivex.Observable<WeatherResult> getWeatherByLatIng(@Path("id") String appid,
                                                              @Path("lat") String lat ,
                                                              @Path("lng") String lng);

}
