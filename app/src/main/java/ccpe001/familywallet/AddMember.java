package ccpe001.familywallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

import ccpe001.familywallet.transaction.GroupDetails;
import ccpe001.familywallet.transaction.TransactionDetails;

/**
 * Created by harithaperera on 5/10/17.
 * Updated by Knight.
 */
public class AddMember extends Fragment  implements View.OnClickListener{

    private ImageView qrImage;
    private TextView addMemberTitle, qrError;
    private String userID, familyID, fname, qrValue="",InGroup;
    private Button scanqrbtn, btnLeave;
    private DatabaseReference mDatabase, gDatabase;
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;

    SharedPreferences sharedPref;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.add_member, container, false);


        /* Getting the user id and family id from shared preferences  */
        sharedPref = this.getContext().getSharedPreferences("fwPrefs",0);
        String uid = sharedPref.getString("uniUserID", "");
        String fid = sharedPref.getString("uniFamilyID", "");
        String name = sharedPref.getString("uniFname", "");
        InGroup = sharedPref.getString("InGroup", "");
        editor = sharedPref.edit();
        userID = uid;
        familyID = fid;
        fname=  name;

        qrValue = "fwValid:"+userID+":"+fname ; //Output value for QR string

        /* Getting ad populating list with group member info */
        gDatabase = FirebaseDatabase.getInstance().getReference("Groups").child(familyID);
        recyclerView=(RecyclerView)view.findViewById(R.id.grDetails);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                FirebaseRecyclerAdapter<GroupDetails,GrDetailsViewHolder> adapter = new FirebaseRecyclerAdapter<GroupDetails,GrDetailsViewHolder>(
                GroupDetails.class,
                R.layout.group_row,
                GrDetailsViewHolder.class,
                gDatabase
        ){

            @Override
            protected void populateViewHolder(GrDetailsViewHolder viewHolder, GroupDetails model, int position) {
                viewHolder.setName(model.getFirstName());
            }
        };

        recyclerView.setAdapter(adapter);


        mDatabase = FirebaseDatabase.getInstance().getReference();  //getting database reference to create group

        /*Getting the updated family ID when joining a group and saving it in shared preferences*/
        FirebaseDatabase.getInstance().getReference("UserInfo").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                familyID=dataSnapshot.child("familyId").getValue().toString();
                /* saving user id, family id and first name in preferences */
                changeLayout(userID,familyID);
                editor.putString("uniFamilyID", familyID);
                editor.commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        init(view);
        return view;
    }

    /**
     * This method is used to initialize elements in the xml file
     * @param v
     */
    private void init(View v) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.addmember_title);
        addMemberTitle = (TextView) v.findViewById(R.id.addMemberTitle);
        scanqrbtn = (Button) v.findViewById(R.id.scanqrbtn);
        scanqrbtn.setOnClickListener(this);

        /* Leave Group button clicked */
        btnLeave = (Button) v.findViewById(R.id.btnLeave);
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup(userID,familyID);
            }
        });
        qrError = (TextView) v.findViewById(R.id.qrError);
        qrError.setOnClickListener(this);
        qrImage = (ImageView) v.findViewById(R.id.qrCodeImage);
        setQr();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.qrError) {
            System.exit(0);
        }else if(view.getId() == R.id.scanqrbtn){
            qrReader();

        }
    }

    /**
     * This method is used to generate QR code
     */
    private void setQr(){
        MultiFormatWriter mfw = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = mfw.encode(qrValue, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to open the QR code scanner for specified properties.
     * After result is cached in onActivityResult();
     */
    private void qrReader(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt(getString(R.string.signin_scan_setpromt));
        intentIntegrator.setCameraId(0);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setBarcodeImageEnabled(true);
        intentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if (result.getContents()== null){      //Scan cancelled
                Toast.makeText(getActivity(),R.string.signup_cancel_toast,Toast.LENGTH_LONG).show();
            }else {
                String[] parts = result.getContents().split(":");      //Split QR String into parts
                 /* checking if the scanning qr is valid */
                if (parts[0].equals("fwValid")) {
                    InGroup="true";
                    GroupDetails groupDetails = new GroupDetails(parts[1], parts[2]);   //new members details
                    GroupDetails groupHDetails = new GroupDetails(userID, fname);       //admins details
                    mDatabase.child("Groups").child(userID).child(parts[1]).setValue(groupDetails); //adding new members details to db
                    mDatabase.child("Groups").child(userID).child(userID).setValue(groupHDetails);  //adding admins  details to db
                    mDatabase.child("UserInfo").child(parts[1]).child("familyId").setValue(userID); //Changing family ID in user details
                    editor.putString("InGroup", "true");    //InGroup value for admin because user ID and Family ID is equal
                    editor.commit();
                    changeLayout(userID,familyID);  //change layout after adding a new member

                }
                else {
                    Toast.makeText(getActivity(), "Invalid QR", Toast.LENGTH_LONG).show();  //if QR is invalid
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* method to change layout */
    public void changeLayout(final String uID, String fID){
        /* when a member in a group make scan button,qr image, qr error visible and leave button gone */
        if (fID.equals(uID)  && !InGroup.equals("true")){
            scanqrbtn.setVisibility(View.VISIBLE);
            qrImage.setVisibility(View.VISIBLE);
            qrError.setVisibility(View.VISIBLE);
            addMemberTitle.setText(R.string.xmladdmember_editText10_text);
            btnLeave.setVisibility(View.GONE);
        }
        /* when a admin in a group make qr image, qr error gone and scan button, leave button visible */
        else if (fID.equals(uID) && InGroup.equals("true")){
            scanqrbtn.setVisibility(View.VISIBLE);
            qrImage.setVisibility(View.GONE);
            qrError.setVisibility(View.GONE);
            addMemberTitle.setText("Group Members");
            btnLeave.setVisibility(View.VISIBLE);

        }
        /* when a member in a group make scan button,qr image, qr error gone and leave button visible */
        else if (!fID.equals(uID)){
            scanqrbtn.setVisibility(View.GONE);
            qrImage.setVisibility(View.GONE);
            qrError.setVisibility(View.GONE);
            addMemberTitle.setText("Group Details");
            btnLeave.setVisibility(View.VISIBLE);


        }
    }
    private void leaveGroup(String uID, String fID) {

        /* change layout and remove node from group db*/
        changeLayout(uID,fID);
        mDatabase.child("UserInfo").child(uID).child("familyId").setValue(uID);
        DatabaseReference transaction = FirebaseDatabase.getInstance().getReference("Groups").child(fID).child(uID);
        transaction.removeValue();
        editor.putString("InGroup", "false");
        editor.commit();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

    }

    /* view holder for group details for member info */
    public static class GrDetailsViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        public GrDetailsViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.txtGrName);
        }

        public void setName(String name) {
            txtName.setText(name);
        }

    }
}
