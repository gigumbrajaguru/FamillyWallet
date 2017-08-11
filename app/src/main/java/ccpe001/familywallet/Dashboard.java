package ccpe001.familywallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import android.widget.TextView;
import ccpe001.familywallet.admin.CircleTransform;
import ccpe001.familywallet.admin.Notification;
import ccpe001.familywallet.admin.UserData;
import ccpe001.familywallet.budget.accUpdate;
import ccpe001.familywallet.budget.addAccount;
import ccpe001.familywallet.budget.budgetList;
import ccpe001.familywallet.summary.sumMain;
import ccpe001.familywallet.transaction.TransactionMain;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.joanzapata.iconify.widget.IconButton;
import com.kobakei.ratethisapp.RateThisApp;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

    private Toolbar toolbar = null;
    private NavigationView navigationView = null;
    private DrawerLayout drawerLayout = null;
    private FloatingActionButton circleButton;
    private Spinner navUserDetTxt;
    private FirebaseAuth mAuth;
    private String[] arrSpinner;
    private Intent signUpIntent;

    public String fullname;
    public String propicUrl;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private UserData userData;
    private SharedPreferences prefs;
    public static int badgeCount = 0;
    private int animateCounter = 0;
    private ShowcaseView showcaseView;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().setTitle("Dashboard");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        signUpIntent = getIntent();

        //display help menu if app first installed
        pref = getSharedPreferences("First Time",Context.MODE_PRIVATE);
        if(pref.getBoolean("isFirst",true)){
            animateMenu();
        }
        setFirst(false);


        badgeCount = new SQLiteHelper(getApplication()).viewNoti().size();//LOAD ONCE

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){

        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(firebaseUser.getUid());
        databaseReference.keepSynced(true);

        prefs = getSharedPreferences("App Settings", Context.MODE_PRIVATE);
        PeriodicBackupCaller.backupRunner(getApplication(),prefs.getString("appBackUp","No Auto Backups"));

        storageReference = FirebaseStorage.getInstance().getReference();

            databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userData = new UserData();
                    if (ds.getKey().equals("firstName")){
                        userData.setFirstName(ds.getValue().toString());
                        fullname = userData.getFirstName();
                    }
                    else if(ds.getKey().equals("lastName")) {
                        userData.setLastName(ds.getValue().toString());
                        fullname = fullname + " "+userData.getLastName();
                    }
                    else if(ds.getKey().equals("proPic")) {
                        try {
                            userData.setProPic(ds.getValue().toString());
                            propicUrl = userData.getProPic();
                        } catch (Exception e) {

                        }
                    }
                }

                if (signUpIntent.getStringExtra("firstname") != null
                        && signUpIntent.getStringExtra("lastname") != null) {

                    fullname = signUpIntent.getStringExtra("firstname") + " " +
                            signUpIntent.getStringExtra("lastname");
                }

                arrSpinner = new String[]{fullname};//add more elems dynamically
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, arrSpinner);
                navUserDetTxt.setAdapter(adapter);

                //cannot use one method for this call are asynchrous
                if (signUpIntent.getStringExtra("profilepic") != null) {
                    Picasso.with(getApplication())
                            .load(Uri.parse(signUpIntent.getStringExtra("profilepic")))
                            .transform(new CircleTransform())
                            .into(circleButton);

                }else if(mAuth.getCurrentUser().getProviders().toString().equals("[facebook.com]")
                        ||mAuth.getCurrentUser().getProviders().toString().equals("[google.com]")){

                    Picasso.with(getApplication())
                            .load(propicUrl)
                            .transform(new CircleTransform())
                            .into(circleButton);
                }else{
                    storageReference.child("UserPics/" + firebaseUser.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(getApplication())
                                    .load(uri)
                                    .transform(new CircleTransform())
                                    .into(circleButton);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        //initialize dashboard fragment 1st
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TransactionMain transaction = new TransactionMain();
        fragmentTransaction.replace(R.id.fragmentContainer1,transaction);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_navigation_drawer);
        navUserDetTxt = (Spinner) headerView.findViewById(R.id.navUserDet);
        circleButton = (FloatingActionButton) headerView.findViewById(R.id.loggedUsrImg);
        circleButton.setOnClickListener(this);

    }




    public static void setBadgeCount(int badgeCount,TextView tVw){
        if(badgeCount<=0) {
            tVw.setVisibility(View.GONE); // initially hidden
        }else {
            tVw.setVisibility(View.VISIBLE);
            tVw.setText(" "+badgeCount);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);

        MenuItem itemMessages = menu.findItem(R.id.action_notification);
        RelativeLayout badgeLayout = (RelativeLayout) itemMessages.getActionView();
        TextView itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.id.badge_textView);
        IconButton iconButtonMessages = (IconButton) badgeLayout.findViewById(R.id.badge_icon_button);
        Log.d("badgeCount","onCreateOptionsMenu"+badgeCount);
        setBadgeCount(badgeCount,itemMessagesBadgeTextView);

        iconButtonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                NotificationCards noti = new NotificationCards();
                fragmentTransaction.replace(R.id.fragmentContainer1,noti);
                fragmentTransaction.commit();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.transactionFrag) {
             android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
             android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
             TransactionMain dashboard = new TransactionMain();
             fragmentTransaction.replace(R.id.fragmentContainer1,dashboard);
             fragmentTransaction.commit();
        } else if (id == R.id.reportsFrag) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            sumMain summary = new sumMain();
            fragmentTransaction.replace(R.id.fragmentContainer1,summary);
            fragmentTransaction.commit();
        } else if (id == R.id.transferFrag) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            accUpdate transmoney = new accUpdate();
            fragmentTransaction.replace(R.id.fragmentContainer1,transmoney);
            fragmentTransaction.commit();
        }else if (id == R.id.budgetFrag) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            budgetList budget = new budgetList();
            fragmentTransaction.replace(R.id.fragmentContainer1,budget);
            fragmentTransaction.commit();
        }else if (id == R.id.walletFrag) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            addAccount addwallet = new addAccount();
            fragmentTransaction.replace(R.id.fragmentContainer1,addwallet);
            fragmentTransaction.commit();
        }else if (id == R.id.settingFrag) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Settings setting = new Settings();
            fragmentTransaction.replace(R.id.fragmentContainer1,setting);
            fragmentTransaction.commit();
        }else if (id == R.id.backupFrag) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ExportData backup = new ExportData();
            fragmentTransaction.replace(R.id.fragmentContainer1,backup);
            fragmentTransaction.commit();
        }else if(id == R.id.helpFrag){
            animateMenu();
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void animateMenu(){
        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(Target.NONE)
                .setContentTitle(R.string.dashboard_animatemenu_setcontitle)
                .setContentText(R.string.dashboard_animatemenu_setcontext)
                .setOnClickListener(this)
                .build();
        showcaseView.setButtonText(getString(R.string.intropage_onPageSelected_else_setext));
    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.loggedUsrImg){
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            AddMember addmember = new AddMember();
            fragmentTransaction.replace(R.id.fragmentContainer1,addmember);
            fragmentTransaction.commit();
            //Close nav drawer here
            drawerLayout.closeDrawer(GravityCompat.START);
        }


        //for each click on btn
        ViewTarget navigationButtonViewTarget = null;
        try {
            navigationButtonViewTarget = ViewTargets.navigationButtonViewTarget(toolbar);
        } catch (ViewTargets.MissingViewException e) {
            e.printStackTrace();
        }
        switch (animateCounter) {
            case 0:
                showcaseView.setShowcase(navigationButtonViewTarget, true);
                showcaseView.setContentTitle(getString(R.string.dashboard_onclick_0_setcontitle));
                showcaseView.setContentText(getString(R.string.dashboard_onclick_0_setconttext));
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.action_notification)), true);
                showcaseView.setContentTitle(getString(R.string.dashboard_onclick_1_setcontitle));
                showcaseView.setContentText(getString(R.string.dashboard_onclick_1_setconttext));
                break;

            case 2:
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.action_search)), true);
                showcaseView.setContentTitle(getString(R.string.dashboard_onclick_2_setcontitle));
                showcaseView.setContentText(getString(R.string.dashboard_onclick_2_setconttext));
                break;

            case 3:
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.fabMain)), true);
                showcaseView.setContentTitle(getString(R.string.dashboard_onclick_3_setcontitle));
                showcaseView.setContentText(getString(R.string.dashboard_onclick_3_setconttext));
                showcaseView.setButtonText(getString(R.string.dashboard_onclick_3_setbtntext));
                break;

            case 4:
                showcaseView.hide();
                animateCounter = 0;
                break;
        }
        animateCounter++;
    }

    private void setFirst(boolean isFirst){
        editor = pref.edit();
        editor.putBoolean("isFirst",isFirst);
        editor.commit();
    }


}