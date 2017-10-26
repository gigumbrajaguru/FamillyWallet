package ccpe001.familywallet.transaction;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.internal.gr;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import ccpe001.familywallet.R;
import ccpe001.familywallet.Translate;
import ccpe001.familywallet.Validate;
import ccpe001.familywallet.admin.CircleTransform;

import static ccpe001.familywallet.transaction.FamilyExpensesIncomes.id_total;

/**
 * Created by Knight on 10/3/2017.
 */

public class GroupListAdapter extends ArrayAdapter<GroupDetails> {

    private Activity context;
    private List<GroupDetails> grList;
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    private FamilyExpensesIncomes expensesIncomes;
    private String familyID;
    private Double[] totalExpensesIncomes ;

    public GroupListAdapter(Activity context, List<GroupDetails> grList,String familyId) {
        super(context, R.layout.transaction_family_slideup, grList);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.grList = grList;
        this.familyID=familyId;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView;
        GroupDetails grpDetail = grList.get(position);


        rowView = inflater.inflate(R.layout.group_row_leaderboard, null, true);
        TextView txtName = (TextView) rowView.findViewById(R.id.txtGrName);
        TextView txtTotalIncome = (TextView) rowView.findViewById(R.id.txtIncomeAmount);
        TextView txtTotalExpense = (TextView) rowView.findViewById(R.id.txtExpenseAmount);
        final ImageView proPic = (ImageView) rowView.findViewById(R.id.proPic);
        totalExpensesIncomes = id_total.get(grpDetail.getUserID());

        txtTotalIncome.setText(grpDetail.getTotalIncome().toString());
        txtTotalExpense.setText(grpDetail.getTotalExpense().toString());


        txtName.setText(grpDetail.getFirstName());
        try {
            if(grpDetail.getProPic().equals("Storage")){

                storageReference.child("UserPics/" + grpDetail.getUserID() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(context.getApplication())
                                .load(uri)
                                .into(proPic);
                    }
                });
            }else{

                Picasso.with(context.getApplication())
                        .load(grpDetail.getProPic())
                        .transform(new CircleTransform())
                        .into(proPic);
            }
        }catch (Exception e){
            Log.i("echoPro",""+e);
        }
        return rowView;


    }
}
