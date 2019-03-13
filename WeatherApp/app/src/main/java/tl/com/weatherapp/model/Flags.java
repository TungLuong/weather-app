package tl.com.weatherapp.model;

import java.io.Serializable;
import java.util.List;

public class Flags implements Serializable {
    private List<String> sources ;
    private double __invalid_name__nearest_station ;
    private String units ;

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public double get__invalid_name__nearest_station() {
        return __invalid_name__nearest_station;
    }

    public void set__invalid_name__nearest_station(double __invalid_name__nearest_station) {
        this.__invalid_name__nearest_station = __invalid_name__nearest_station;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
