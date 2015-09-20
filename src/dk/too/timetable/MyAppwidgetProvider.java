package dk.too.timetable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * TODO 바탕화면 위젯을 눌러 실행 할 경우, 액티비티가 하나 더 생기는 현상 수정할 것.
 */
public class MyAppwidgetProvider extends AppWidgetProvider {

    static final String SHOW_INFO = "SHOW_INFO";
    static final String UPDATE_WIDGET = "dk.too.timetable.MyAppwidgetProvider.UPDATE_WIDGET";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("dk.too.timetable",
                ".MyAppwidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        String amService = Context.ALARM_SERVICE;
        AlarmManager am = (AlarmManager) context.getSystemService(amService);

        Intent intent = new Intent(UPDATE_WIDGET);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        am.setRepeating(AlarmManager.RTC, 10000, 600000, pi);

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("dk.too.timetable",
                ".MyAppwidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        String amService = Context.ALARM_SERVICE;
        AlarmManager am = (AlarmManager) context.getSystemService(amService);
        Intent intent = new Intent(UPDATE_WIDGET);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(pi);
    }

    /* 위젯을 2개 이상 추가할 경우와 위젯 갱신 후 호출됨 */
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; i++) {
            Log.d(Debug.D + "MyAppwidgetProvider", "onUpdate " + i);

            update(context, appWidgetIds[i], appWidgetManager);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(Debug.D + "MyAppwidgetProvider",
                "onReceive " + intent.getAction());

        // 10분마다 불린다.
        if (UPDATE_WIDGET.equals(intent.getAction())) {

            AppWidgetManager man = AppWidgetManager.getInstance(context);
            int[] ids = man.getAppWidgetIds(new ComponentName(context,
                    MyAppwidgetProvider.class));

            for (int appWidgetId : ids) {
                update(context, appWidgetId, man);
            }
        }
    }

    private void update(Context context, int appWidgetId,
            AppWidgetManager appWidgetManager) {

        Intent intent = new Intent(context, TimeTableWidgetService.class);
        // Add the app widget ID to the intent extras.
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        // Instantiate the RemoteViews object for the app widget layout.
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        // Set up the RemoteViews object to use a RemoteViews adapter.
        // This adapter connects
        // to a RemoteViewsService through the specified intent.
        // This is how you populate the data.
        rv.setRemoteAdapter(appWidgetId, R.id.list_view, intent);

        // The empty view is displayed when the collection has no items.
        // It should be in the same layout used to instantiate the
        // RemoteViews
        // object above.
        rv.setEmptyView(R.id.list_view, R.id.empty_lecture);

        // 클릭시 시간표 화면 띄운다.
        Intent clickIntent = new Intent(context, MyTimeTable.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        rv.setOnClickPendingIntent(R.id.widget, pendingIntent);
        rv.setPendingIntentTemplate(R.id.list_view, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

}
