package ccpe001.familywallet.transaction;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ccpe001.familywallet.R;
import ccpe001.familywallet.Translate;
import ccpe001.familywallet.Validate;

/**
 * Created by Knight on 5/23/2017.
 */

public class TransactionListAdapter extends ArrayAdapter<TransactionDetails> {

    private Activity context;
    private List<TransactionDetails> tdList;
    Translate trns = new Translate();


    public TransactionListAdapter(Activity context, List<TransactionDetails> tdList) {
        super(context, R.layout.transaction_main, tdList);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.tdList = tdList;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView;
        TransactionDetails td = tdList.get(position);
        final Validate v = new Validate();


            rowView = inflater.inflate(R.layout.transaction_row, null, true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.txtTitle);
            TextView txtCategory = (TextView) rowView.findViewById(R.id.txtCategory);
            TextView txtDate = (TextView) rowView.findViewById(R.id.txtTime);
            TextView txtAmount = (TextView) rowView.findViewById(R.id.txtAmount);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);



            String type = td.getType();
            if (type.equals("Income")){
                txtAmount.setTextColor(ContextCompat.getColor(getContext(),R.color.income));
                txtTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.income));
                txtCategory.setTextColor(ContextCompat.getColor(getContext(),R.color.income));
                txtDate.setTextColor(ContextCompat.getColor(getContext(),R.color.income));
                txtAmount.setText("+"+trns.currencyView(td.getCurrency(),getContext())+td.getAmount());
                txtTitle.setText(td.getTitle());
                txtCategory.setText(trns.categoryView(td.getCategoryName(),getContext()));
                txtDate.setText(trns.dateView(td.getDate(),getContext()));
            }else if (type.equals("Expense")){
                txtAmount.setTextColor(ContextCompat.getColor(getContext(),R.color.expense));
                txtTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.expense));
                txtCategory.setTextColor(ContextCompat.getColor(getContext(),R.color.expense));
                txtDate.setTextColor(ContextCompat.getColor(getContext(),R.color.expense));
                txtAmount.setText("-"+trns.currencyView(td.getCurrency(),getContext())+td.getAmount());
                txtTitle.setText(td.getTitle());
                txtCategory.setText(trns.categoryView(td.getCategoryName(),getContext()));
                txtDate.setText(trns.dateView(td.getDate(),getContext()));
            }
            imageView.setImageResource(td.getCategoryID());





        return rowView;


    }
    public String getget(int position, List<TransactionDetails> tdListt){
        TransactionDetails td = tdListt.get(position);
        return td.getAmount();
    }
}