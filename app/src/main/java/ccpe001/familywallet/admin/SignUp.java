package ccpe001.familywallet.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import ccpe001.familywallet.R;
import ccpe001.familywallet.Validate;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Arrays;


public class SignUp extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    private Button signUpBtn;
    private EditText emailTxt,passTxt;
    private SignInButton googleBtn;
    private LoginButton fbBtn;
    private ProgressDialog progressBar;

    private final static int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.signup);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        if(mAuth.getCurrentUser() != null){
            finish();
            Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
            startActivity(intent);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        getSupportActionBar().setTitle(R.string.signup_title);
        signUpBtn= (Button)findViewById(R.id.signupBtn);
        emailTxt= (EditText)findViewById(R.id.emailTxt);
        progressBar = new ProgressDialog(this);
        fbBtn = (LoginButton) findViewById(R.id.fbOptBtn);
        passTxt= (EditText)findViewById(R.id.passwordTxt);
        googleBtn= (SignInButton)findViewById(R.id.googleOptBtn);
        signUpBtn.setOnClickListener(this);
        googleBtn.setOnClickListener(this);
        fbBtn.setOnClickListener(this);


        callbackManager = CallbackManager.Factory.create();
        fbBtn.setReadPermissions(Arrays.asList("email","public_profile"));


    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Facebook", "signInWithCredential:oncomplete: " + task.isSuccessful());
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
                Toast.makeText(SignUp.this,getString(R.string.common_error),Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.signupBtn){

            if(Validate.anyValidMail(emailTxt.getText().toString().trim())) {
                if(Validate.anyValidPass(passTxt.getText().toString().trim())){
                    progressBar.setMessage(getString(R.string.signup_onclickprogressbar_msg));
                    progressBar.show();
                    mAuth.createUserWithEmailAndPassword(emailTxt.getText().toString().trim(),
                                                        passTxt.getText().toString().trim())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.dismiss();
                                    if(task.isSuccessful()){
                                        saveSession(mAuth.getCurrentUser().getEmail());
                                        Toast.makeText(SignUp.this,R.string.signup_oncomplete_sucesstoast,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent("ccpe001.familywallet.GETINFO");
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(SignUp.this,getString(R.string.common_error),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    passTxt.setError(getString(R.string.signup_onclick_passerr));
                }
            }else {
                emailTxt.setError(getString(R.string.signup_onclick_emailerr));
            }

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
                    Toast.makeText(getApplicationContext(), R.string.common_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void saveSession(String email) {
        SharedPreferences prefs = getSharedPreferences("Session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userMail",email);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,R.string.connectionfailed,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);//once it auth with google it does others
            }
            else
                Toast.makeText(this, R.string.common_error,Toast.LENGTH_SHORT).show();

        }
    }


    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                    }
                });

    }

    private void saveData(String fname, String lname, String proPic) {
        UserData userData = new UserData(fname,lname, mAuth.getCurrentUser().getUid(),proPic);
        databaseReference.child("UserInfo").child(mAuth.getCurrentUser().getUid()).setValue(userData);
    }



}
