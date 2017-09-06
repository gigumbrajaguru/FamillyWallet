package ccpe001.familywallet.budget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ccpe001.familywallet.R;

public class budgetTrack extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_track);
        Intent myIntent = getIntent();
        String budgetI = myIntent.getStringExtra("budgetID");






    }



}
