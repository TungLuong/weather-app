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

        //
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

        // tao danh sach cac dia diem
        listView = findViewById(R.id.list_address);
        listAddress = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(Common.DATA, MODE_PRIVATE);
        int totalAddress = sharedPreferences.getInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, 1);
        for (int i = 0; i < totalAddress; i++) {
            int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT+i,-1);
            listAddress.add(sharedPreferences.getString(Common.SHARE_PREF_ADDRESS_NAME_KEY_AT +addressId , "unknown"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listAddress);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // sua thong tin widget
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_AT +position,-1);
                editor.putInt(Common.SHARE_PREF_WIDGET_ADDRESS_ID_KEY_AT+appWidgetId,addressId);
                editor.commit();

                Toast.makeText(WidgetConfigActivity.this, "INTENT_ADDRESS_ID: "+addressId, Toast.LENGTH_SHORT).show();
                //Gui thong tin cho widget provider cap nhat thong tin
                Intent intent = new Intent(WidgetConfigActivity.this, WeatherWidget.class);
                intent.setAction(ACTION_UPDATE_CONFIG_WEATHER);
                intent.putExtra(Common.INTENT_APP_WIDGET_ID,appWidgetId);
                sendBroadcast(intent);

                // gui tin hieu ket thuc cau hinh
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
                setResult(RESULT_OK,resultValue);

                finish();
            }

        });
    }

}
