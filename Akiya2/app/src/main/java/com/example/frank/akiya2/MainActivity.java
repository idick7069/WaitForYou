package com.example.frank.akiya2;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final long RIPPLE_DURATION = 500;
    private Button btn;
    DatePicker datePicker;
    int nowMonth;
    int nowday;
    int nowYear;
    TextView textView,textView2;
    Calendar c;
    public static long leftday;

    private SharedPreferences settings;
    private Long saveday;
    long chooseday;
    private UpdateService mMyService = null;
    private String TAG = "MainService";
    private boolean isBound = false;
    int day,month,year;
    private boolean first = true;
    int cday,cmonth,cyear;
    boolean checkclick=false;


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


    //menu
    Toolbar toolbar;

    ConstraintLayout root;
    ImageView contentHamburger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        root = (ConstraintLayout) findViewById(R.id.root);
        contentHamburger = (ImageView)findViewById(R.id.content_hamburger);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();


        checkclick=false;
        //宣告
        btn = (Button)findViewById(R.id.button);
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        textView = (TextView)findViewById(R.id.dateText);
        textView2 = (TextView)findViewById(R.id.textView);

        datePicker.setScaleX(0.9f);
        datePicker.setScaleY(0.9f);
        //抓暫存
        //儲存暫存
        settings = getSharedPreferences("data" , MODE_PRIVATE);
        saveday =  settings.getLong("day",0);




        first  = settings.getBoolean("first",true);
        if(!first)
        {
            if(saveday >0)
            {
                textView.setText("還剩"+saveday +"天");

            }
            else if(saveday == 0)
            {
                textView.setText("是今天唷～♥");
            }
            else
            {
                textView.setText("已經過了呢");
            }

            cday = settings.getInt("cday",0);
            cmonth = settings.getInt("cmonth",0);
            cyear =settings.getInt("cyear",0);

            if(cmonth != 0 || cday != 0)
            {
                textView2.setText(cmonth + "月" + cday +"日");
            }

        }
        else
        {
            firstToast();
            textView2.setText("");
            textView.setText("還沒選擇唷");


        }


        Intent  serviceIntent = new Intent(this, UpdateService.class);
        isBound = bindService(serviceIntent, connection, BIND_AUTO_CREATE);



    }

    public void setDay(View view)
    {
        checkclick = true;
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
        textView2.setText(month + "月" + day +"日");

           if (mMyService != null)
                mMyService.buildUpdate();

    }
    private void openToast(int month,int day)
    {
        Toast.makeText(this, "選擇的時間："+month+"月"+day+"日",Toast.LENGTH_SHORT ).show();
    }
    private void firstToast()
    {
        Toast.makeText(this,"第一次開",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(checkclick)
        {
            settings.edit().putLong("day" , leftday).apply();

            settings.edit().putInt("cday",day).apply();
            settings.edit().putInt("cmonth",month).apply();
            settings.edit().putInt("cyear",year).apply();

            settings.edit().putLong("chooseday" , chooseday).apply();
        }
//        settings.edit().putLong("day" , leftday).apply();
//
//        settings.edit().putInt("cday",day).apply();
//        settings.edit().putInt("cmonth",month).apply();
//        settings.edit().putInt("cyear",year).apply();
//
//        settings.edit().putLong("chooseday" , chooseday).apply();

        if (isBound) {
            unbind();
            isBound = false;
        }
        if (mMyService != null) {
            mMyService.buildUpdate();
        }
        if(first)
        {
            settings.edit().putBoolean("first",false).apply();
        }
    }

    public void unbind() {
        unbindService(connection);
    }

    public void openDialog(View view) {
        new AlertDialog.Builder(this)
                .setTitle("來自剛剛的訊息")
                .setMessage("親愛的：\n" +
                        "因為我們遠距離常常需要等待\n" +
                        "所以我想用這種方式讓等待的時間比較不那麼孤單\n" +
                        "也算是我的手作禮物吧？\n" +
                        "可能會有Bug，畫面也不是很好看\n" +
                        "但還是希望你會喜歡囉\n" +
                        "畢竟你最愛我了對吧？<3")
                .setPositiveButton("對呀最愛你了<3", null)
                .setNegativeButton("沒..沒有啦<3", null)
                .show();
    }
    public void openProfile(View view)
    {
        final Dialog dialog = new Dialog(MainActivity.this,R.style.MyDialog);//指定自定義樣式
        dialog.setContentView(R.layout.dialoglayout);//指定自定義layout

//新增自定義按鈕點擊監聽
        Button btn = (Button)dialog.findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();;
            }
        });

//顯示dialog
        dialog.show();
    }

}
