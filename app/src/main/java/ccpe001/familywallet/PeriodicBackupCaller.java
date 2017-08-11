package ccpe001.familywallet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import ccpe001.familywallet.transaction.TransactionDetails;
import com.google.android.gms.gcm.*;
import com.google.firebase.database.*;
import com.opencsv.CSVWriter;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;


public class PeriodicBackupCaller extends GcmTaskService {

    private static GcmNetworkManager gcmNetworkManager;
    private SharedPreferences prefs;
    private DatabaseReference databaseReference;
    private TransactionDetails tdata;
    private CSVWriter writer;
    private WritableWorkbook workbook;
    private File file;
    private WritableSheet sheet;
    private String finalLoc;



    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d("I ran","sd");
        try {
            export();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void backupRunner(Context c,String runTime){
        if(timeConverter(runTime,c)==0){
            if(gcmNetworkManager!=null) {
                gcmNetworkManager.cancelTask("backupTask", PeriodicBackupCaller.class);
            }
            return;
        }else {
            Log.d("backuo runner","d");

            gcmNetworkManager = GcmNetworkManager.getInstance(c);
            PeriodicTask task = new PeriodicTask.Builder()
                    .setService(PeriodicBackupCaller.class)
                    .setPeriod(timeConverter(runTime,c))
                    .setFlex(30)
                    .setRequiresCharging(true)//save battery
                    .setTag("backupTask")
                    .build();
            gcmNetworkManager.schedule(task);
        }
    }

    private static long timeConverter(String time,Context c){
        switch (time){
            /*case c.getString(R.string.daily): return 86400;
            case c.getString(R.string.weekly): return 30;//604800;
            case c.getString(R.string.monthly): return 2628003;
            case c.getString(R.string.annualy): return 31536000;
            case c.getString(R.string.nobackup): return 0;*/
        }
        return 0;
    }


    private void export() throws IOException {
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Transactions");
        databaseReference.keepSynced(true);

        prefs = getSharedPreferences("App Settings", Context.MODE_PRIVATE);
        finalLoc = prefs.getString("appBackUpPath","/storage/emulated/0/");
        finalLoc += "Backup";

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int countRow = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        //for csv file
                        if(writer ==null) {
                            String[] nameArr = {getString(R.string.id), getString(R.string.type),
                                    getString(R.string.userid), getString(R.string.datetime), getString(R.string.category),
                                    getString(R.string.amount), getString(R.string.account), getString(R.string.currency),
                                    getString(R.string.location)};
                            try {
                                writer = new CSVWriter(new FileWriter(finalLoc + ".csv"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            writer.writeNext(nameArr);
                        }
                        tdata = ds.getValue(TransactionDetails.class);
                        String[] data = {ds.getKey(), tdata.getType(), tdata.getUserID(), tdata.getDate() + " " + tdata.getTime(),
                                tdata.getCategoryName(), tdata.getAmount(), tdata.getAccount(), tdata.getCurrency(), tdata.getLocation()};
                        writer.writeNext(data);

                        //for excel file
                        try {
                            if (file == null) {
                                file = new File(finalLoc + ".xls");
                                WorkbookSettings wbSettings = new WorkbookSettings();
                                wbSettings.setLocale(new Locale("en", "EN"));
                                workbook = jxl.Workbook.createWorkbook(file, wbSettings);
                                sheet = workbook.createSheet(getString(R.string.periodicbackupcaller_createsheet), 0);
                                sheet.addCell(new Label(0, 0, getString(R.string.id)));
                                sheet.addCell(new Label(1, 0, getString(R.string.type)));
                                sheet.addCell(new Label(2, 0, getString(R.string.userid)));
                                sheet.addCell(new Label(3, 0, getString(R.string.datetime)));
                                sheet.addCell(new Label(4, 0, getString(R.string.category)));
                                sheet.addCell(new Label(5, 0, getString(R.string.amount)));
                                sheet.addCell(new Label(6, 0, getString(R.string.account)));
                                sheet.addCell(new Label(7, 0, getString(R.string.currency)));
                                sheet.addCell(new Label(8, 0, getString(R.string.location)));
                            }

                            countRow++;
                            tdata = ds.getValue(TransactionDetails.class);
                            sheet.addCell(new Label(0, countRow, ds.getKey()));
                            sheet.addCell(new Label(1, countRow, tdata.getType()));
                            sheet.addCell(new Label(2, countRow, tdata.getUserID()));
                            sheet.addCell(new Label(3, countRow, tdata.getDate() + " " + tdata.getTime()));
                            sheet.addCell(new Label(4, countRow, tdata.getCategoryName()));
                            sheet.addCell(new Label(5, countRow, tdata.getAmount()));
                            sheet.addCell(new Label(6, countRow, tdata.getAccount()));
                            sheet.addCell(new Label(7, countRow, tdata.getCurrency()));
                            sheet.addCell(new Label(8, countRow, tdata.getLocation()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

                try {
                    //closing opened files
                        workbook.write();
                        workbook.close();
                        writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
