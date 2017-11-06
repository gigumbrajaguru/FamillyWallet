package ccpe001.familywallet;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;

import ccpe001.familywallet.admin.GetInfo;
import ccpe001.familywallet.admin.Notification;
import ccpe001.familywallet.admin.SignIn;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kobakei.ratethisapp.RateThisApp;
import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import static ccpe001.familywallet.transaction.TimeDialog.pad;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by harithaperera on 5/8/17.
 */
public class Settings extends Fragment implements View.OnClickListener,Switch.OnCheckedChangeListener,DirectoryChooserFragment.OnFragmentInteractionListener{

    private Switch localMode,statusIcon,appNotySwitch,enDisPinSwitch;
    private Button signOutBtn,setPinBtn;
    private TextView langText,currText,dateForText,dailyRemText,backupLocText,appPwText,backupRemText;
    private AlertDialog.Builder langBuilder,currBuilder,dateForBuilder,enterPinBuilder,langBuilderOpener;
    private TableRow langRow,currRow,dateForRow,dailyRemRow,backupLocRow,appPassRow,feedBackRow,rateRow,backupRemRow;
    private Calendar c;
    private DirectoryChooserFragment mDialog;
    private static final int SET_PIN = 0;
    private static final int ENABLE_PIN = 1;
    private static final int DIR_CHOOSER = 2;
    private final static int DAILY_REMINDER = 11;
    private final static int NOTI_PPROTOTYPE = 22;
    private static final int BACKUP_PERM = 3;
    private static final int BACKUP_PERM2 = 4;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private NotificationManager notificationManager;

    private String[] langArr,currArr,dateForArr;

