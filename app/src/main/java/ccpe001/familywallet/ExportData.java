package ccpe001.familywallet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ccpe001.familywallet.transaction.TransactionDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.opencsv.CSVWriter;
import jxl.*;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by harithaperera on 5/28/17.
 */
public class ExportData extends Fragment implements View.OnClickListener,CheckBox.OnCheckedChangeListener{


    private CheckBox checkCSV,checkEXEL;
    private Button createBtn,createBtn2;
    private EditText fileName;
    private Switch isMail;
    private String filename,finalLoc;
    private boolean exelChecked;
    private boolean csvChecked;
    private boolean mailChecked;
    private static final int BACKUP_PERM = 3;
    private DatabaseReference databaseReference,recurrDatabaseReference;
    private TransactionDetails tdata;
    private CSVWriter writer;
    private WritableWorkbook workbook;
    private File file;
    private WritableSheet sheet;
    private SharedPreferences prefs,prefs2;
    private CustomAlertDialogs alert;
    private String InGroup,familyID,uid;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.exportdata, container, false);

        prefs2 = getContext().getSharedPreferences("fwPrefs",0);
        InGroup = prefs2.getString("InGroup", "");
        familyID = prefs2.getString("uniFamilyID", "");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Transactions").child(uid);
        if (familyID.equals(uid) && !InGroup.equals("true")){
            databaseReference = databaseReference.child("Transactions").child(uid);
        }else {
            databaseReference =  databaseReference.child("Transactions").child("Groups").child(familyID);
        }
        recurrDatabaseReference = FirebaseDatabase.getInstance().getReference().child("RecurringTransactions").child(uid);
        recurrDatabaseReference.keepSynced(true);
        databaseReference.keepSynced(true);
        init(view);
        return view;
    }

    /**
     * This method is used to initialize elements in the xml file
     * @param view
     */
    private void init(View view) {
        checkCSV = (CheckBox)view.findViewById(R.id.csvCheck);
        checkEXEL = (CheckBox)view.findViewById(R.id.exelCheck);
        createBtn = (Button)view.findViewById(R.id.createBtn);
        createBtn2 = (Button)view.findViewById(R.id.createBtn2);
        fileName = (EditText) view.findViewById(R.id.filename);
        isMail = (Switch)view.findViewById(R.id.sendBackMail);
        createBtn.setOnClickListener(this);
        createBtn2.setOnClickListener(this);
        checkCSV.setOnCheckedChangeListener(this);
        checkEXEL.setOnCheckedChangeListener(this);
        isMail.setOnCheckedChangeListener(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == BACKUP_PERM){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED||grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Log.d("dff","sd");
                try {
                    alert = new CustomAlertDialogs();
                    if ((!csvChecked) && (!exelChecked)) {
                        alert.initCommonDialogPage(getActivity(),getString(R.string.exportdata_onclick_checkticks),true).show();
                    } else {
                        exportGenaralTrans();
                        alert.initCommonDialogPage(getActivity(),getString(R.string.exportdata_onclick_backupdone),false).show();
                        if (mailChecked) {
                            isMailCreator();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                alert = new CustomAlertDialogs();
                alert.initCommonDialogPage(getActivity(),getString(R.string.error_permitting),true).show();
            }
        }
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.createBtn2){
            if (Validate.fileValidate(fileName.getText())) {
                filename = fileName.getText().toString();
                final String[] EXPORTDATAPERARR = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!CustomAlertDialogs.hasPermissions(getActivity(),EXPORTDATAPERARR)) {

                    alert = new CustomAlertDialogs();
                    alert.initPermissionPage(getActivity(),getString(R.string.permit_only_backup)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            requestPermissions(EXPORTDATAPERARR,BACKUP_PERM);
                        }
                    }).show();


                }else {

                    try {
                        alert = new CustomAlertDialogs();
                        if ((!csvChecked) && (!exelChecked)) {
                            alert.initCommonDialogPage(getActivity(),getString(R.string.exportdata_onclick_checkticks),true).show();
                        } else {
                            exportGenaralTrans();
                            alert.initCommonDialogPage(getActivity(),getString(R.string.exportdata_onclick_backupdone),false).show();
                            if (mailChecked) {
                                isMailCreator();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                fileName.setError(getString(R.string.exportdata_onclick_invalidfilename));
            }
        }else if(view.getId()==R.id.createBtn){
            if (Validate.fileValidate(fileName.getText())) {
                filename = fileName.getText().toString();
                final String[] EXPORTDATAPERARR = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!CustomAlertDialogs.hasPermissions(getActivity(),EXPORTDATAPERARR)) {

                    alert = new CustomAlertDialogs();
                    alert.initPermissionPage(getActivity(),getString(R.string.permit_only_backup)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            requestPermissions(EXPORTDATAPERARR,BACKUP_PERM);
                        }
                    }).show();


                }else {

                    try {
                        alert = new CustomAlertDialogs();
                        if ((!csvChecked) && (!exelChecked)) {
                            alert.initCommonDialogPage(getActivity(),getString(R.string.exportdata_onclick_checkticks),true).show();
                        } else {
                            exportRecurringTrans();
                            alert.initCommonDialogPage(getActivity(),getString(R.string.exportdata_onclick_backupdone),false).show();
                            if (mailChecked) {
                                isMailCreator();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                fileName.setError(getString(R.string.exportdata_onclick_invalidfilename));
            }
        }
    }


    /**
     * This method is used when exporting general transactions
     * @throws IOException
     */
    public void exportGenaralTrans() throws IOException {
        prefs = getContext().getSharedPreferences("App Settings", Context.MODE_PRIVATE);
        finalLoc = prefs.getString("appBackUpPath","/storage/emulated/0/");
        finalLoc += filename;

         databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int countRow = 0;
                    finalLoc = finalLoc.concat(" General");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //execute if csv export is checked
                        if (csvChecked) {

                            if(writer ==null) {
                                //used to create titles
                                String[] nameArr = {getString(R.string.id),getString(R.string.title), getString(R.string.type), getString(R.string.datetime), getString(R.string.category),
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
                            String[] data = {ds.getKey(), tdata.getTitle(),tdata.getType(), Translate.dateView(tdata.getDate(),getActivity()) + " " + tdata.getTime(),
                                    tdata.getCategoryName(), tdata.getAmount(), tdata.getAccount(), tdata.getCurrency(), tdata.getLocation()};
                            writer.writeNext(data);

                        }
                        //execute if excel export is checked
                        if(exelChecked){
                            try {
                                //used to create titles
                                if (file == null) {
                                    file = new File(finalLoc + ".xls");
                                    WorkbookSettings wbSettings = new WorkbookSettings();
                                    wbSettings.setLocale(new Locale("en", "EN"));
                                    workbook = jxl.Workbook.createWorkbook(file, wbSettings);
                                    sheet = workbook.createSheet(getString(R.string.periodicbackupcaller_createsheet), 0);
                                    sheet.addCell(new Label(0, 0, getString(R.string.id)));
                                    sheet.addCell(new Label(1, 0, getString(R.string.title)));
                                    sheet.addCell(new Label(2, 0, getString(R.string.type)));
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
                                sheet.addCell(new Label(1, countRow, tdata.getTitle()));
                                sheet.addCell(new Label(2, countRow, tdata.getType()));
                                sheet.addCell(new Label(3, countRow, Translate.dateView(tdata.getDate(),getActivity()) + " " + tdata.getTime()));
                                sheet.addCell(new Label(4, countRow, tdata.getCategoryName()));
                                sheet.addCell(new Label(5, countRow, tdata.getAmount()));
                                sheet.addCell(new Label(6, countRow, tdata.getAccount()));
                                sheet.addCell(new Label(7, countRow, tdata.getCurrency()));
                                sheet.addCell(new Label(8, countRow, tdata.getLocation()));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    try {
                        if (exelChecked){
                            workbook.write();
                            workbook.close();
                        }
                        if (csvChecked)
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

    /**
     * This method is used when exporting recurring transactions
     * @throws IOException
     */
    public void exportRecurringTrans() throws IOException {
        prefs = getContext().getSharedPreferences("App Settings", Context.MODE_PRIVATE);
        finalLoc = prefs.getString("appBackUpPath","/storage/emulated/0/");
        finalLoc += filename;


        recurrDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int countRow = 0;
                finalLoc = finalLoc.concat(" Recurring");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (csvChecked) {

                        if(writer ==null) {
                            String[] nameArr = {getString(R.string.id),getString(R.string.title),getString(R.string.recurringPeriod), getString(R.string.type), getString(R.string.datetime), getString(R.string.category),
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
                        String[] data = {ds.getKey(), tdata.getTitle(),tdata.getRecurringPeriod(),tdata.getType(), Translate.dateView(tdata.getDate(),getActivity()) + " " + tdata.getTime(),
                                tdata.getCategoryName(), tdata.getAmount(), tdata.getAccount(), tdata.getCurrency(), tdata.getLocation()};
                        writer.writeNext(data);

                    }
                    if(exelChecked){
                        try {
                            if (file == null) {
                                file = new File(finalLoc + ".xls");
                                WorkbookSettings wbSettings = new WorkbookSettings();
                                wbSettings.setLocale(new Locale("en", "EN"));
                                workbook = jxl.Workbook.createWorkbook(file, wbSettings);
                                sheet = workbook.createSheet(getString(R.string.periodicbackupcaller_createsheet), 0);
                                sheet.addCell(new Label(0, 0, getString(R.string.id)));
                                sheet.addCell(new Label(1, 0, getString(R.string.title)));
                                sheet.addCell(new Label(2, 0, getString(R.string.recurringPeriod)));
                                sheet.addCell(new Label(3, 0, getString(R.string.type)));
                                sheet.addCell(new Label(4, 0, getString(R.string.datetime)));
                                sheet.addCell(new Label(5, 0, getString(R.string.category)));
                                sheet.addCell(new Label(6, 0, getString(R.string.amount)));
                                sheet.addCell(new Label(7, 0, getString(R.string.account)));
                                sheet.addCell(new Label(8, 0, getString(R.string.currency)));
                                sheet.addCell(new Label(9, 0, getString(R.string.location)));
                            }

                            countRow++;
                            tdata = ds.getValue(TransactionDetails.class);
                            sheet.addCell(new Label(0, countRow, ds.getKey()));
                            sheet.addCell(new Label(1, countRow, tdata.getTitle()));
                            sheet.addCell(new Label(2, countRow, tdata.getRecurringPeriod()));
                            sheet.addCell(new Label(3, countRow, tdata.getType()));
                            sheet.addCell(new Label(4, countRow, Translate.dateView(tdata.getDate(),getActivity()) + " " + tdata.getTime()));
                            sheet.addCell(new Label(5, countRow, tdata.getCategoryName()));
                            sheet.addCell(new Label(6, countRow, tdata.getAmount()));
                            sheet.addCell(new Label(7, countRow, tdata.getAccount()));
                            sheet.addCell(new Label(8, countRow, tdata.getCurrency()));
                            sheet.addCell(new Label(9, countRow, tdata.getLocation()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    if (exelChecked){
                        workbook.write();
                        workbook.close();
                    }
                    if (csvChecked)
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


    /**
     * Used to send generated files via email
     */
    private void isMailCreator(){
        String attchArr[] = {(finalLoc+".csv"),(finalLoc+".xls")};

        ArrayList<Uri> uriArr = new ArrayList<>();
        for(String file : attchArr){
            File fIn = new File(file);
            if(fIn.exists()) {
                Uri uri = Uri.fromFile(fIn);
                uriArr.add(uri);
            }
        }

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("message/rfc882");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uriArr);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Family Wallet Backup");
        intent.createChooser(intent,getString(R.string.exportdata_ismailcreator_createchooser));
        startActivity(intent);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.getId()==R.id.csvCheck){
            if (b){
                csvChecked = true;
            }else {
                csvChecked = false;
            }
        }else if(compoundButton.getId()==R.id.exelCheck){
            if (b){
                exelChecked = true;
            }else {
                exelChecked = false;
            }
        }else if(compoundButton.getId()==R.id.sendBackMail){
            if (b){
                mailChecked = true;
            }else {
                mailChecked = false;
            }
        }

    }
}
