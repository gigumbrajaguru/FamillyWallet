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
    private static DatabaseReference mDatabase;
    String Fname="1111";
    Object[] array,array10,array20;
    String[] title1,catName1,status1;
    Integer[] imgId1;
    int check=0;
    budgetListAd addList;
    final List<String> lBname = new ArrayList<String>();
    final List<String> lcat = new ArrayList<String>();
    final List<String> lstat = new ArrayList<String>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Toast.makeText(getContext(), "Budget", Toast.LENGTH_LONG).show();
        View view = inflater.inflate(R.layout.budget_list, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ListView budList = (ListView) view.findViewById(R.id.list);
        dataListCollector(view);
        budList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = ((TextView) view.findViewById(R.id.budStat)).getText().toString();
                Intent newInt1 = new Intent("ccpe001.familywallet.budget.budgetTrack");
                startActivity(newInt1);
            }
        });
        FloatingActionButton f=(FloatingActionButton)view.findViewById(R.id.fab1);
        f.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newInt3 = new Intent(getContext(),BudgetHandling.class);
               startActivity(newInt3);
            }
        });
        return view;
    }
    public boolean dataListCollector(View view){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ListView budList = (ListView) view.findViewById(R.id.list);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserInfo").orderByChild("userId").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Fname=child.child("familyId").getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Translate getcat =new Translate();
       /* mDatabase.child("getCategoryIDBudget").orderByChild("familyId").equalTo(Fname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String xy = child.child("bName").getValue().toString();
                    String xyz=child.child("stat").getValue().toString();
                    String xxy=child.child("cat").getValue().toString();
                    lBname.add(xy);
                    lcat.add(xyz);
                    lstat.add(xxy);
                }
                array = new Object[lBname.size()];
                title1 = new String[lBname.size()];
                array10 = new Object[lcat.size()];
                catName1 = new String[lcat.size()];
                array20 = new Object[lstat.size()];
                status1 = new String[lstat.size()];
                array = lBname.toArray();
                array10= lcat.toArray();
                array20=lstat.toArray();
                for (int y = 0; y < array.length; y++) {
                    title1[y] = (String) array[y];
                    catName1[y]=(String)array10[y];
                    status1[y]=(String)array20[y];
                    imgId1[y]=getcat.getCategoryID(catName[y]);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
            final String[] title = title1.clone();
            final String[] catName = catName1.clone();
            final String[] status = status1.clone();
            final Integer[] imgId = imgId1.clone();
            addList = new budgetListAd(getActivity(), title, catName, status, imgId);
            budList.setAdapter(addList);*/
        return true;
        }

    }


