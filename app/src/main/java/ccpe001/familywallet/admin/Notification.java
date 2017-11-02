package ccpe001.familywallet.admin;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ccpe001.familywallet.*;
import ccpe001.familywallet.transaction.AddTransaction;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.joanzapata.iconify.widget.IconButton;
import me.leolin.shortcutbadger.ShortcutBadger;
import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Context.ALARM_SERVICE;
import static ccpe001.familywallet.Dashboard.badgeCount;
import static ccpe001.familywallet.Dashboard.setBadgeCount;

/**
 * Created by harithaperera on 7/9/17.
 */
public class Notification {

    private SharedPreferences prefs;
    private android.app.Notification.Builder notification;
    private NotificationManager nm;
    private final static int DAILY_REMINDER = 11;
    private final static int NOTI_PPROTOTYPE = 22;

    private Calendar calendar;
    private static TextView itemMessagesBadgeTextView;////////////////////
    TextView itemMessagesBadgeTextViewC;

    public Notification(TextView itemMessagesBadgeTextView){
        this.itemMessagesBadgeTextView = itemMessagesBadgeTextView;
    }

    //used only in addNotification
    public Notification(){

    }

    //displaing status icon
    public void statusIcon(Context c){
        PendingIntent addExpense = PendingIntent.getActivity(c,PendingIntent.FLAG_UPDATE_CURRENT,new Intent(c, AddTransaction.class).
                putExtra("transactionType","Expense").putExtra("Update","False").setAction("ccpe001.familywallet.AddTransaction"),PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent addIncome = PendingIntent.getActivity(c,PendingIntent.FLAG_UPDATE_CURRENT,new Intent(c, AddTransaction.class).
                putExtra("transactionType","Income").putExtra("Update","False"),PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new android.app.Notification.Builder(c)
                .setContentTitle(c.getString(R.string.noti_statusicon_setcontenttitle))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(android.app.Notification.PRIORITY_MIN)
                .setOngoing(true)
                .addAction(R.mipmap.minus,c.getString(R.string.noti_statusicon_setaction_expense),addExpense)
                .addAction(R.mipmap.add_transaction,c.getString(R.string.noti_statusicon_setaction_income),addIncome);

        nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(PendingIntent.FLAG_UPDATE_CURRENT,notification.build());
    }

    //t is null default

    //THIS FUNC PROTOTYPE IMPLEMENTED TO USE WITH BUDGET LIMITS
    //This method returns true if notification successfully created and added to DB
    public boolean addNotification(Context context,String title,String body,TextView t) {
        NotificationCompat.Builder notiBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTI_PPROTOTYPE, notiBuilder.build());
        new SQLiteHelper(context).addNoti(title, new SimpleDateFormat("yyyy-MM-dd").
                format(GregorianCalendar.getInstance().getTime()), body);
        badgeCount++;
        if (t != null){
            setBadgeCount(badgeCount, t);//setting null for itemMessagesBadgeTextView
        }
        ShortcutBadger.applyCount(context, badgeCount);
        return true;
    }

    public void dailyReminder(Context c){
        prefs = c.getSharedPreferences("App Settings", c.MODE_PRIVATE);

        String remTime = prefs.getString("appDailyRem", "09:00");

        String[] arr = remTime.split(":");
        Log.d("badcount","dfdf"+Integer.parseInt(arr[0])+"   "+Integer.parseInt(arr[1]));

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr[0]));
        calendar.set(Calendar.MINUTE,Integer.parseInt(arr[1]));
        Intent intent = new Intent(c,Notification_Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c,DAILY_REMINDER,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }


    public static class Notification_Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent newAct = new Intent(context,Dashboard.class);
            newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,DAILY_REMINDER,newAct,PendingIntent.FLAG_UPDATE_CURRENT);

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSound(soundUri)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setTicker(context.getString(R.string.noti_onrecieve_setticker))
                    .setContentTitle(context.getString(R.string.noti_onrecieve_setcontenttitle))
                    .setContentText(context.getString(R.string.noti_onrecieve_setcontenttext));
            notificationManager.notify(DAILY_REMINDER,builder.build());
            new SQLiteHelper(context).addNoti(context.getString(R.string.noti_onrecieve_setcontenttitle),new SimpleDateFormat("yyyy-MM-dd").
                    format(GregorianCalendar.getInstance().getTime()),context.getString(R.string.noti_onrecieve_setcontenttext));
            badgeCount++;
            setBadgeCount(badgeCount,itemMessagesBadgeTextView);
            ShortcutBadger.applyCount(context, badgeCount);

        }
    }

    public static class UpdateNotification extends FirebaseMessagingService {

        private TextView v;

        public UpdateNotification(){
        }

        public UpdateNotification(TextView v){
            this.v = v;
        }

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            new Notification(v).addNotification(this,getString(R.string.app_announce),remoteMessage.getNotification().getBody(),v);
        }
    }

}


