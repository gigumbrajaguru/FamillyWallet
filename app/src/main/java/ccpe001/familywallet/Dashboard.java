package ccpe001.familywallet;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.joanzapata.iconify.widget.IconButton;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import ccpe001.familywallet.admin.CircleTransform;
import ccpe001.familywallet.admin.GetInfo;
import ccpe001.familywallet.admin.UserData;
import ccpe001.familywallet.budget.ActionValidater;
import ccpe001.familywallet.budget.AddAccount;
import ccpe001.familywallet.budget.AutoTracking;
import ccpe001.familywallet.budget.BudgetList;
import ccpe001.familywallet.summary.SummaryTab;
import ccpe001.familywallet.transaction.FamilyTransactions;
import ccpe001.familywallet.transaction.TransactionMain;
import ccpe001.familywallet.transaction.TransactionRecurring;


public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar = null;
    private NavigationView navigationView = null;
    private DrawerLayout drawerLayout = null;
    private FloatingActionButton circleButton;
    private Spinner navUserDetTxt;
    private FirebaseAuth mAuth;
    private Intent signUpIntent;

    public String fullname;
    public String propicUrl;
    private String userID, familyID, fname;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private UserData userData;
    private SharedPreferences prefs;
    public static int badgeCount = 0;
    private final static int PERMENT_NOT = 33;
    private int animateCounter = 0;
    private final static int RC_SIGN_IN = 0;

    private ShowcaseView showcaseView;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref,pref2;
    private TextView itemMessagesBadgeTextView;
    private DrawerLayout layout;
    private Snackbar snackbar;
    private CallbackManager callbackManager;
    private CustomAlertDialogs alert;
    private NotificationManager notificationManager;
    private GoogleApiClient mGoogleApiClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar.setTitle(R.string.dashboard_settitle_overview);
        setSupportActionBar(toolbar);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_navigation_drawer);
        navUserDetTxt = (Spinner) headerView.findViewById(R.id.navUserDet);
        circleButton = (FloatingActionButton) headerView.findViewById(R.id.loggedUsrImg);


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

        /*Setting up shared preferences*/
        SharedPreferences sharedPref= getSharedPreferences("fwPrefs", 0);
        final SharedPreferences.Editor editor= sharedPref.edit();

        userID = firebaseUser.getUid();
        FirebaseDatabase.getInstance().getReference("UserInfo").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                familyID=dataSnapshot.child("familyId").getValue().toString();
                fname=dataSnapshot.child("firstName").getValue().toString()+" "+dataSnapshot.child("lastName").getValue().toString();
                /* saving user id, family id and first name in preferences */
                editor.putString("uniUserID", userID);
                editor.putString("uniFamilyID", familyID);
                editor.putString("uniFname", fname);
                editor.commit();
                groupStatus(userID,familyID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(firebaseUser.getUid());
        databaseReference.keepSynced(true);

        prefs = getSharedPreferences("App Settings", Context.MODE_PRIVATE);
        PeriodicBackupCaller.backupRunner(getApplication(),prefs.getString("appBackUp","No Auto Backups"));

        if (mAuth.getCurrentUser() != null) {
            new Splash().userLoginFunc(getApplication());
        }

        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userData = new UserData();
                    if (ds.getKey().equals("firstName")){
                        Log.d("lol", String.valueOf(ds.getValue()));
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

                navUserDetTxt.setPrompt(fullname);
                navUserDetTxt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i==0){
                            startActivity(new Intent(Dashboard.this,GetInfo.class));
                        }else if(i==1){
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            AddMember addMember = new AddMember();
                            fragmentTransaction.replace(R.id.fragmentContainer1,addMember);
                            fragmentTransaction.commit();
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }else if(i==2){
                            alert = new CustomAlertDialogs();
                            alert.initLoadingPage(Dashboard.this);

                            //sign out & del daily rem,auto backups,noti icon,clear session
                            if(mAuth.getCurrentUser().getProviders().toString().equals("[facebook.com]")){
                                LoginManager.getInstance().logOut();
                            }else if(mAuth.getCurrentUser().getProviders().toString().equals("[google.com]")){
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            }
                            mAuth.signOut();
                            SharedPreferences sharedPref= getSharedPreferences("fwPrefs",0);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();      //clear all data.
                            editor.commit();  //commit change to SharedPreferences.
                            NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context
                                    .NOTIFICATION_SERVICE);
                            mNotificationManager.cancel(PERMENT_NOT);

                            PeriodicBackupCaller.backupRunner(getApplication(),getString(R.string.nobackup));

                            //off noti here
                            notificationManager = (NotificationManager) getSystemService(Context
                                    .NOTIFICATION_SERVICE);
                            notificationManager.cancelAll();

                            finish();
                            Settings.sessionClear(getApplication());
                            startActivity(new Intent("ccpe001.familywallet.SIGNIN"));
                        }
                        navUserDetTxt.setPrompt(fullname);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        navUserDetTxt.setPrompt(fullname);
                    }
                });


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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        //if it is a a unnkown login
        if(mAuth.getCurrentUser().getProviders().toString().equals("[]")) {
            //layout.setPadding(0,0,0,(int) (-200*getResources().getDisplayMetrics().density + 0.5f));
            snackbar = Snackbar
                    .make(layout, R.string.dashboard_snackbar_demopermanent, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.dashboard_snackbar_demopermanentBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                            builder.setTitle(R.string.dashboard_socialbuilder_settitle)
                                    .setItems(R.array.social_logins, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which == 0){
                                                final EditText emailTxt,pwTxt;

                                                final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(Dashboard.this);
                                                View alertDiaView = getLayoutInflater().inflate(R.layout.get_credentials,null);
                                                nameBuilder.setView(alertDiaView);
                                                emailTxt = (EditText) alertDiaView.findViewById(R.id.email);
                                                pwTxt = (EditText) alertDiaView.findViewById(R.id.password);

                                                nameBuilder.setPositiveButton(R.string.dashboard_social_demo_signup, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                if (Validate.anyValidMail(emailTxt.getText().toString().trim())) {
                                                                    if (Validate.anyValidPass(pwTxt.getText().toString().trim())) {
                                                                        alert = new CustomAlertDialogs();
                                                                        alert.initLoadingPage(Dashboard.this);
                                                                        mAuth.getCurrentUser().linkWithCredential(EmailAuthProvider.getCredential(emailTxt.getText().toString(),
                                                                                pwTxt.getText().toString()))
                                                                                .addOnCompleteListener(Dashboard.this, new OnCompleteListener<AuthResult>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            alert.initCommonDialogPage(Dashboard.this,getString(R.string.dashboard_permentmem_sucess),false).show();
                                                                                            Intent intent = new Intent("ccpe001.familywallet.GETINFO");
                                                                                            startActivity(intent);
                                                                                        } else {
                                                                                            alert.hideLoadingPage();
                                                                                            try {
                                                                                                throw task.getException();
                                                                                            }catch (FirebaseNetworkException e) {
                                                                                                alert.initCommonDialogPage(Dashboard.this,getString(R.string.network_error),true).show();
                                                                                            }catch (FirebaseAuthUserCollisionException invalidEmail) {
                                                                                                emailTxt.setError(getString(R.string.signup_already_email_text));
                                                                                            } catch (Exception e) {
                                                                                                alert.initCommonDialogPage(Dashboard.this, getString(R.string.common_error), true).show();
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });

                                                                    } else {
                                                                        Log.d("df","df");
                                                                        pwTxt.setError(getString(R.string.signup_onclick_passerr));
                                                                    }
                                                                } else {
                                                                    emailTxt.setError(getString(R.string.signup_onclick_emailerr));
                                                                }
                                                            }
                                                        }).setNegativeButton(R.string.setting_pinbuilder_negbtn, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.dismiss();
                                                            }
                                                        }).show();

                                            }else if(which == 1){
                                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                        .requestIdToken(getString(R.string.default_web_client_id))
                                                        .requestEmail()
                                                        .build();

                                                GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(Dashboard.this)
                                                        .enableAutoManage(Dashboard.this, Dashboard.this)
                                                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                                        .build();

                                                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                                                startActivityForResult(signInIntent, RC_SIGN_IN);



                                            }else if(which == 2){
                                                FacebookSdk.sdkInitialize(getApplicationContext());

                                                LoginManager.getInstance().logInWithReadPermissions(Dashboard.this, Arrays.asList("email","public_profile"));

                                                callbackManager = CallbackManager.Factory.create();

                                                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                                                    @Override
                                                    public void onSuccess(LoginResult loginResult) {
                                                        alert = new CustomAlertDialogs();
                                                        alert.initLoadingPage(Dashboard.this);
                                                        mAuth.getCurrentUser().linkWithCredential(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()))
                                                                .addOnCompleteListener(Dashboard.this, new OnCompleteListener<AuthResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                                        alert.hideLoadingPage();
                                                                        if (task.isSuccessful()) {
                                                                            alert.hideLoadingPage();
                                                                            alert.initCommonDialogPage(Dashboard.this,getString(R.string.dashboard_permentmem_sucess),false).show();
                                                                        saveData(Profile.getCurrentProfile().getFirstName(),Profile.getCurrentProfile().getLastName(),
                                                                                    Profile.getCurrentProfile().getProfilePictureUri(500,500).toString());
                                                                        } else {
                                                                            try {
                                                                                throw task.getException();
                                                                            }catch (FirebaseAuthUserCollisionException collide) {
                                                                                alert.initCommonDialogPage(Dashboard.this,getString(R.string.dashboard_accountcollide_text),true).show();
                                                                            } catch (Exception e) {
                                                                                alert.initCommonDialogPage(Dashboard.this,getString(R.string.common_error),true).show();
                                                                                Log.d("rror", ""+e.getMessage());
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void onCancel() {
                                                        Toast.makeText(getApplication(),R.string.signup_cancel_toast,Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onError(FacebookException error) {
                                                        alert = new CustomAlertDialogs();
                                                        alert.initCommonDialogPage(Dashboard.this,getString(R.string.network_error),true).show();                                                    }
                                                });


                                            }

                                        }
                                    });
                            builder.show();

                        }
                    });
            snackbar.show();
        }
        /*------------------------Account Availability checker--------------------------------------*/
        AutoTracking autoTrack=new AutoTracking();
        autoTrack.getTransactionDetail(this);
        if (ActionValidater.accountChecker()) {
            final android.support.v4.app.FragmentTransaction fragmentes = getSupportFragmentManager().beginTransaction();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Do you want to add new Wallet/Bank account ?");
            builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AddAccount addwallet = new AddAccount();
                    fragmentes.replace(R.id.fragmentContainer1, addwallet);
                    fragmentes.commit();

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        /*------------------------Account Availabilty checker--------------------------------------*/
    }



    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplication())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager!=null){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if(requestCode == RC_SIGN_IN){
            alert = new CustomAlertDialogs();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                alert.initLoadingPage(Dashboard.this);
                final GoogleSignInAccount acct = result.getSignInAccount();
                mAuth.getCurrentUser().linkWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                        .addOnCompleteListener(Dashboard.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                alert.hideLoadingPage();
                                if (task.isSuccessful()) {
                                    alert.initCommonDialogPage(Dashboard.this,getString(R.string.dashboard_permentmem_sucess),false).show();
                                    saveData(acct.getFamilyName(),acct.getDisplayName(),acct.getPhotoUrl().toString());
                                } else {
                                    try {
                                        throw task.getException();
                                    }catch (FirebaseAuthUserCollisionException collide) {
                                        alert.initCommonDialogPage(Dashboard.this,getString(R.string.dashboard_accountcollide_text),true).show();
                                    } catch (Exception e) {
                                        alert.initCommonDialogPage(Dashboard.this,getString(R.string.common_error),true).show();
                                        Log.d("rror", ""+e.getMessage());
                                    }
                                }

                            }
                        });
            }
            else {
                alert.initCommonDialogPage(Dashboard.this, getString(R.string.network_error), true).show();
            }
        }
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
        itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.id.badge_textView);
        IconButton iconButtonMessages = (IconButton) badgeLayout.findViewById(R.id.badge_icon_button);
        Log.d("badgeCount","onCreateOptionsMenu"+badgeCount);
        setBadgeCount(badgeCount,itemMessagesBadgeTextView);

        iconButtonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationCards noti = new NotificationCards();
                TransactionMain dashboard = new TransactionMain();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                if (getSupportFragmentManager().findFragmentById(R.id.fragmentContainer1) instanceof TransactionMain) {
                    toolbar.setTitle(R.string.dashboard_settitle_notifications);
                    fragmentTransaction.replace(R.id.fragmentContainer1, noti);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }else{
                    toolbar.setTitle(R.string.dashboard_settitle_overview);
                    fragmentTransaction.replace(R.id.fragmentContainer1,dashboard);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
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

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        if (id == R.id.transactionFrag) {
            toolbar.setTitle(R.string.dashboard_settitle_overview);
             TransactionMain dashboard = new TransactionMain();
             fragmentTransaction.replace(R.id.fragmentContainer1,dashboard);
             fragmentTransaction.commit();
        } else if (id == R.id.reportsFrag) {
            toolbar.setTitle(R.string.dashboard_settitle_summary);


            SummaryTab summary = new SummaryTab();
            fragmentTransaction.replace(R.id.fragmentContainer1,summary);
            fragmentTransaction.commit();
        }else if (id == R.id.recurringFrag) {
            toolbar.setTitle(R.string.dashboard_settitle_recurring);


            TransactionRecurring transRecur = new TransactionRecurring();
            fragmentTransaction.replace(R.id.fragmentContainer1,transRecur);
            fragmentTransaction.commit();
        }else if (id == R.id.categoryFrag) {
            toolbar.setTitle(R.string.dashboard_settitle_family);


            FamilyTransactions famTrans = new FamilyTransactions();
            fragmentTransaction.replace(R.id.fragmentContainer1,famTrans);
            fragmentTransaction.commit();
        }else if (id == R.id.budgetFrag) {
            toolbar.setTitle(R.string.dashboard_settitle_budget);


            BudgetList budget = new BudgetList();
            fragmentTransaction.replace(R.id.fragmentContainer1,budget);
            fragmentTransaction.commit();
        }else if (id == R.id.walletFrag) {
            toolbar.setTitle(R.string.dashboard_settitle_wallet);


            AddAccount addwallet = new AddAccount();
            fragmentTransaction.replace(R.id.fragmentContainer1,addwallet);
            fragmentTransaction.commit();
        }else if (id == R.id.settingFrag) {
            toolbar.setTitle(R.string.dashboard_settitle_setting);


            Settings setting = new Settings();
            fragmentTransaction.replace(R.id.fragmentContainer1,setting);
            fragmentTransaction.commit();
        }else if (id == R.id.backupFrag) {
            toolbar.setTitle(R.string.dashboard_settitle_backup);


            ExportData backup = new ExportData();
            fragmentTransaction.replace(R.id.fragmentContainer1,backup);
            fragmentTransaction.commit();
        }else if(id == R.id.helpFrag){
            TransactionMain dashboard = new TransactionMain();

            if(!dashboard.isVisible()){
                toolbar.setTitle(R.string.dashboard_settitle_overview);
                fragmentTransaction.replace(R.id.fragmentContainer1,dashboard);
                fragmentTransaction.commit();
            }
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
        if (showcaseView!=null) {
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
    }

    private void setFirst(boolean isFirst){
        editor = pref.edit();
        editor.putBoolean("isFirst",isFirst);
        editor.commit();
    }

    @Override
    public void onBackPressed() { /*back disabled*/}


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,R.string.connectionfailed,Toast.LENGTH_SHORT).show();
    }

    private void saveData(String fname, String lname, String proPic) {
        UserData userData = new UserData(fname,lname, mAuth.getCurrentUser().getUid(),proPic);
        databaseReference.setValue(userData);
    }

    private void groupStatus(final String uID, String fID){
        FirebaseDatabase.getInstance().getReference("Groups").child(fID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tdSnapshot:dataSnapshot.getChildren()) {
                    if (tdSnapshot.getKey().equals(uID)) {
                        SharedPreferences sharedPref = getSharedPreferences("fwPrefs", 0);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("InGroup", "true");
                        editor.commit();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}