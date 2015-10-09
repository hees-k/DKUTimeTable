package dk.too.timetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import dk.too.timetable.DKClass.PartialTime;

public class TimeTableWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TimeTableRemoteViewsFactory(this.getApplicationContext(),
                intent);
    }
}

class TimeTableRemoteViewsFactory implements
        RemoteViewsService.RemoteViewsFactory {
    private List<PartialTime> mWidgetItems = new ArrayList<PartialTime>();
    private Context mContext;
    private int mAppWidgetId;
    private MyDB db;

    public TimeTableRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
//        mAppWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        
        db = new MyDB(context);
        db.open();
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data
        // source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred
        // to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result
        // in an ANR.

        loadData();
    }

    private void loadData() {
        // 오늘 현재 시간 이후의 수업만
        List<DKClass> list = db.DBselect();

        for (DKClass dkClass : list) {

            PartialTime[] partials = dkClass.getPartialTime();

            for (int i = 0; i < partials.length; i++) {

                if (partials[i].getHour() == -1)
                    continue;

                if (partials[i].isShowWidget()) {
                    mWidgetItems.add(partials[i]);
                }
            }
        }

        Collections.sort(mWidgetItems, new Comparator<PartialTime>() {
            @Override
            public int compare(PartialTime lhs, PartialTime rhs) {
                return lhs.getHour() - rhs.getHour();
            }
        });

        Log.d(Debug.D + "TimeTableWidgetService", "loadData");
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your
        // data source,
        // eg. cursors, connections, etc.
        db.close();
        mWidgetItems.clear();
    }

    public int getCount() {
        return mWidgetItems.size();
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file,
        // and set the
        // text based on the position.

        PartialTime p = mWidgetItems.get(position);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.appwidget_list_item);

        rv.setTextViewText(R.id.time, p.getTimeStr(mContext));
        rv.setTextViewText(R.id.room, p.getRoom());
        rv.setTextViewText(R.id.lecture, p.getLecture());

        // 클릭시 시간표 화면 띄운다.
        Intent fillInIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.list_item_container, fillInIntent);

        // Return the remote views object.
        return rv;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt()
        // is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager
        // notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do
        // heaving lifting in
        // here, synchronously. For example, if you need to process an image,
        // fetch something
        // from the network, etc., it is ok to do it here, synchronously. The
        // widget will remain
        // in its current state while work is being done here, so you don't need
        // to worry about
        // locking up the widget.
        
        mWidgetItems.clear();
        loadData();
    }
}