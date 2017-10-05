package ccpe001.familywallet.budget;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by Gigum on 2017-08-13.
 */

public class AlertBox {


    public static void alertBoxOut(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        // Show Alert Message
        alertDialog.show();
    }

}
