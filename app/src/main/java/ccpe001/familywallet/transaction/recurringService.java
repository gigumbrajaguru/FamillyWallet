package ccpe001.familywallet.transaction;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by Knight on 9/12/2017.
 */

public class recurringService extends GcmTaskService {
    public recurringService(){

    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        /* getting user id and family id from shared preferences*/
        SharedPreferences sharedPref = getSharedPreferences("fwPrefs",0);
        String uid = sharedPref.getString("uniUserID", "");
        String fid = sharedPref.getString("uniFamilyID", "");
        String InGroup = sharedPref.getString("InGroup", "");
        /* calling recurring transaction method*/
        AutoRecurringTransactions recurTrans = new AutoRecurringTransactions(uid, fid, InGroup);
        Log.i("echo","background service working");
        return 0;
    }
}


