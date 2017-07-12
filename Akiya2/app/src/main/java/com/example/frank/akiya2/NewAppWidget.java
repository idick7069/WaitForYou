package com.example.frank.akiya2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;


/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider
{
    private static final int UPDATE_DURATION = 1 * 1000; // Widget 更新间隔

    private PendingIntent pendingIntent = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("widget","onUpdate");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, UpdateService.class);
        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getService(context, 0,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), UPDATE_DURATION, pendingIntent);


    }

    @Override
    public void onDisabled(Context context) {
        Log.d("widget","onDisabled");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);


    }
    private static final String TAG="widget";

    // 没接收一次广播消息就调用一次，使用频繁
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onReceive");
        super.onReceive(context, intent);
    }



    // 没删除一个就调用一次
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        Log.d(TAG, "onDeleted");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        super.onDeleted(context, appWidgetIds);
    }

    // 当该Widget第一次添加到桌面是调用该方法，可添加多次但只第一次调用
    public void onEnabled(Context context)
    {
        Log.d(TAG, "onEnabled");
        super.onEnabled(context);
    }


}

