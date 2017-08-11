package ccpe001.familywallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ccpe001.familywallet.transaction.TransactionDetails;
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
    private Button createBtn;
    private EditText fileName;
    private Switch isMail;
    private String filename,finalLoc;
    private boolean exelChecked;
    private boolean csvChecked;
    private boolean mailChecked;
    private static final int EXTERNAL_READ_PERMIT = 3;
    private static final int EXTERNAL_WRITE_PERMIT = 4;
    private DatabaseReference databaseReference;
    private TransactionDetails tdata;
    private CSVWriter writer;
    private WritableWorkbook workbook;
    private File file;
    private WritableSheet sheet;
    private SharedPreferences prefs;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.exportdata, container, false);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Transactions");
        databaseReference.keepSynced(true);
        init(view);
        return view;
    }

    private void init(View view) {
        checkCSV = (CheckBox)view.findViewById(R.id.csvCheck);
        checkEXEL = (CheckBox)view.findViewById(R.id.exelCheck);
        createBtn = (Button)view.findViewById(R.id.createBtn);
        fileName = (EditText) view.findViewById(R.id.filename);
        isMail = (Switch)view.findViewById(R.id.sendBackMail);
        createBtn.setOnClickListener(this);
        checkCSV.setOnCheckedChangeListener(this);
        checkEXEL.setOnCheckedChangeListener(this);
        isMail.setOnCheckedChangeListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == EXTERNAL_READ_PERMIT||requestCode == EXTERNAL_WRITE_PERMIT){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED||grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(),R.string.permitgranted,Toast.LENGTH_SHORT).show();
            }else {
                checkPermitBackup(getActivity());
            }
        }
    }

    protected static boolean checkPermitBackup(Context c){
        return ActivityCompat.checkSelfPermission(c, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(c, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.createBtn){
            if (Validate.fileValidate(fileName.getText())) {
                filename = fileName.getText().toString();

                if (!checkPermitBackup(getActivity())) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},EXTERNAL_READ_PERMIT);
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_WRITE_PERMIT);
                }else {


                    try {
                        if ((!csvChecked) && (!exelChecked)) {
                            Toast.makeText(getContext(), getString(R.string.exportdata_onclick_checkticks), Toast.LENGTH_SHORT).show();
                        } else {
                            export();
                            Toast.makeText(getContext(), getString(R.string.exportdata_onclick_backupdone), Toast.LENGTH_SHORT).show();
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

    public void export() throws IOException {
        prefs = getContext().getSharedPreferences("App Settings", Context.MODE_PRIVATE);
        finalLoc = prefs.getString("appBackUpPath","/storage/emulated/0/");
        finalLoc += filename;

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int countRow = 0;

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (csvChecked) {

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
