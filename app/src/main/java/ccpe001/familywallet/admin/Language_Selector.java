package ccpe001.familywallet.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import ccpe001.familywallet.IntoductionPage;
import ccpe001.familywallet.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Language_Selector extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private RadioGroup radioLang;
    private RadioButton radioBtnSin,radioBtnEng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = getSharedPreferences("First Time", Context.MODE_PRIVATE);
        if (!pref.getBoolean("isFirst",true)){
            Intent intent = new Intent("ccpe001.familywallet.SIGNIN");
            startActivity(intent);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_language__selector);

        pref = getSharedPreferences("App Settings",Context.MODE_PRIVATE);

        radioLang = (RadioGroup) findViewById(R.id.radioLang);
        radioBtnSin = (RadioButton) findViewById(R.id.radioBtnSin);
        radioBtnEng = (RadioButton) findViewById(R.id.radioBtnEng);

        radioLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);

                Locale locale = null;
                editor = pref.edit();
                if(rb.equals(radioBtnSin)) {
                    locale = new Locale("sin");
                    editor.putString("appLang","Sinhala");
                }else if(rb.equals(radioBtnEng)){
                    locale = new Locale("en");
                    editor.putString("appLang","English");
                }
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());

                editor.commit();
                startActivity(new Intent(Language_Selector.this, IntoductionPage.class));
            }
        });



    }
}
