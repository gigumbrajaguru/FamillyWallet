package ccpe001.familywallet.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ccpe001.familywallet.CustomAlertDialogs;
import ccpe001.familywallet.Dashboard;
import ccpe001.familywallet.Validate;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ccpe001.familywallet.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Arrays;

/**
 * Created by harithaperera on 4/30/17.
 */
public class SignIn extends PinActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private Button signIn;
    private TextView toSignUp,forgotTxt;
    private EditText emailTxt,passTxt;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private SignInButton googleBtn;
    private LoginButton fbBtn;
    private Button noSignInBtn;

    private final static int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private DatabaseReference databaseReference;
    private CustomAlertDialogs alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.signin);
        init();
    }

    private void init() {
        setTitle(R.string.signin_title);
        signIn= (Button)findViewById(R.id.signInBtn);
        noSignInBtn= (Button)findViewById(R.id.noSignInBtn);
        noSignInBtn.setOnClickListener(this);
        toSignUp = (TextView)findViewById(R.id.textView2);
        forgotTxt = (TextView)findViewById(R.id.textView);
        emailTxt = (EditText)findViewById(R.id.emailTxt);
        passTxt = (EditText)findViewById(R.id.passwordTxt);

        signIn.setOnClickListener(this);
        toSignUp.setOnClickListener(this);
        forgotTxt.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        fbBtn = (LoginButton) findViewById(R.id.fbOptBtn);
        googleBtn= (SignInButton)findViewById(R.id.googleOptBtn);
        googleBtn.setOnClickListener(this);
        fbBtn.setOnClickListener(this);
        TextView textView = (TextView) googleBtn.getChildAt(0);
        textView.setText(R.string.xmlsignin_googleOptBtn_text);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        callbackManager = CallbackManager.Factory.create();
        fbBtn.setReadPermissions(Arrays.asList("email","public_profile"));


        databaseReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){//if user already logged in
            finish();
            Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
            startActivity(intent);
        }
    }


    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.signInBtn){
            if(Validate.anyValidMail(emailTxt.getText().toString().trim())) {
                if(Validate.anyValidPass(passTxt.getText().toString().trim())){
                    alert = new CustomAlertDialogs();
                    alert.initLoadingPage(this);
                    mAuth.signInWithEmailAndPassword(emailTxt.getText().toString().trim(),
                            passTxt.getText().toString().trim())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        saveSession(emailTxt.getText().toString());
                                        finish();
                                        Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
                                        startActivity(intent);
                                    }else{
                                        alert.hideLoadingPage();
                                        try {
                                            throw task.getException();
                                        }catch (FirebaseAuthInvalidUserException invalidEmail)
                                        {
                                            emailTxt.setError(getString(R.string.signup_incorrect_email_text));
                                        }catch(FirebaseAuthInvalidCredentialsException wrongPassword) {
                                            passTxt.setError(getString(R.string.signup_incorrect_pw_text));
                                        }
                                        catch (FirebaseNetworkException e)
                                        {
                                            alert.hideLoadingPage();
                                            alert.initCommonDialogPage(SignIn.this,getString(R.string.network_error),true);
                                        } catch (Exception e) {
                                            alert.initCommonDialogPage(SignIn.this,getString(R.string.common_error),true);
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                }else{
                    passTxt.setError(getString(R.string.signup_onclick_passerr));
                }
            }else {
                emailTxt.setError(getString(R.string.signup_onclick_emailerr));
            }


        }else if(view.getId()== R.id.textView2){
            startActivity(new Intent(this,SignUp.class));
        }else if(view.getId()== R.id.textView){
            Intent intent = new Intent(this,Forgot.class);
            startActivity(intent);
        }else if(view.getId()== R.id.googleOptBtn){
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else if(view.getId()== R.id.fbOptBtn){
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplication(),R.string.signup_cancel_toast,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    alert = new CustomAlertDialogs();
                    alert.initCommonDialogPage(SignIn.this,getString(R.string.network_error),true);
                }
            });
        }else if(view.getId()== R.id.noSignInBtn){
            alert = new CustomAlertDialogs();
            alert.initLoadingPage(this);
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                saveData("Demo","User",null);
                                finish();
                                Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
                                startActivity(intent);
                            } else {
                                alert.hideLoadingPage();
                                try {
                                    throw task.getException();
                                }
                                catch (FirebaseNetworkException e)
                                {
                                    alert.initCommonDialogPage(SignIn.this,getString(R.string.network_error),true);
                                } catch (Exception e) {
                                    alert.initCommonDialogPage(getApplication(),"signInAnonymously:failure "+task.getException(),true);
                                }
                            }
                        }
                    });
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(callbackManager!=null){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }


        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);//once it auth with google it does others
            }
            else {
                alert = new CustomAlertDialogs();
                alert.initCommonDialogPage(SignIn.this, getString(R.string.network_error), true);
            }

        }
    }

    private void saveSession(String email) {
        SharedPreferences prefs = getSharedPreferences("Session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userMail",email);
        editor.commit();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,R.string.connectionfailed,Toast.LENGTH_SHORT).show();
    }

    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        alert = new CustomAlertDialogs();
        alert.initLoadingPage(this);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("Google", "signInWithCredential:oncomplete: " + task.isSuccessful());
                            Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
                            intent.putExtra("firstname",acct.getFamilyName());
                            intent.putExtra("lastname",acct.getDisplayName());
                            saveData(acct.getFamilyName(),acct.getDisplayName(),acct.getPhotoUrl().toString());
                            try {
                                intent.putExtra("profilepic", acct.getPhotoUrl().toString());
                            }catch (Exception e){

                            }
                            startActivity(intent);
                        }else {
                            alert.hideLoadingPage();
                            alert.initCommonDialogPage(SignIn.this,getString(R.string.common_error),true);
                        }
                    }
                });


    }

    private void saveData(String fname, String lname, String proPic) {
        UserData userData = new UserData(fname,lname, mAuth.getCurrentUser().getUid(),proPic);
        databaseReference.child("UserInfo").child(mAuth.getCurrentUser().getUid()).setValue(userData);
    }

    public void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        alert = new CustomAlertDialogs();
        alert.initLoadingPage(this);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
                intent.putExtra("firstname",Profile.getCurrentProfile().getFirstName());
                intent.putExtra("lastname",Profile.getCurrentProfile().getLastName());
                saveData(Profile.getCurrentProfile().getFirstName(),Profile.getCurrentProfile().getLastName(),
                        Profile.getCurrentProfile().getProfilePictureUri(500,500).toString());
                try {
                    intent.putExtra("profilepic", Profile.getCurrentProfile().getProfilePictureUri(500,500).toString());
                }catch (Exception e){

                }
                startActivity(intent);
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alert.hideLoadingPage();
                alert.initCommonDialogPage(SignIn.this,getString(R.string.common_error),true);
            }
        });
    }

    @Override
    public void onBackPressed() { /*back disabled*/}
}
