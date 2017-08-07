package ccpe001.familywallet.transaction;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import ccpe001.familywallet.R;

/**
 * Created by Knight on 5/21/2017.
 */

public class CategoryTab01 extends Fragment {

    GridView grid;
    TextView txtCategory;
    String  categoryName, title, date, time, amount, location, type, update, key, eUserID, eFamilyID;
    int currencyIndex, accountIndex, categoryID;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.category_tab01, container, false);

        Resources res = getResources();
        final String[] itemname = res.getStringArray(R.array.IncomeCategory);

        final Integer[] imgid = {
                R.drawable.cat1,R.drawable.cat2,R.drawable.cat3,R.drawable.cat4,
                R.drawable.cat5,R.drawable.cat6,R.drawable.cat7,R.drawable.cat8,R.drawable.cat9,
                R.drawable.cat10,R.drawable.cat11,R.drawable.cat12,R.drawable.cat13,R.drawable.cat14,
                R.drawable.cat15,R.drawable.cat16,R.drawable.cat17,R.drawable.cat18,R.drawable.cat19,
                R.drawable.cat_other,
        };

        CategoryAdapter adapter = new CategoryAdapter(getActivity(), itemname, imgid);
        grid = (GridView) view.findViewById(R.id.tab01_list);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                txtCategory = (TextView) view.findViewById(R.id.txtCategory);

                Bundle extras = getActivity().getIntent().getExtras();

                categoryName = extras.getString("categoryName");
                categoryID = extras.getInt("categoryID");
                title = extras.getString("title");
                date = extras.getString("date");
                time = extras.getString("time");
                amount = extras.getString("amount");
                location = extras.getString("location");
                currencyIndex = extras.getInt("currencyIndex");
                accountIndex = extras.getInt("accountIndex");
                type = extras.getString("transactionType");
                update = extras.getString("Update");
                key = extras.getString("key");
                eUserID = extras.getString("userID");
                eFamilyID = extras.getString("familyID");

                categoryName = itemname[+position];
                categoryID = imgid[+position];
                Intent intent = new Intent("ccpe001.familywallet.AddTransaction");
                intent.putExtra("categoryName",categoryName);
                intent.putExtra("categoryID",categoryID);
                intent.putExtra("title",title);
                intent.putExtra("amount",amount);
                intent.putExtra("date",date);
                intent.putExtra("time",time);
                intent.putExtra("location",location);
                intent.putExtra("currencyIndex",currencyIndex);
                intent.putExtra("accountIndex",accountIndex);
                intent.putExtra("transactionType",type);
                intent.putExtra("Update",update);
                intent.putExtra("key",key);
                intent.putExtra("userID",eUserID);
                intent.putExtra("familyID",eFamilyID);
                getActivity().finish();
                startActivity(intent);



            }
        });
        return view;

    }




}
