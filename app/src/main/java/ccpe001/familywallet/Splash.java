package ccpe001.familywallet;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import ccpe001.familywallet.admin.SignIn;
import ccpe001.familywallet.admin.SignUp;
import com.github.orangegangsters.lollipin.lib.PinActivity;

import java.util.Calendar;

import static java.lang.Thread.sleep;

/**
 * Created by harithaperera on 4/29/17.
 */
public class Splash extends PinActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent("ccpe001.familywallet.INTRODUCTIONPAGE");
                startActivity(intent);
                overridePendingTransition(R.animator.transition1,R.animator.transition2);//for animations(shoul have 2 files)
                finish();
            }
        });
        t1.start();

        ccpe001.familywallet.admin.Notification noti = new ccpe001.familywallet.admin.Notification();
        noti.statusIcon(getApplication());
        noti.dailyReminder(getApplication());

    }




}
