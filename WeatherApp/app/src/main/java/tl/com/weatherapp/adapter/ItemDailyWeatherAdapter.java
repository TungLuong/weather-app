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


public class ItemDailyWeatherAdapter extends RecyclerView.Adapter<ItemDailyWeatherAdapter.ItemDailyViewHolder> {

    private WeatherResult forecastWeatherResult;

    public ItemDailyWeatherAdapter(WeatherResult forecastWeatherResult) {
        this.forecastWeatherResult = forecastWeatherResult;
    }

    @NonNull
    @Override
    public ItemDailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_daily, parent, false);
        return new ItemDailyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(ItemDailyViewHolder holder, int position) {
        holder.tvDay.setText(Common.convertUnixToDay(forecastWeatherResult.getDaily().getData().get(position).getTime()));
        holder.tvTempMax.setText(Common.covertFtoC(forecastWeatherResult.getDaily().getData().get(position).getTemperatureHigh()) + "°");
        holder.tvTempMin.setText(Common.covertFtoC(forecastWeatherResult.getDaily().getData().get(position).getTemperatureLow()) + "°");
        Picasso.get().load(new StringBuilder("https://darksky.net/images/weather-icons/")
                .append(forecastWeatherResult.getDaily().getData().get(position).getIcon())
                .append(".png").toString()).into(holder.iconWeather);
    }

    @Override
    public int getItemCount() {
        if (forecastWeatherResult.getDaily() == null) return 0;
        else return forecastWeatherResult.getDaily().getData().size();
    }

    public class ItemDailyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDay, tvTempMax, tvTempMin;
        private ImageView iconWeather;

        public ItemDailyViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvTempMax = itemView.findViewById(R.id.tv_temp_max);
            tvTempMin = itemView.findViewById(R.id.tv_temp_min);
            iconWeather = itemView.findViewById(R.id.img_icon);
        }
    }
}
