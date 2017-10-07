package ccpe001.familywallet.transaction;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

/**
 * Created by Knight on 10/2/2017.
 */

public class CurrencyResponse {

    Double convertedCurrency;
    private DatabaseReference mDatabase;

    public void curr(final TransactionDetails td, final String InGroup){

        final Double amount = Double.parseDouble(td.getAmount());

        CurrencyConverter.getJSONObj(td.getDate(), new CurrencyConverter.OnJSONResponseCallback(){
            @Override
            public void onJSONResponse(boolean success, JSONObject ratesObj){
                try {
                    final Double lkr = ratesObj.getDouble("LKR");
                    final Double eur = ratesObj.getDouble("EUR");
                    final Double gbp = ratesObj.getDouble("GBP");
                    final Double inr = ratesObj.getDouble("INR");

                    if (td.getCurrency().equals("USD.")) {
                        convertedCurrency = amount * lkr;
                    } else if (td.getCurrency().equals("EUR.")) {
                        convertedCurrency = amount * (lkr / eur);

                    } else if (td.getCurrency().equals("GBP.")) {
                        convertedCurrency = amount * (lkr / gbp);

                    } else if (td.getCurrency().equals("INR.")) {
                        convertedCurrency = amount * (lkr / inr);
                    }

                    convertedCurrency=Math.round(convertedCurrency * 100.0) / 100.0;

                    td.setAmount(String.valueOf(convertedCurrency));
                    td.setCurrency("LKR.");
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    if (td.getFamilyID().equals(td.getUserID()) && !InGroup.equals("true")){
                        mDatabase.child("Transactions").child(td.getUserID()).push().setValue(td);
                    }
                    else{
                        mDatabase.child("Transactions").child("Groups").child(td.getFamilyID()).push().setValue(td);
                    }


                    Log.i("echoCur",""+convertedCurrency);
                }catch (Exception e){

                }
            }
        });
    }
}
