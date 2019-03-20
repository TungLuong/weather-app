package tl.com.weatherapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import tl.com.weatherapp.WeatherFragment;
import tl.com.weatherapp.model.WeatherResult;

public class WeatherFragmentAdapter extends FragmentStatePagerAdapter {

    List<WeatherResult> weatherResultList = new ArrayList<>();

    public WeatherFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public List<WeatherResult> getWeatherResultList() {
        return weatherResultList;
    }

    public void setWeatherResultList(List<WeatherResult> weatherResultList) {
        this.weatherResultList = weatherResultList;
    }

    @Override
    public Fragment getItem(int position) {
        return new WeatherFragment(weatherResultList.get(position),position);
    }

    @Override
    public int getCount() {
        return weatherResultList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

//    private TextView tvCityName, tvHumidity, tvPressure, tvTemperature, tvDateTime, tvWindSpeed, tvDescription, tvDewPoint, tvCloudCover, tvUVIndex, tvVisibility, tvOzone;
//    private LinearLayout linerLayout1, linerLayout2, linerLayout3;
//    private RecyclerView rcvDaily, rcvHourly;
//    private ScrollView scrollView1;
//    private NestedScrollView scrollView2;
//    private LinearLayout linearLayout;
//    //private ProgressBar loading;
//    private ImageView background;
//    private Context context;
//
//    public WeatherFragmentAdapter(List<WeatherResult> weatherResultList,Context context) {
//        super();
//        this.context = context;
//        this.weatherResultList = weatherResultList;
//    }
//
//    public List<WeatherResult> getWeatherResultList() {
//        return weatherResultList;
//    }
//
//    public void setWeatherResultList(List<WeatherResult> weatherResultList) {
//        this.weatherResultList = weatherResultList;
//        weatherResultList.size();
//    }
//
//    @Override
//    public int getCount() {
//        if (weatherResultList == null) return 0;
//        return weatherResultList.size();
//    }
//
//
//    @Override
//    public void destroyItem(ViewGroup container, int position,
//                            Object object) {
//        container.removeView((View) object);
//    }
//
//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//
//        return view == object;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        return null;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        //tao ra item view
//        LayoutInflater inflate = LayoutInflater.from(container.getContext());
//        View itemView =
//                inflate.inflate(R.layout.fragment_weather,
//                        container, false);
//        //anh xa cac view trong itemview
//        initView(itemView);
//
//        //do du lieu len
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            getWeatherInfo(weatherResultList.get(position));
//        }
//        //add itemview vao contain
//        container.addView(itemView);
//
//        return itemView;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void initView(View view) {
//
//         background = view.findViewById(R.id.background_image_view);
//         tvCityName = view.findViewById(R.id.tv_city_name);
//         tvHumidity = view.findViewById(R.id.tv_humidity);
//         tvPressure = view.findViewById(R.id.tv_pressure);
//         tvTemperature = view.findViewById(R.id.tv_temperature);
//         tvDateTime = view.findViewById(R.id.tv_date_time);
//         tvWindSpeed = view.findViewById(R.id.tv_windSpeed);
//         tvDescription = view.findViewById(R.id.tv_description);
//         tvDewPoint = view.findViewById(R.id.tv_dewPoint);
//         tvCloudCover = view.findViewById(R.id.tv_cloudCover);
//         tvUVIndex = view.findViewById(R.id.tv_uvIndex);
//         tvVisibility = view.findViewById(R.id.tv_visibility);
//         tvOzone = view.findViewById(R.id.tv_ozone);
//
//         rcvDaily = view.findViewById(R.id.rcv_daily);
//         rcvHourly = view.findViewById(R.id.rcv_hourly);
//
//          linerLayout1 = view.findViewById(R.id.liner_layout_1);
//          linerLayout2 = view.findViewById(R.id.liner_layout_2);
//          linerLayout3 = view.findViewById(R.id.liner_layout_3);
////        listView = findViewById(R.id.list_item);
////        List<String> modelList = new ArrayList<>();
////        for (int i = 0; i < 5; i++) {
////            modelList.add("List item " + i);
////        }
////
////        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_view, modelList);
////        listView.setAdapter(adapter);
//          scrollView1 = view.findViewById(R.id.scrollView_1);
//        scrollView1.setVisibility(View.INVISIBLE);
//          scrollView2 = view.findViewById(R.id.scrollView_2);
//          linearLayout = view.findViewById(R.id.linear);
//        scrollView1.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//                linerLayout1.setY(Math.max(0, scrollY));
//                linerLayout3.setY(Math.max(linerLayout1.getHeight() + scrollY, linerLayout1.getHeight() + linerLayout2.getHeight() - scrollY));
//                float al = 20.0f / (scrollY + 1) - 0.25f;
//                linerLayout2.setAlpha(al);
//                linearLayout.setY(Math.max(linerLayout1.getHeight() + linerLayout2.getHeight() + scrollY, linerLayout1.getHeight() + linerLayout2.getHeight() + linerLayout3.getHeight() - scrollY));
//                int x = scrollView1.getScrollY();
//
//
//            }
//        });
//        scrollView2.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//                if (linerLayout3.getY() > linerLayout1.getHeight() + linerLayout1.getY()) {
//                    scrollView2.scrollTo(0, 0);
//                } else scrollView2.scrollTo(0, scrollY);
//
//
//            }
//        });
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void getWeatherInfo(WeatherResult weatherResult) {
//        if (weatherResult == null){
//            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/drawable/" + "blue_sky_2");
//            Picasso.get()
//                    .load(uri)
//                    .fit()
//                    .into(background);
//
//            return;
//        }
//        String icon_name = weatherResult.getCurrently().getIcon().replace('-', '_');
//        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/drawable/" + icon_name);
//        Picasso.get()
//                .load(uri)
//                .fit()
//                .into(background);
//
//        //set RecyclerView Hourly
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        ItemHourlyWeatherAdapter itemHourlyWeatherAdapter = new ItemHourlyWeatherAdapter(weatherResult);
//        rcvHourly.setLayoutManager(layoutManager);
//        rcvHourly.setAdapter(itemHourlyWeatherAdapter);
//
//        //set RecyclerView Daily
//        LinearLayoutManager layoutManager_2 = new LinearLayoutManager(context);
//        layoutManager_2.setOrientation(LinearLayoutManager.VERTICAL);
//        ItemDailyWeatherAdapter itemDailyWeatherAdapter = new ItemDailyWeatherAdapter(weatherResult);
//        rcvDaily.setLayoutManager(layoutManager_2);
//        rcvDaily.setAdapter(itemDailyWeatherAdapter);
//
//        // load information
//        if (weatherResult.getAddress() == null) {
//            tvCityName.setText("unknown where");
//        }else tvCityName.setText(weatherResult.getAddress());
//
//
//        tvDateTime.setText(convertUnixToDate(weatherResult.getCurrently().getTime()) + "");
//        tvHumidity.setText(weatherResult.getCurrently().getHumidity() + "%");
//        tvPressure.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getPressure())).append(" hPa"));
//        tvTemperature.setText(new StringBuilder(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getTemperature()))).append("˚"));
//        tvWindSpeed.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getWindSpeed())).append("m/s"));
//        tvDescription.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getSummary())));
//        tvDewPoint.setText(new StringBuilder(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getDewPoint()))).append("˚"));
//        tvCloudCover.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getCloudCover())));
//        tvUVIndex.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getUvIndex())));
//        tvVisibility.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getVisibility())).append("+ km"));
//        tvOzone.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getOzone())).append(""));
//
//        // display
//        scrollView1.setVisibility(View.VISIBLE);
//        //loading.setVisibility(View.GONE);
//    }


}
