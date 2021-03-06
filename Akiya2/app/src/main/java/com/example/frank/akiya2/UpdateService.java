package com.example.frank.akiya2;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.frank.akiya2.R.id.datePicker;
import static com.example.frank.akiya2.R.id.imageView;


public class UpdateService extends Service
{
    int i=0;
    Calendar c;
    int nowMonth,nowday,nowYear;
    private SharedPreferences settings;
    Long saveday,chooseday;
    int cMonth,cday,cyear;



    public class LocalBinder extends Binder //宣告一個繼承 Binder 的類別 LocalBinder
    {
        public UpdateService getService()
        {
            return  UpdateService.this;
        }
    }
    public LocalBinder mLocBin = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        buildUpdate();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        Log.d("service","onBind");

        return mLocBin;
       // return null;
    }

    public void buildUpdate() {
        settings = getSharedPreferences("data" , MODE_PRIVATE);

        saveday =  settings.getLong("day",0);
        chooseday = settings.getLong("chooseday",0);
        cday = settings.getInt("cday",0);
        cMonth = settings.getInt("cmonth",0);
        cyear = settings.getInt("cyear",0);




        i++;
        Log.d("service","update : " + i);

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.new_app_widget);

        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.new_app_widget);
        //remoteViews.setImageViewResource(R.id.imageView,R.drawable.test2);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(new ComponentName(this, NewAppWidget.class), remoteViews);
        //remoteViews.setTextViewText(R.id.appwidget_text,"目前是"+i);




        c = Calendar.getInstance();
        nowMonth = c.get(Calendar.MONTH)+1;
        nowday = c.get(Calendar.DAY_OF_MONTH);
        nowYear = c.get(Calendar.YEAR);

        Calendar c2 = Calendar.getInstance();
        c2.set(cyear,cMonth-1,cday);

        //有延遲的時間 +個5秒
        int delay = 5000;


        long aDayInMilliSecond = 60 * 60 * 24 * 1000;     //一天的毫秒數
        long dayDiff2 = (c2.getTimeInMillis() - c.getTimeInMillis()) / aDayInMilliSecond;


        Log.d("Service","2 = "+dayDiff2+" choose = "+chooseday + " c. = "+c.getTimeInMillis());



        //先行定義時間格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        String str = df.format(c.getTime());
        Log.d("測試：",str);

        // 2. 获取RemoteViews对象
        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.new_app_widget);
        // 3. 显示时间widget
        views.setTextViewText(R.id.appwidget_text, str +"還有"+ dayDiff2+ "天");
        //views.setImageViewResource(R.id.imageView,R.drawable.test4);


        //更新圖片
        if(dayDiff2 >= 10)
        {
            //大於10天
            views.setImageViewResource(imageView,R.drawable.many);
        }
        else if(dayDiff2 == 9)
        {
            views.setImageViewResource(imageView,R.drawable.day9);
        }
        else if(dayDiff2 == 8)
        {
            views.setImageViewResource(imageView,R.drawable.day8);
        }
        else if(dayDiff2 == 7)
        {
            views.setImageViewResource(imageView,R.drawable.day7);
        }
        else if(dayDiff2 == 6)
        {
            views.setImageViewResource(imageView,R.drawable.day6);
        }
        else if(dayDiff2 == 5)
        {
            views.setImageViewResource(imageView,R.drawable.day5);
        }
        else if(dayDiff2 == 4)
        {
            views.setImageViewResource(imageView,R.drawable.day4);
        }
        else if(dayDiff2 == 3)
        {
            views.setImageViewResource(imageView,R.drawable.day3);
        }
        else if(dayDiff2 == 2)
        {
            views.setImageViewResource(imageView,R.drawable.day2);
        }
        else if(dayDiff2 == 1)
        {
            views.setImageViewResource(imageView,R.drawable.day1);
        }
        else if(dayDiff2 == 0)
        {
            views.setImageViewResource(imageView,R.drawable.day0);
        }
        else
        {
            views.setImageViewResource(imageView,R.drawable.setting);
        }


        // 4. 更新widget
        appWidgetManager.updateAppWidget(new ComponentName(this, NewAppWidget.class), views);

    }

}