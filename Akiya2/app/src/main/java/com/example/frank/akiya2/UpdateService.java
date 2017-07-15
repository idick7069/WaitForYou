package com.example.frank.akiya2;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class UpdateService extends Service
{
    int i=0;
    Calendar c;
    int nowMonth,nowday,nowYear;
    private SharedPreferences settings;
    Long saveday,chooseday;


    public class LocalBinder extends Binder //宣告一個繼承 Binder 的類別 LocalBinder
    {
        UpdateService getService()
        {
            return  UpdateService.this;
        }
    }
    private LocalBinder mLocBin = new LocalBinder();

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



        i++;
        Log.d("service","update : " + i);

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.new_app_widget);

        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.new_app_widget);
        //remoteViews.setImageViewResource(R.id.imageView,R.drawable.test2);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(new ComponentName(this, NewAppWidget.class), remoteViews);
        //remoteViews.setTextViewText(R.id.appwidget_text,"目前是"+i);

/*

        //1. 获取当前时间
        Date now = new Date(); String strNow = now.toLocaleString();
        // 2. 获取RemoteViews对象
        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.new_app_widget);
        // 3. 显示时间widget
        views.setTextViewText(R.id.appwidget_text, strNow);
        // 4. 更新widget
        appWidgetManager.updateAppWidget(new ComponentName(this, NewAppWidget.class), views);

*/
        c = Calendar.getInstance();
        nowMonth = c.get(Calendar.MONTH)+1;
        nowday = c.get(Calendar.DAY_OF_MONTH);
        nowYear = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);


        long aDayInMilliSecond = 60 * 60 * 24 * 1000;     //一天的毫秒數
        long dayDiff = (chooseday - c.getTimeInMillis()) / aDayInMilliSecond;

        //先行定義時間格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        String str = df.format(c.getTime());
        Log.d("測試：",str);

        // 2. 获取RemoteViews对象
        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.new_app_widget);
        // 3. 显示时间widget
        views.setTextViewText(R.id.appwidget_text, str +"還有"+ dayDiff + "天");
        //views.setImageViewResource(R.id.imageView,R.drawable.test4);

        //更新圖片
        if(dayDiff >= 10)
        {
            views.setImageViewResource(R.id.imageView,R.drawable.test4);
        }
        else if(dayDiff >= 5 && dayDiff <10)
        {
            views.setImageViewResource(R.id.imageView,R.drawable.test3);
        }
        else if(dayDiff >2 && dayDiff <5)
        {
            views.setImageViewResource(R.id.imageView,R.drawable.test2);
        }
        else
        {
            views.setImageViewResource(R.id.imageView,R.drawable.test1);
        }


        // 4. 更新widget
        appWidgetManager.updateAppWidget(new ComponentName(this, NewAppWidget.class), views);

    }

}