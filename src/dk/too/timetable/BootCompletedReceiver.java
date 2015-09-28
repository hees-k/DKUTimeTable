package dk.too.timetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Log.d(Debug.D, "onReceive() " + Intent.ACTION_BOOT_COMPLETED);
            setAlarm(context);
        }
    }

    private void setAlarm(Context context) {

        MyDB db = new MyDB(context);
        db.open();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean useAlarm = prefs.getBoolean("useAlarm", false);
        int alarmTime = Integer.parseInt(prefs.getString("alarmTime", "10"));

        MyTimeTable.changeAlarm(context, db, useAlarm, alarmTime);

        db.close();
    }
}
