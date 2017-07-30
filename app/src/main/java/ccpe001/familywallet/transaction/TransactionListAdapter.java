package ccpe001.familywallet.transaction;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ccpe001.familywallet.R;
import ccpe001.familywallet.Validate;

/**
 * Created by Knight on 5/23/2017.
 */

public class TransactionListAdapter extends ArrayAdapter<TransactionDetails> {

    private Activity context;
    private List<TransactionDetails> tdList;
//    private final ArrayList<String> title;
//    private final ArrayList<String> category;
//    private final ArrayList<String> date;
//    private final ArrayList<String> amount;
//    private final ArrayList<Integer>imgid;


    public TransactionListAdapter(Activity context, List<TransactionDetails> tdList) {
        super(context, R.layout.category_list, tdList);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.tdList = tdList;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView;
        TransactionDetails td = tdList.get(position);
        String prevDate="";

        final Validate v = new Validate();
        rowView = inflater.inflate(R.layout.transaction_list, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtTitle);
        TextView txtCategory = (TextView) rowView.findViewById(R.id.txtCategory);
        TextView txtDate = (TextView) rowView.findViewById(R.id.txtTime);
        TextView txtAmount = (TextView) rowView.findViewById(R.id.txtAmount);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);


        txtTitle.setText(td.getTitle());
        txtCategory.setText(td.getCategoryName());
        txtDate.setText(v.valueToDate(td.getDate()));
        String type = td.getType();
        if (type.equals("Income")){
            txtAmount.setText("+"+td.getCurrency()+td.getAmount());
            txtAmount.setTextColor(ContextCompat.getColor(context,R.color.income));
        }else if (type.equals("Expense")){
            txtAmount.setText("-"+td.getCurrency()+td.getAmount());
            txtAmount.setTextColor(ContextCompat.getColor(context,R.color.expense));
        }
        imageView.setImageResource(td.getCategoryID());

        return rowView;


    }

}