package tl.com.weatherapp.model;

import java.io.Serializable;
import java.util.List;

public class Minutely implements Serializable {
    private String summary ;
    private String icon ;
    private List<Datum> data ;

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

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }
}
