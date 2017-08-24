package ccpe001.familywallet.transaction;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import ccpe001.familywallet.R;

public class TransactionCategory extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toolbar categoryToolbar;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_category);

        if (savedInstanceState == null) {
            Bundle extras = this.getIntent().getExtras();
            if(extras == null) {
            } else {
                type = extras.getString("transactionType");

            }
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        categoryToolbar = (Toolbar) findViewById(R.id.categoryToolbar);
        categoryToolbar.setTitle("Categories");

        setSupportActionBar(categoryToolbar);
        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transaction_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*Setting the tab fragments according to the selection(Income/Expense)*/
        @Override
        public Fragment getItem(int position) {
            if (type.equals("Income")) {
                switch (position) {
                    case 0 :
                        CategoryTab02 tab2 = new CategoryTab02();
                        return tab2;
                    case 1:
                        CategoryTab01 tab1 = new CategoryTab01();
                        return tab1;
                    default:
                        return null;
                }
            } else if (type.equals("Expense")) {
                switch (position) {
                    case 0 :
                        CategoryTab01 tab1 = new CategoryTab01();
                        return tab1;
                    case 1:
                        CategoryTab02 tab2 = new CategoryTab02();
                        return tab2;

                    default:
                        return null;
                }
            }
            return null;
        }

        /*Setting total no of tabs*/
        @Override
        public int getCount() {
            return 2;
        }

        /*Setting the tab titles according to the selection(Income/Expense)*/
        @Override
        public CharSequence getPageTitle(int position) {
            if (type.equals("Income")) {
                switch (position) {
                    case 0:
                        return "Income";
                    case 1:
                        return "Expense";
                }
            } else if (type.equals("Expense")) {
                switch (position) {
                    case 0:
                        return "Expense";
                    case 1:
                        return "Income";
                }
            }
            return null;
        }
    }
}
