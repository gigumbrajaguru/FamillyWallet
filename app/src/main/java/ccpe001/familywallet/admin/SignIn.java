package ccpe001.familywallet.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ccpe001.familywallet.OCRReader;
import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import ccpe001.familywallet.R;
import ccpe001.familywallet.Validate;

/**
 * Created by harithaperera on 4/30/17.
 */
public class SignIn extends PinActivity implements View.OnClickListener{

    private Button signIn;
    private TextView toSignUp,forgotTxt;
    private EditText emailTxt,passTxt;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        init();
    }

    private void init() {
        setTitle(R.string.signin_title);
        signIn= (Button)findViewById(R.id.signInBtn);
        toSignUp = (TextView)findViewById(R.id.textView2);
        forgotTxt = (TextView)findViewById(R.id.textView);
        emailTxt = (EditText)findViewById(R.id.emailTxt);
        passTxt = (EditText)findViewById(R.id.passwordTxt);

        signIn.setOnClickListener(this);
        toSignUp.setOnClickListener(this);
        forgotTxt.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

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
                    progressDialog.setMessage(getString(R.string.signin_waitmsg));
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(emailTxt.getText().toString().trim(),
                            passTxt.getText().toString().trim())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        saveSession(emailTxt.getText().toString());
                                        finish();
                                        Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(SignIn.this,R.string.common_error,Toast.LENGTH_SHORT).show();
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
        }
    }

    private void saveSession(String email) {
        SharedPreferences prefs = getSharedPreferences("Session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userMail",email);
        editor.commit();
    }



    
}
