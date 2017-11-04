package ccpe001.familywallet.budget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ccpe001.familywallet.R;

/**
 * Created by Gigum on 2017-09-15.
 */

/***** This class use to pass account details for account list view*****/

public class AccountListAd extends ArrayAdapter{
    private final Activity context;
    private final String[]accountName;
    private final String[]status;
    private final Integer[]imgId;
    private final String[] key;
    private final String[] accAmounts;
    private final String[] type;
    private final String[] curtype;


    public AccountListAd(Activity context, String[]accountNames, String[] accAmount , String[] issaving, Integer[]imgid, String[] keyes, String[] types, String[] curtypes){
        super(context, R.layout.bud_list_view, accountNames);
        this.context=context;
        this.accountName=accountNames;
        this.status=issaving;
        this.imgId=imgid;
        this.key=keyes;
        this.accAmounts=accAmount;
        this.type=types;
        this.curtype=curtypes;
    }
        /**Set passed data in Inflaters field**/
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.bud_list_view, null, true);
        TextView ttype=(TextView)v.findViewById(R.id.typesAcc);
        TextView accname = (TextView) v.findViewById(R.id.budCat);
        TextView accAmount=(TextView)v.findViewById(R.id.budName);
        TextView isavings = (TextView) v.findViewById(R.id.budStat);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        TextView budID=(TextView)v.findViewById(R.id.txtbudgetId);
        budID.setVisibility(TextView.INVISIBLE);
        ttype.setText(type[position]);
        ttype.setTextSize(15);
        accAmount.setText(curtype[position]+" "+accAmounts[position]);
        accAmount.setTextSize(15);
        accname.setText(accountName[position]);
        accname.setTextSize(20);
        isavings.setText(status[position]);
        budID.setText(key[position]);
        img.setImageResource(imgId[position]);
        return v;

    }




}
