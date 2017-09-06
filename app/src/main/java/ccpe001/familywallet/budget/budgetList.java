package ccpe001.familywallet.budget;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import ccpe001.familywallet.R;
import ccpe001.familywallet.Translate;


public class budgetList extends Fragment {
    private DatabaseReference mDatabase;
    Object[] array={},array10={},array20={},arrayk={};
    String[] title1={},catName1={},status1={},bugKey={};
    Integer[] imgId1={};
    budgetListAd addList;
    final List<String> lkey = new ArrayList<String>();
    final List<String> lBname = new ArrayList<String>();
    final List<String> lcat = new ArrayList<String>();
    final List<String> lstat = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Toast.makeText(getContext(), "Budget", Toast.LENGTH_LONG).show();
        final View view = inflater.inflate(R.layout.budget_list, container, false);
        ListView budList = (ListView) view.findViewById(R.id.list);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("UserInfo").orderByChild("userId").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    setList(view,child.child("familyId").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        budList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = ((TextView) view.findViewById(R.id.budStat)).getText().toString();
                Intent newInt1 = new Intent("ccpe001.familywallet.budget.budgetTrack");
                newInt1.putExtra("budgetID", selected);
                startActivity(newInt1);
            }
        });
        FloatingActionButton f = (FloatingActionButton) view.findViewById(R.id.fab1);
        f.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newInt3 = new Intent(getContext(), BudgetHandling.class);
                startActivity(newInt3);
            }
        });
        return view;
    }

    public boolean setList(final View view, String Fid) {
        mDatabase.child("Budget").orderByChild("familyId").equalTo(Fid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String xy = child.child("bName").getValue().toString();
                    String xyz=child.child("status").getValue().toString();
                    String xxy=child.child("cat").getValue().toString();
                    String xyy=child.getKey().toString();
                    lkey.add(xyy);
                    lBname.add(xy);
                    lcat.add(xxy);
                    lstat.add(xyz);
                }
                arrayk=new Object[lkey.size()];
                bugKey=new String[lkey.size()];
                array = new Object[lBname.size()];
                title1 = new String[lBname.size()];
                array10 = new Object[lcat.size()];
                catName1 = new String[lcat.size()];
                array20 = new Object[lstat.size()];
                status1 = new String[lstat.size()];
                imgId1=new Integer[lBname.size()];
                array = lBname.toArray();
                array10= lcat.toArray();
                array20=lstat.toArray();
                arrayk=lkey.toArray();
                Translate getcat =new Translate();
                for (int y = 0; y < array.length; y++) {
                    title1[y] = (String) array[y];
                    catName1[y] = (String) array10[y];
                    status1[y] = (String) array20[y];
                    bugKey[y]=(String)arrayk[y];
                    imgId1[y]=getcat.getCategoryID(catName1[y]);

                }
                pushList(view,title1,catName1,status1,imgId1,bugKey);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return true;
    }

    public void pushList(View view,String title1[],String catName1[],String status1[],Integer imgId1[],String budKey[]) {
        String[] title,catName,status,budKeys;
        Integer[] imgId;
        ListView budList = (ListView) view.findViewById(R.id.list);
        title = title1.clone();
        catName= catName1.clone();
        status = status1.clone();
        imgId = imgId1.clone();
        budKeys=budKey.clone();
        addList = new budgetListAd(getActivity(), title, catName, status, imgId,budKeys);
        budList.setAdapter(addList);
    }
}


