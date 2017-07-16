package com.example.frank.akiya2;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    DatePicker datePicker;
    int nowMonth;
    int nowday;
    int nowYear;
    TextView textView;
    Calendar c;
    public static long leftday;

    private SharedPreferences settings;
    private Long saveday;
    long chooseday;
    private UpdateService mMyService = null;
    private String TAG = "MainService";
    private boolean isBound = false;
    int day,month,year;


    public ServiceConnection connection = new ServiceConnection() {

        // 成功與 Service 建立連線
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyService = ((UpdateService.LocalBinder) service).getService();
            Log.d(TAG, "MainActivity onServiceConnected");
        }

        // 與 Service 建立連線失敗
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMyService = null;
            Log.d(TAG, "MainActivity onServiceFailed");
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //宣告
        btn = (Button)findViewById(R.id.button);
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        textView = (TextView)findViewById(R.id.dateText);

        //抓暫存
        //儲存暫存
        settings = getSharedPreferences("data" , MODE_PRIVATE);
        saveday =  settings.getLong("day",0);
        textView.setText("還沒選擇唷");





        Intent  serviceIntent = new Intent(this, UpdateService.class);
        isBound = bindService(serviceIntent, connection, BIND_AUTO_CREATE);



    }

    public void setDay(View view)
    {
        c = Calendar.getInstance();
        nowMonth = c.get(Calendar.MONTH)+1;
        nowday = c.get(Calendar.DAY_OF_MONTH);
        nowYear = c.get(Calendar.YEAR);

        //先行定義時間格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String str = df.format(c.getTime());
        Log.d("測試Activity：",str);

        Log.d("MainActivity","setDate");

        day = datePicker.getDayOfMonth();
        month = datePicker.getMonth()+1;
        year = datePicker.getYear();


        Calendar c2 = Calendar.getInstance();
        c2.set(year,month-1,day);

        long aDayInMilliSecond = 60 * 60 * 24 * 1000;     //一天的毫秒數
        long dayDiff = (c2.getTimeInMillis() - c.getTimeInMillis()) / aDayInMilliSecond;

        Log.d("目前的時間",nowMonth+"月"+nowday+"日");
        Log.d("選擇的時間",month+"月"+day+"日");
        openToast(month,day);
        if(dayDiff >0)
        {
            textView.setText("還剩"+dayDiff +"天");
        }
        else if(dayDiff == 0)
        {
            textView.setText("是今天唷～♥");
        }
        else
        {
            textView.setText("已經過了呢");
        }

        leftday = dayDiff;
        chooseday = c2.getTimeInMillis();
        Log.d("Main","2 = "+dayDiff+" choose = "+c2.getTimeInMillis() + " c. = "+c.getTimeInMillis());


        //取得SharedPreferences ， 丟入的參數為("名稱" , 存取權限)
        settings.edit().putLong("day" , leftday).apply();
        settings.edit().putLong("chooseday" , chooseday).apply();


           if (mMyService != null)
                mMyService.buildUpdate();

    }
    private void openToast(int month,int day)
    {
        Toast.makeText(this, "選擇的時間："+month+"月"+day+"日",Toast.LENGTH_SHORT ).show();
    }
    @Override
    public void onStop() {
        super.onStop();
        settings.edit().putLong("day" , leftday).apply();

        settings.edit().putInt("cday",day).apply();
        settings.edit().putInt("cmonth",month).apply();
        settings.edit().putInt("cyear",year).apply();

        settings.edit().putLong("chooseday" , chooseday).apply();

        if (isBound) {
            unbind();
            isBound = false;
        }
        if (mMyService != null)
            mMyService.buildUpdate();

    }

    public void unbind() {
        unbindService(connection);
    }

}
