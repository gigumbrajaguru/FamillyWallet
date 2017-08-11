package ccpe001.familywallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by harithaperera on 5/28/17.
 */
public class IntoductionPage extends AppCompatActivity implements ViewPager.OnPageChangeListener,View.OnClickListener{

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private ViewPager viewPager;
    private int[] layouts;
    private Button nextBtn,skipBtn;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("First Time",Context.MODE_PRIVATE);
        if (!pref.getBoolean("isFirst",true)){
            Intent intent = new Intent("ccpe001.familywallet.SIGNIN");
            startActivity(intent);
        }

        setContentView(R.layout.introcontainer);
        init();
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.vPager);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        skipBtn = (Button) findViewById(R.id.skipBtn);

        layouts = new int[] {R.layout.activity_screen1,R.layout.activity_screen2,R.layout.activity_screen3};

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);
        nextBtn.setOnClickListener(this);
        skipBtn.setOnClickListener(this);
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position==layouts.length-1){
            nextBtn.setText(R.string.intropage_onPageSelected_setext);
            skipBtn.setVisibility(View.GONE);
        }else {
            nextBtn.setText(R.string.intropage_onPageSelected_else_setext);
            skipBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.skipBtn){
            Intent intent = new Intent("ccpe001.familywallet.SIGNIN");
            startActivity(intent);
        }else if(view.getId()==R.id.nextBtn){
            int currItem = viewPager.getCurrentItem()+1;
            if(currItem<layouts.length){
                viewPager.setCurrentItem(currItem);
            }else {
                Intent intent = new Intent("ccpe001.familywallet.SIGNIN");
                startActivity(intent);
            }
        }
    }



    public class ViewPagerAdapter extends PagerAdapter{

        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position],container,false);
            container.addView(view);
            return view;
        }


        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View)object;
            container.removeView(view);
        }
    }
}
