package tl.com.weatherapp.adapter;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tl.com.weatherapp.R;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.WeatherResult;

public class ItemHourlyWeatherAdapter extends RecyclerView.Adapter<ItemHourlyWeatherAdapter.ItemHourlyViewHolder> {

    private WeatherResult forecastWeatherResult;

    public ItemHourlyWeatherAdapter(WeatherResult forecastWeatherResult) {
        this.forecastWeatherResult = forecastWeatherResult;
    }

    @NonNull
    @Override
    public ItemHourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_hourly,parent,false);
        return new ItemHourlyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ItemHourlyViewHolder holder, int position) {
        if (position == 0){
            holder.tvHour.setAlpha(1.0f);
            holder.tvTemp.setAlpha(1.0f);
            holder.tvHour.setText("now");
        }else {
            holder.tvHour.setAlpha(0.8f);
            holder.tvTemp.setAlpha(0.8f);
            holder.tvHour.setText((Common.convertUnixToTime(forecastWeatherResult.getHourly().getData().get(position).getTime()).substring(0,3))+"h");
        }

        holder.tvTemp.setText( Common.covertFtoC(forecastWeatherResult.getHourly().getData().get(position).getTemperature())+ "°");
        Picasso.get().load(new StringBuilder("https://darksky.net/images/weather-icons/")
                                    .append(forecastWeatherResult.getHourly().getData().get(position).getIcon())
                                    .append(".png").toString()).into(holder.iconWeather);
    }

    @Override
    public int getItemCount() {
        if(forecastWeatherResult.getHourly() == null) return 0;
        else return forecastWeatherResult.getHourly().getData().size();
    }

    public class ItemHourlyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTemp, tvHour;
        private ImageView iconWeather;
        public ItemHourlyViewHolder(View itemView) {
            super(itemView);
            tvTemp = itemView.findViewById(R.id.tv_temp);
            tvHour = itemView.findViewById(R.id.tv_hour);
            iconWeather = itemView.findViewById(R.id.img_icon_item_weather);
        }
    }
}
