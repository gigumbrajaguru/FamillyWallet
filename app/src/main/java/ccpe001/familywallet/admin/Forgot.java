package ccpe001.familywallet.admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ccpe001.familywallet.R;
import ccpe001.familywallet.Validate;
import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by harithaperera on 5/28/17.
 */
public class Forgot extends PinActivity implements View.OnClickListener {

    private Button sendBtn;
    private TextView textForTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot);
        init();
    }

    private void init() {
        sendBtn = (Button) findViewById(R.id.sendMail);
        textForTxt = (TextView) findViewById(R.id.emailTxtFor);
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.sendMail){

            if(Validate.anyValidMail(textForTxt.getText().toString().trim())){
                FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(textForTxt.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),textForTxt.getText().toString().trim()
                                            +R.string.forgot_oncomplete_toast,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),R.string.common_error,Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                textForTxt.setError(getString(R.string.forgot_emailerr));
            }

        }
    }
}
