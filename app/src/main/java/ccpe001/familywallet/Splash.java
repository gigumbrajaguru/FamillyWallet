package ccpe001.familywallet;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import ccpe001.familywallet.admin.SignIn;
import ccpe001.familywallet.admin.SignUp;
import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.kobakei.ratethisapp.RateThisApp;

import java.util.Calendar;
import java.util.Locale;

import static java.lang.Thread.sleep;

/**
 * Created by harithaperera on 4/29/17.
 */
public class Splash extends PinActivity {

    private SharedPreferences prefs;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mAuth = FirebaseAuth.getInstance();

        prefs = getSharedPreferences("App Settings",Context.MODE_PRIVATE);


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
                overridePendingTransition(R.animator.transition1,R.animator.transition2);
                finish();
            }
        });
        t1.start();

        //if user logged in only
        if(mAuth.getCurrentUser() != null){
            ccpe001.familywallet.admin.Notification noti = new ccpe001.familywallet.admin.Notification();
            noti.statusIcon(getApplication());

            noti.dailyReminder(getApplication());
            rateApi();


            //localisation
            Locale locale = null;
            if(prefs.getString("appLang","English").equals("English")){
                locale = new Locale("en");
            }else {
                locale = new Locale("sin");
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }


    }


    protected void rateApi(){
        RateThisApp.Config config = new RateThisApp.Config(3, 8);
        config.setTitle(R.string.splash_rateapi_settitle);
        config.setMessage(R.string.splash_rateapi_setmsg);
        config.setYesButtonText(R.string.splash_rateapi_setyesbtntxt);
        config.setNoButtonText(R.string.splash_rateapi_setnobtntxt);
        config.setCancelButtonText(R.string.splash_rateapi_setcancelbtntxt);
        config.setUrl("market://details?id=" + getPackageName());
        RateThisApp.init(config);
        RateThisApp.onCreate(getApplication());
        RateThisApp.showRateDialogIfNeeded(getApplication());

            RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {}

            @Override
            public void onNoClicked() {
                RateThisApp.stopRateDialog(getApplication());
            }

            @Override
            public void onCancelClicked() {}
        });
    }



}
