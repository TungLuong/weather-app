package tl.com.weatherapp;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tl.com.weatherapp.common.Common;

import static tl.com.weatherapp.common.Common.ACTION_UPDATE_CONFIG_WEATHER;

public class WidgetConfigActivity extends AppCompatActivity {

    private static int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private ListView listView;
    private List<String> listAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        setResult(RESULT_CANCELED,resultValue);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        listView = findViewById(R.id.list_address);
        listAddress = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(Common.DATA, MODE_PRIVATE);
        int totalAddress = sharedPreferences.getInt("TOTAL_ADDRESS", 1);
        for (int i = 0; i < totalAddress; i++) {
            listAddress.add(sharedPreferences.getString("ADDRESS_NAME" + i, "unknown"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listAddress);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(WidgetConfigActivity.this, listAddress.get(position), Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("WIDGET_ADDRESS_ID"+appWidgetId);
                editor.putInt("WIDGET_ADDRESS_ID"+appWidgetId,position);
                editor.commit();

                Intent intent = new Intent(WidgetConfigActivity.this, WeatherWidget.class);
                intent.setAction(ACTION_UPDATE_CONFIG_WEATHER);
                intent.putExtra("APP_WIDGET_ID",appWidgetId);
                sendBroadcast(intent);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
                setResult(RESULT_OK,resultValue);

                finish();
            }

        });
    }

    private static void updateWeatherWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
