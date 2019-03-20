package tl.com.weatherapp.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tl.com.weatherapp.IListenerDelete;
import tl.com.weatherapp.R;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.WeatherResult;

public class ItemWeatherAddressAdapter extends RecyclerView.Adapter<ItemWeatherAddressAdapter.ItemWeatherAddressViewHolder> {

    private List<WeatherResult> weatherResultList;
    private Context mContext;
    private IListenerDelete iListenerDelete;
    public ItemWeatherAddressAdapter(List<WeatherResult> weatherResultList, Context mContext, IListenerDelete iListenerDelete) {
        this.weatherResultList = weatherResultList;
        this.mContext = mContext;
        this.iListenerDelete = iListenerDelete;
    }

    @NonNull
    @Override
    public ItemWeatherAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_address, parent, false);
        return new ItemWeatherAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemWeatherAddressViewHolder holder, final int position) {

        if (weatherResultList.get(position) == null) return;
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.btnDelete.getVisibility() == View.VISIBLE && position != Common.COUNT_CURRENT_ADDRESS ){
                    holder.btnDelete.setVisibility(View.GONE);
                }else if (holder.btnDelete.getVisibility() == View.GONE && position != Common.COUNT_CURRENT_ADDRESS){
                    holder.btnDelete.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iListenerDelete.deleteItem(position);
            }
        });
        String icon_name = weatherResultList.get(position).getCurrently().getIcon().replace('-', '_');
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/drawable/" + icon_name);
        Picasso.get()
                .load(uri)
                .fit()
                .into(holder.background);
        if(position == 0){
            holder.icLocation.setVisibility(View.VISIBLE);
        }else {
            holder.icLocation.setVisibility(View.GONE);
        }

//        Picasso.get().load(new StringBuilder("https://darksky.net/images/weather-icons/")
//                .append(weatherResultList.get(position).getCurrently().getIcon())
//                .append(".png").toString()).into(holder.icWeather);

        holder.tvTemp.setText(Common.covertFtoC(weatherResultList.get(position).getCurrently().getTemperature()) + "°");
        holder.tvAddress.setText(weatherResultList.get(position).getAddress());
        String lastTimeUpdate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            lastTimeUpdate = "Last update " + Common.convertUnixToTime(weatherResultList.get(position).getCurrently().getTime());
        }
        holder.tvTimeLastUpdate.setText(lastTimeUpdate);

    }

    @Override
    public int getItemCount() {
        if (weatherResultList == null) return 0;
        return weatherResultList.size();
    }

    public class ItemWeatherAddressViewHolder extends RecyclerView.ViewHolder {
        private ImageView background;
        private TextView tvAddress;
        private TextView tvTimeLastUpdate;
        private ImageView icLocation;
        private ImageView icWeather;
        private TextView tvTemp;
        private TextView btnDelete;
        public ItemWeatherAddressViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvTimeLastUpdate = itemView.findViewById(R.id.tv_time_update);
            tvTemp = itemView.findViewById(R.id.tv_temp);
            icWeather = itemView.findViewById(R.id.img_icon);
            icLocation = itemView.findViewById(R.id.is_location_enable);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