    private boolean pinStatus,mode,appNoty,appIcon;
    private String pin,preferedLang,preferedDateFor,preferedCurr,remTime,appbackUpPath,appBackUp;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference db;
    private TextView itemMessagesBadgeTextView;
    private CustomAlertDialogs alert;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.setting, container, false);
        FacebookSdk.sdkInitialize(getActivity());
        prefs = getContext().getSharedPreferences("App Settings",Context.MODE_PRIVATE);
        init(view);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            getActivity().finish();
            Intent intent = new Intent(getActivity(),SignIn.class);
            startActivity(intent);
        }

        return view;
    }

    private void init(View v){
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.xmlmenu_settingFrag_title);

        c = Calendar.getInstance();
        langArr = getActivity().getResources().getStringArray(R.array.spinnerLanguage);
        currArr = getActivity().getResources().getStringArray(R.array.spinnerCurrency);
        dateForArr = getActivity().getResources().getStringArray(R.array.spinnerDateFor);

        appNotySwitch = (Switch) v.findViewById(R.id.appNotySwitch);
        appNotySwitch.setOnCheckedChangeListener(this);
        localMode = (Switch) v.findViewById(R.id.localModeSwitch);
        localMode.setOnCheckedChangeListener(this);
        langRow = (TableRow) v.findViewById(R.id.selectLangRow);
        langRow.setOnClickListener(this);
        backupRemText = (TextView) v.findViewById(R.id.backupRemText);
        langText = (TextView) v.findViewById(R.id.statusLang);
        dateForRow = (TableRow) v.findViewById(R.id.selectDateRow);
        dateForRow.setOnClickListener(this);
        dateForText = (TextView) v.findViewById(R.id.statusDateFor);
        currRow = (TableRow) v.findViewById(R.id.selectCurrRow);
        currRow.setOnClickListener(this);
        currText = (TextView) v.findViewById(R.id.statusCurr);
        statusIcon = (Switch) v.findViewById(R.id.statusIconSwitch);
        statusIcon.setOnCheckedChangeListener(this);
        dailyRemRow = (TableRow) v.findViewById(R.id.remTimeRow);
        dailyRemRow.setOnClickListener(this);
        backupRemRow = (TableRow) v.findViewById(R.id.backupRemRow);
        backupRemRow.setOnClickListener(this);
        dailyRemText = (TextView) v.findViewById(R.id.startRem);
        backupLocRow = (TableRow) v.findViewById(R.id.backupLocRow);
        backupLocRow.setOnClickListener(this);
        backupLocText = (TextView) v.findViewById(R.id.statusBackUpLoc);
        appPassRow = (TableRow) v.findViewById(R.id.appPasswordRow);
        appPassRow.setOnClickListener(this);
        appPwText = (TextView) v.findViewById(R.id.statusAppPw);
        signOutBtn = (Button) v.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(this);
        feedBackRow = (TableRow) v.findViewById(R.id.feedbackRow);
        feedBackRow.setOnClickListener(this);
        rateRow = (TableRow) v.findViewById(R.id.rateRow);
        rateRow.setOnClickListener(this);

        retrievePWSharedPref();
    }

    public void setLanguage(Locale locale) {
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }


    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == BACKUP_PERM){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED||grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                final String[] items = {getString(R.string.daily),getString(R.string.weekly),getString(R.string.monthly),getString(R.string.annualy),getString(R.string.nobackup)};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.setting_reminderbuilder_settitle);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        PeriodicBackupCaller.backupRunner(getActivity(),items[item]);
                        backupRemText.setText(items[item]);
                        appBackUp = items[item];
                        storePWSharedPref();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                alert = new CustomAlertDialogs();
                alert.initCommonDialogPage(getActivity(),getString(R.string.error_permitting),true).show();
            }
        }else if(requestCode == BACKUP_PERM2){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED||grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                        .allowNewDirectoryNameModification(true)
                        .newDirectoryName("FamilyWallet Backups")
                        .build();

                mDialog = DirectoryChooserFragment.newInstance(config);
                mDialog.show(getActivity().getFragmentManager(), null);
                mDialog.setDirectoryChooserListener(this);
            }else {
                alert = new CustomAlertDialogs();
                alert.initCommonDialogPage(getActivity(),getString(R.string.error_permitting),true).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.signOutBtn){
            CustomAlertDialogs alert = new CustomAlertDialogs();
            alert.initLoadingPage(getActivity());

            //sign out & del daily rem,auto backups,noti icon,clear session
            if(mAuth.getCurrentUser().getProviders().toString().equals("[facebook.com]")){
                LoginManager.getInstance().logOut();
            }else if(mAuth.getCurrentUser().getProviders().toString().equals("[google.com]")){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }
            mAuth.signOut();
            SharedPreferences sharedPref= getContext().getSharedPreferences("fwPrefs",0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();      //clear all data.
            editor.commit();  //commit change to SharedPreferences.
            NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context
                    .NOTIFICATION_SERVICE);
            mNotificationManager.cancel(PendingIntent.FLAG_UPDATE_CURRENT);
            PeriodicBackupCaller.backupRunner(getActivity(),getString(R.string.nobackup));

            //off noti here
            notificationManager = (NotificationManager)getActivity().getSystemService(Context
                    .NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            getActivity().finish();
            sessionClear(getActivity());
            startActivity(new Intent("ccpe001.familywallet.SIGNIN"));
        }else if(view.getId()==R.id.selectLangRow){
            new CustomAlertDialogs().initCommonDialogPage(getActivity(),getString(R.string.setting_langBuilderOpener_setmsg),true)
                    .setPositiveButton(R.string.setting_setlangBuilderOpener_positive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    showLangChanger();
                }
            }).setNegativeButton(R.string.setting_setNegativeButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }else if(view.getId()==R.id.selectCurrRow){
            currBuilder = new AlertDialog.Builder(getContext());
            currBuilder.setTitle(R.string.setting_currbuilder_settitle);
            int z
                    =  prefs.getString("appCurr",currArr[0]).equals("Rs.") ? 0
                    : prefs.getString("appCurr",currArr[0]).equals("US $") ? 1
                    : 2;
            currBuilder.setSingleChoiceItems(R.array.spinnerCurrency, z, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    preferedCurr = currArr[i];
                    storePWSharedPref();
                    currText.setText(currArr[i]);
                    dialogInterface.dismiss();
                }
            });
            currBuilder.show();
        }else if(view.getId()==R.id.selectDateRow){
            dateForBuilder = new AlertDialog.Builder(getContext());
            dateForBuilder.setTitle(R.string.setting_datebuilder_settitle);
            int z
                    =  prefs.getString("appDateFor",dateForArr[0]).equals("2001/03/14") ? 0
                    : prefs.getString("appDateFor",dateForArr[0]).equals("14/03/2001") ? 1
                    : 2;
            dateForBuilder.setSingleChoiceItems(R.array.spinnerDateFor, z, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    preferedDateFor = dateForArr[i];
                    storePWSharedPref();
                    dateForText.setText(dateForArr[i]);
                    dialogInterface.dismiss();
                }
            });
            dateForBuilder.show();
        }else if(view.getId()==R.id.remTimeRow){
            new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hr, int min) {
                    remTime = pad(hr) + ":" + pad(min);
                    dailyRemText.setText(remTime);
                    storePWSharedPref();
                    Notification noti = new Notification(itemMessagesBadgeTextView);
                    noti.dailyReminder(getActivity());
                }
            },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
        }else if(view.getId()==R.id.backupLocRow) {
            final String[] EXPORTDATAPERARR = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

            if (!CustomAlertDialogs.hasPermissions(getActivity(),EXPORTDATAPERARR)) {
                alert = new CustomAlertDialogs();
                alert.initPermissionPage(getActivity(),getString(R.string.permit_only_backup)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        requestPermissions(EXPORTDATAPERARR,BACKUP_PERM2);
                    }
                }).show();
            }else {
                final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                        .allowNewDirectoryNameModification(true)
                        .newDirectoryName("FamilyWallet Backups")
                        .build();

                mDialog = DirectoryChooserFragment.newInstance(config);
                mDialog.show(getActivity().getFragmentManager(), null);
                mDialog.setDirectoryChooserListener(this);
            }

        }else if(view.getId()==R.id.appPasswordRow){
            enterPinBuilder = new AlertDialog.Builder(getActivity());
            enterPinBuilder.setTitle(R.string.setting_pinbuilder_settitle);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View alertDiaView = inflater.inflate(R.layout.passwordsetter,null);
            enterPinBuilder.setView(alertDiaView);

            setPinBtn = (Button) alertDiaView.findViewById(R.id.setPinBtn);
            enDisPinSwitch = (Switch) alertDiaView.findViewById(R.id.enDisPinSwitch);
            setPinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),CustomPinActivity.class);
                    intent.putExtra(AppLock.EXTRA_TYPE,AppLock.ENABLE_PINLOCK);
                    getActivity().startActivityForResult(intent,SET_PIN);
                }
            });
            enDisPinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Intent intent = new Intent(getContext(),CustomPinActivity.class);
                    if(b){
                        //enable lock
                        Toast.makeText(getContext(),"FamilyWallet Backups",Toast.LENGTH_LONG).show();
                        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
                        lockManager.enableAppLock(getActivity(),CustomPinActivity.class);
                        lockManager.getAppLock().setTimeout(5000000);
                        startActivityForResult(intent, ENABLE_PIN);
                    }else{
                        //disanle lock
                        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
                        lockManager.disableAppLock();
                    }
                }
            });
            enterPinBuilder.setNegativeButton(R.string.setting_pinbuilder_negbtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            enterPinBuilder.show();

        }else if(view.getId()==R.id.feedbackRow){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"ccpe_001@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT,"Customer Feedback");
            Intent.createChooser(intent,"Send email");
            try{
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), R.string.settings_feedbackmail_elsetoast, Toast.LENGTH_SHORT).show();
            }
        }else if(view.getId()==R.id.rateRow){
            //explicitly show dialog
            RateThisApp.Config config = new RateThisApp.Config();
            config.setUrl("market://details?id=" + getActivity().getPackageName());
            RateThisApp.init(config);
            RateThisApp.showRateDialog(getActivity());
        }else if(view.getId()==R.id.backupRemRow) {
            final String[] EXPORTDATAPERARR = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if(!CustomAlertDialogs.hasPermissions(getActivity(),EXPORTDATAPERARR)){
                alert = new CustomAlertDialogs();
                alert.initPermissionPage(getActivity(),getString(R.string.permit_only_backup)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        requestPermissions(EXPORTDATAPERARR,BACKUP_PERM);
                    }
                }).show();
            }else{
                final String[] items = {getString(R.string.daily),getString(R.string.weekly),getString(R.string.monthly),getString(R.string.annualy),getString(R.string.nobackup)};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.setting_reminderbuilder_settitle);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        PeriodicBackupCaller.backupRunner(getActivity(),items[item]);
                        backupRemText.setText(items[item]);
                        appBackUp = items[item];
                        storePWSharedPref();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem itemMessages = menu.findItem(R.id.action_notification);
        RelativeLayout badgeLayout = (RelativeLayout) itemMessages.getActionView();
        itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.id.badge_textView);
        if(itemMessagesBadgeTextView != null){
            Log.d("nullbro2","dfdfdf");
        }
    }



    private void showLangChanger(){
        langBuilder = new AlertDialog.Builder(getContext());
        langBuilder.setTitle(R.string.setting_langbuilder_settitle);
        int i = prefs.getString("appLang",langArr[1]).equals("Sinhala") ? 0 : 1;
        langBuilder.setSingleChoiceItems(R.array.spinnerLanguage, i, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preferedLang = langArr[i];
                storePWSharedPref();
                langText.setText(langArr[i]);
                dialogInterface.dismiss();

                Locale locale = null;

                switch (i){
                    case 0:
                        locale = new Locale("sin");
                        break;
                    case 1:
                        locale = new Locale("en");
                        break;
                }

                setLanguage(locale);
                getActivity().finish();
                startActivity(new Intent(getActivity(),Splash.class));
            }
        });
        langBuilder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIR_CHOOSER){
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED){
                data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
            }
        }else if(requestCode == ENABLE_PIN){
            Toast.makeText(getActivity(),"enabled pin",Toast.LENGTH_LONG).show();
        }
        else if(requestCode == SET_PIN){
            Toast.makeText(getActivity(),"set pin",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton switchs, boolean b) {
        if(switchs.getId()==R.id.localModeSwitch){

            if(b) {
                new CustomAlertDialogs().initCommonDialogPage(getActivity(),getString(R.string.setting_onCheckedChanged_langBuilderOpener_setmsg),true)
                .setPositiveButton(R.string.setting_setlangBuilderOpener_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mode = true;
                        localMode();
                        storePWSharedPref();

                        Locale locale = new Locale("sin");
                        setLanguage(locale);
                        getActivity().finish();
                        startActivity(new Intent(getActivity(),Splash.class));
                    }
                }).setNegativeButton(R.string.setting_setNegativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mode = false;
                        setDefaults();
                        storePWSharedPref();
                    }
                }).show();
            }else{
                mode = false;
                setDefaults();
                storePWSharedPref();
            }
        }else if(switchs.getId()==R.id.appNotySwitch){
            if(b) {
                dailyRemRow.setVisibility(View.VISIBLE);
                appNoty = true;
                storePWSharedPref();

                Notification noti = new Notification(itemMessagesBadgeTextView);
                noti.dailyReminder(getActivity());
                new Notification.UpdateNotification(itemMessagesBadgeTextView);

            }else{
                dailyRemRow.setVisibility(View.GONE);
                stopNotifications();
                notificationManager = (NotificationManager)getActivity().getSystemService(Context
                        .NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

                appNoty = false;
                storePWSharedPref();
            }
        }else if(switchs.getId()==R.id.statusIconSwitch){
            if(b) {
                appIcon = true;
                storePWSharedPref();
                new ccpe001.familywallet.admin.Notification(itemMessagesBadgeTextView).statusIcon(getActivity());
            }else{
                appIcon = false;
                storePWSharedPref();
                notificationManager = (NotificationManager)getActivity().getSystemService(Context
                        .NOTIFICATION_SERVICE);
                notificationManager.cancel(PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }
    }

    private void stopNotifications(){
        Intent intent = new Intent(getActivity(), Notification.Notification_Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),DAILY_REMINDER,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        if(alarmManager!=null){
            alarmManager.cancel(pendingIntent);
        }
    }

    private void retrievePWSharedPref(){
        pin = prefs.getString("appPass","123");
        pinStatus = prefs.getBoolean("appPinStatus",false);

        mode = prefs.getBoolean("appMode",false);
        preferedLang = prefs.getString("appLang",langArr[1]);
        preferedDateFor = prefs.getString("appDateFor",dateForArr[0]);
        preferedCurr = prefs.getString("appCurr",currArr[0]);
        appNoty = prefs.getBoolean("appNoty",true);
        appIcon = prefs.getBoolean("appIcon",true);
        remTime = prefs.getString("appDailyRem","09:00");
        appBackUp = prefs.getString("appBackUp",getString(R.string.weekly));
        appbackUpPath = prefs.getString("appBackUpPath","/storage/emulated/0/");


        localMode.setChecked(mode);
        langText.setText(preferedLang);
        dateForText.setText(preferedDateFor);
        currText.setText(preferedCurr);
        appNotySwitch.setChecked(appNoty);
        statusIcon.setChecked(appIcon);
        dailyRemText.setText(remTime);
        backupRemText.setText(appBackUp);
        backupLocText.setText(appbackUpPath);
        appPwText.setText(String.valueOf(pinStatus));
    }

    private void storePWSharedPref(){
        editor = prefs.edit();
        editor.putString("appPass",pin);
        editor.putBoolean("appPinStatus",pinStatus);

        editor.putBoolean("appMode",mode);
        editor.putString("appLang",preferedLang);
        editor.putString("appDateFor",preferedDateFor);
        editor.putString("appCurr",preferedCurr);
        editor.putBoolean("appNoty",appNoty);
        editor.putBoolean("appIcon",appIcon);
        editor.putString("appDailyRem",remTime);
        editor.putString("appBackUp",appBackUp);
        editor.putString("appBackUpPath", appbackUpPath);

        editor.commit();
    }

    private void setDefaults(){
        mode = false;
        appIcon = true;
        appNoty = true;
        preferedLang = langArr[1];
        preferedDateFor = dateForArr[0];
        preferedCurr = currArr[0];
        remTime = "09:00";
        appBackUp = getString(R.string.nobackup);
        appbackUpPath = "/storage/emulated/0/";
        pinStatus = false;

        localMode.setChecked(mode);
        langText.setText(preferedLang);
        dateForText.setText(preferedDateFor);
        currText.setText(preferedCurr);
        appNotySwitch.setChecked(appIcon);
        statusIcon.setChecked(appNoty);
        dailyRemText.setText(remTime);
        backupRemText.setText(appBackUp);
        backupLocText.setText(appbackUpPath);
        appPwText.setText(String.valueOf(pinStatus));
    }

    private void localMode(){
        preferedLang = langArr[0];
        preferedDateFor = dateForArr[1];
        preferedCurr = currArr[2];

        langText.setText(preferedLang);
        dateForText.setText(preferedDateFor);
        currText.setText(preferedCurr);
    }




    @Override
    public void onSelectDirectory(@NonNull String path) {
        mDialog.setTargetFragment(getActivity().getFragmentManager().findFragmentById(R.id.settingFrag),DIR_CHOOSER);
        //mDialog.show(getActivity().getFragmentManager(),null);
        mDialog.dismiss();
        appbackUpPath = path.concat("/");//fixed minor issue
        storePWSharedPref();
        backupLocText.setText(appbackUpPath);
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }

    public static void sessionClear(Context c){
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = c.getSharedPreferences("Session",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.clear();
        editor.commit();
    }


}

