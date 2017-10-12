package ccpe001.familywallet.transaction;

import android.util.Log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import ccpe001.familywallet.Validate;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Knight on 10/2/2017.
 */

public class CurrencyConverter {


    static JSONObject jsnObj = null;
    private static String CURRENCY_API;
    private static Validate val = new Validate();

    /** Asynchronous call back when json file received */
    public interface OnJSONResponseCallback {
        public void onJSONResponse(boolean success, JSONObject response);
    }

    public static JSONObject getJSONObj(String date, final OnJSONResponseCallback callback) {
        String month = date.substring(4, 6); //getting month of the recurring transaction
        String day = date.substring(6, 8);   //getting day of the recurring transaction
        String year = date.substring(0, 4);  //getting year of the recurring transaction
        String exchangeDate = year + "-" + month + "-" + day;

        /** Getting the json file to the exchange date */
        /** when the transaction date is the todays date*/
        if (exchangeDate.equals(getCurrentDate())){
            CURRENCY_API = "https://openexchangerates.org/api/latest.json?app_id=0dee46c64b7f4d339415facf13e29242";
        }

        /** when the transaction date is a date in the future*/
        else if (val.validFutureDate(exchangeDate)){
            CURRENCY_API = "https://openexchangerates.org/api/latest.json?app_id=0dee46c64b7f4d339415facf13e29242";
        }

        /** when the transaction date is a date in the past*/
        else {
            CURRENCY_API = "https://openexchangerates.org/api/historical/" + exchangeDate
                    + ".json?app_id=0dee46c64b7f4d339415facf13e29242";
        }




            AsyncHttpClient client = new AsyncHttpClient();
            client.get(CURRENCY_API, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    try {
                        /** Getting the json file to a json object and making the callback with true status*/
                        jsnObj = new JSONObject(new String(bytes));
                        JSONObject ratesObj = jsnObj.getJSONObject("rates");
                        callback.onJSONResponse(true,ratesObj);
                    } catch (JSONException e) {
                        Log.e("Exception", "JSONException " + e.toString());
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    try {
                        /**making the callback with false status*/
                        jsnObj = new JSONObject(new String(bytes));
                        JSONObject ratesObj = jsnObj.getJSONObject("rates");
                        callback.onJSONResponse(false,ratesObj);
                    } catch (JSONException e) {
                        Log.e("Exception", "JSONException " + e.toString());
                    }
                }
        });
        return jsnObj;
    }

    private static String getCurrentDate(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}

