package ccpe001.familywallet;

import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ccpe001.familywallet.admin.CircleTransform;
import ccpe001.familywallet.admin.SignIn;
import ccpe001.familywallet.admin.UserData;
import ccpe001.familywallet.budget.BudgetHandling;
import ccpe001.familywallet.budget.accUpdate;
import ccpe001.familywallet.budget.addAccount;
import ccpe001.familywallet.budget.budgetList;
import ccpe001.familywallet.summary.sumMain;
import ccpe001.familywallet.transaction.Transaction_main;
import com.facebook.Profile;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().setTitle("Dashboard");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        signUpIntent = getIntent();


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(firebaseUser.getUid());
        databaseReference.keepSynced(true);


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
                    Log.d("dff","me ran 2"+propicUrl);

                    Picasso.with(getApplication())
                            .load(propicUrl)
                            .transform(new CircleTransform())
                            .into(circleButton);
                }else{
                    storageReference.child("UserPics/" + firebaseUser.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("dff","me ran 3"+uri.toString());
                            Picasso.with(getApplication())
                                    .load(uri)
                                    .transform(new CircleTransform())
                                    .into(circleButton);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //initialize dashboard fragment 1st
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Transaction_main transaction = new Transaction_main();
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



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //load deta on login normal

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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
             Transaction_main dashboard = new Transaction_main();
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
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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


    }

}