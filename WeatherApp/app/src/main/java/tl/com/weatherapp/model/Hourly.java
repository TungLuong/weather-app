package tl.com.weatherapp.model;

import java.io.Serializable;
import java.util.List;

public class Hourly implements Serializable {
    private String summary ;
    private String icon ;
    private List<Datum2> data ;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Datum2> getData() {
        return data;
    }

    public void setData(List<Datum2> data) {
        this.data = data;
    }
}
