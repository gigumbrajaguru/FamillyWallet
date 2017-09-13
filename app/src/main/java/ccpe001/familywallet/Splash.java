package ccpe001.familywallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import android.widget.RelativeLayout;
import android.widget.TextView;
import ccpe001.familywallet.admin.Language_Selector;
import ccpe001.familywallet.admin.Notification;
import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kobakei.ratethisapp.RateThisApp;

import java.util.Locale;

import static java.lang.Thread.sleep;

/**
 * Created by harithaperera on 4/29/17.
 */
public class Splash extends PinActivity {

    private SharedPreferences prefs;
    private FirebaseAuth mAuth;
    public static String userID, familyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){

        }


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
                Intent intent = new Intent(Splash.this, Language_Selector.class);
                startActivity(intent);
                overridePendingTransition(R.animator.transition1, R.animator.transition2);
                finish();
            }
        });
        t1.start();


        /*Setting up shared preferences*/
        SharedPreferences sharedPref= getSharedPreferences("fwPrefs", 0);
        final SharedPreferences.Editor editor= sharedPref.edit();

        //if user logged in only
        if (mAuth.getCurrentUser() != null){
            userLoginFunc(getApplication());
            userID = mAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("UserInfo").child(userID).child("familyId").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    familyID=dataSnapshot.getValue().toString();
                    /* saving user id and family id in preferences */
                    editor.putString("uniUserID", userID);
                    editor.putString("uniFamilyID", familyID);
                    editor.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    protected void userLoginFunc(Context c){
            Notification noti = new Notification();
            noti.statusIcon(c);

            rateApi(c);
            //noti.dailyReminder(c); FIX


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
