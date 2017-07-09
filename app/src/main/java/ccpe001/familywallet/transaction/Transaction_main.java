package ccpe001.familywallet.transaction;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ccpe001.familywallet.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Transaction_main extends Fragment {

    ListView list;
    FloatingActionButton fab_income, fab_expense,fab_main;
    Animation fabOpen, fabClose, fabClockwise, fabAntiClockwise;
    TextView txtIncome,txtExpense;
    boolean isOpen = false;
    String categoryID, categoryName, title, date, amount;
    private DatabaseReference mDatabase;

    public Transaction_main() {
        // Required empty public constructor
    }

        ArrayList<String> Amount ;
        ArrayList<String> Title ;
        ArrayList<String> Category ;
        ArrayList<String> Date ;
        ArrayList<Integer> imgid;
        ArrayList<String> Currency ;
        List<TransactionDetails> tdList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.transaction_main, container, false);
        list = (ListView) view.findViewById(R.id.transactionList);
        tdList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Transactions");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tdList.clear();
                for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                    TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                    tdList.add(td);
                }

                TransactionListAdapter adapter = new TransactionListAdapter(getActivity(),tdList);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                list.setItemChecked(position, true);
                view.setSelected(true);
                return true;
            }
        });
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedItems = list.getCheckedItemCount();
                mode.setTitle(String.valueOf(checkedItems)+ " Selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.transaction_main, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case  R.id.delete_id:
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(AdapterView<?> parent, View view,
//                                                                int position, long id) {
//                                            // TODO Auto-generated method stub
//                                            Toast.makeText(getActivity(), "HEllo", Toast.LENGTH_SHORT).show();
//                                            view.setSelected(true);
//                                            list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
//                                        }
//
//
//                                    });


        txtExpense = (TextView) view.findViewById(R.id.txtExpense);
        txtIncome = (TextView) view.findViewById(R.id.txtIncome);
        fab_main = (FloatingActionButton) view.findViewById(R.id.fabMain);
        fab_expense = (FloatingActionButton) view.findViewById(R.id.fabExpense);
        fab_income = (FloatingActionButton) view.findViewById(R.id.fabIncome);
        fabOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fabClockwise = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_clockwise);
        fabAntiClockwise = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_anticlockwise);
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOpen){
                    fab_income.startAnimation(fabClose);
                    fab_expense.startAnimation(fabClose);
                    txtExpense.setAnimation(fabClose);
                    txtIncome.setAnimation(fabClose);
                    txtExpense.setClickable(false);
                    txtIncome.setClickable(false);
                    fab_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1976d2")));
                    fab_main.setScaleType(ImageView.ScaleType.CENTER);
                    fab_main.setImageResource(R.mipmap.add_transaction);
                    fab_income.setClickable(false);
                    fab_expense.setClickable(false);
                    isOpen = false;

                }
                else {
                    fab_income.startAnimation(fabOpen);
                    fab_expense.startAnimation(fabOpen);
                    txtExpense.setAnimation(fabOpen);
                    txtIncome.setAnimation(fabOpen);
                    txtExpense.setClickable(true);
                    txtIncome.setClickable(true);
                    fab_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffcc0000")));
                    fab_main.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    fab_main.setImageResource(R.mipmap.cancel);
                    fab_income.setClickable(true);
                    fab_expense.setClickable(true);
                    isOpen = true;
                }

                if (isOpen) {
                    fab_income.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent("ccpe001.familywallet.add_transaction");
                            intent.putExtra("transactionType","Income");
                            startActivity(intent);
                        }
                    });
                    fab_expense.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent("ccpe001.familywallet.add_transaction");
                            intent.putExtra("transactionType","Expense");
                            startActivity(intent);
                        }
                    });
                }

            }
        });






        return view;
    }



}
