package com.example.frank.akiya2;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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


    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder)
        {
            // TODO Auto-generated method stub
            mMyService = ((UpdateService.LocalBinder)serviceBinder).getService();
        }

        public void onServiceDisconnected(ComponentName name)
        {
            // TODO Auto-generated method stub
            Log.d("LOG_TAG", "onServiceDisconnected()" + name.getClassName());
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
        textView.setText("還剩："+saveday +"天");


        c = Calendar.getInstance();
        nowMonth = c.get(Calendar.MONTH)+1;
        nowday = c.get(Calendar.DAY_OF_MONTH);
        nowYear = c.get(Calendar.YEAR);


        //先行定義時間格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String str = df.format(c.getTime());
        Log.d("測試Activity：",str);


    }

    public void stopService(View view)
    {
        Intent it = new Intent(MainActivity.this, UpdateService.class);
        stopService(it); //結束Service
    }

    public void setDay(View view)
    {

        Log.d("MainActivity","setDate");
        int day,month,year;
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
        textView.setText("還剩："+dayDiff +"天");
        leftday = dayDiff;
        chooseday = c2.getTimeInMillis();


        //取得SharedPreferences ， 丟入的參數為("名稱" , 存取權限)

        settings.edit().putLong("day" , leftday).apply();
        settings.edit().putLong("chooseday" , chooseday).apply();


        if (mMyService != null)
            //mMyService.myMethod(); //透過bindService()可以使用Service中的方法
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
        settings.edit().putLong("chooseday" , chooseday).apply();

    }

}
