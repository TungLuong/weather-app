package tl.com.weatherapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tl.com.weatherapp.R;
import tl.com.weatherapp.model.AttributeWeather;

public class ItemAttributeWeatherAdapter extends RecyclerView.Adapter<ItemAttributeWeatherAdapter.ItemAttributeWeatherViewHolder> {



    List<AttributeWeather> list;

    public ItemAttributeWeatherAdapter(List<AttributeWeather> list) {
        this.list = list;
    }

    @Override
    public ItemAttributeWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_attribute_weather, parent, false);
        return new ItemAttributeWeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAttributeWeatherViewHolder holder, int position) {
        AttributeWeather attributeWeather = list.get(position);
        holder.attributeName.setText(attributeWeather.getKeyName());
        holder.attributeValue.setText(attributeWeather.getValue());
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public class ItemAttributeWeatherViewHolder extends RecyclerView.ViewHolder {
        private TextView attributeName;
        private TextView attributeValue;
        public ItemAttributeWeatherViewHolder(View itemView) {
            super(itemView);
            attributeName = itemView.findViewById(R.id.attribute_name);
            attributeValue = itemView.findViewById(R.id.attribute_value);
        }
    }
}
