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
import android.widget.TextView;
import ccpe001.familywallet.Dashboard;
import ccpe001.familywallet.NotificationCards;
import ccpe001.familywallet.R;
import ccpe001.familywallet.SQLiteHelper;
import ccpe001.familywallet.transaction.AddTransaction;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Context.ALARM_SERVICE;
import static ccpe001.familywallet.Dashboard.badgeCount;

/**
 * Created by harithaperera on 7/9/17.
 */
public class Notification {

    private SharedPreferences prefs;
    private android.app.Notification.Builder notification;
    private NotificationManager nm;
    private final static int STATUS_ICON = 33;
    private final static int DAILY_REMINDER = 11;
    private final static int NOTI_PPROTOTYPE = 22;

    private Calendar calendar;
    private TextView itemMessagesBadgeTextView;



    public void statusIcon(Context c){
        //displaing status icon
        PendingIntent addExpense = PendingIntent.getActivity(c,STATUS_ICON,new Intent(c, AddTransaction.class).putExtra("transactionType","Expense"),STATUS_ICON);//update here
        PendingIntent scanBill = PendingIntent.getActivity(c,STATUS_ICON,new Intent(c,SignUp.class),STATUS_ICON);//update here
        notification = new android.app.Notification.Builder(c)
                .setContentTitle("Family Wallet")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(android.app.Notification.PRIORITY_MIN)
                .setOngoing(true)
                .addAction(R.mipmap.email,"Add expense",addExpense)
                .addAction(R.mipmap.email,"Scan Bill",scanBill);

        nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(STATUS_ICON,notification.build());
    }


    //This method returns true if notification successfully created and added to DB
    public boolean addNotification(Context context,String title,String body){
        NotificationCompat.Builder notiBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTI_PPROTOTYPE,notiBuilder.build());
        new SQLiteHelper(context).addNoti(title,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(GregorianCalendar.getInstance().getTime()),body);
        badgeCount++;
        return true;
    }

    public void dailyReminder(Context c){
        prefs = c.getSharedPreferences("App Settings", c.MODE_PRIVATE);

        String remTime = prefs.getString("appDailyRem", "09:00");

        String[] arr = remTime.split(":");
        Log.d("badcount","dfdf"+Integer.parseInt(arr[0])+Integer.parseInt(arr[1]));

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr[0]));
        calendar.set(Calendar.MINUTE,Integer.parseInt(arr[1]));
        calendar.set(Calendar.SECOND,00);
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
                    .setTicker("Daily reminder")
                    .setContentTitle("Family Wallet")
                    .setContentText("Add your daily transactions to the wallet..");
            notificationManager.notify(DAILY_REMINDER,builder.build());
            new SQLiteHelper(context).addNoti("We missing you",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(GregorianCalendar.getInstance().getTime()),"Add your daily transactions to the wallet..");
            badgeCount++;
            Log.d("badcount","add on noti rec"+badgeCount);

        }
    }
}


