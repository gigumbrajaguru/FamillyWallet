package ccpe001.familywallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.kobakei.ratethisapp.RateThisApp;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mAuth = FirebaseAuth.getInstance();


        /*CaocConfig.Builder.create()
                //.errorDrawable(R.drawable.ic_custom_drawable) //default: bug image
                .apply();*/

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
                overridePendingTransition(R.animator.transition1, R.animator.transition2);
                finish();
            }
        });
        t1.start();

        //if user logged in only
        if (mAuth.getCurrentUser() != null){
            userLoginFunc(getApplication());
        }

    }

    protected void userLoginFunc(Context c){
            ccpe001.familywallet.admin.Notification noti = new ccpe001.familywallet.admin.Notification();
            noti.statusIcon(c);

            rateApi(c);
            noti.dailyReminder(c);

            //localisation
            Locale locale = null;
            prefs = c.getSharedPreferences("App Settings", c.MODE_PRIVATE);
            if(prefs.getString("appLang","English").equals("English")){
                locale = new Locale("en");
            }else {
                locale = new Locale("sin");
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            c.getResources().updateConfiguration(config, c.getResources().getDisplayMetrics());

    }

    protected void rateApi(final Context c)  {
        RateThisApp.Config config = new RateThisApp.Config(3, 8);
        config.setTitle(R.string.splash_rateapi_settitle);
        config.setMessage(R.string.splash_rateapi_setmsg);
        config.setYesButtonText(R.string.splash_rateapi_setyesbtntxt);
        config.setNoButtonText(R.string.splash_rateapi_setnobtntxt);
        config.setCancelButtonText(R.string.splash_rateapi_setcancelbtntxt);
        config.setUrl("market://details?id=" + c.getPackageName());
        RateThisApp.init(config);
        RateThisApp.showRateDialogIfNeeded(c);

        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {}

            @Override
            public void onNoClicked() {
                RateThisApp.stopRateDialog(c);
            }

            @Override
            public void onCancelClicked() {}
        });
    }



}
