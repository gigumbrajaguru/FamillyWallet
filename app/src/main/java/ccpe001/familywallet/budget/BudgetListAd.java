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
 * Created by Gigum on 2017-05-29.
 */


/**This class use to add data into list view box**/
public class BudgetListAd extends ArrayAdapter<String> {
    private final Activity context;
    private final String[]title;
    private final String[]catName;
    private final String[]status;
    private final Integer[]imgId;
    private final String[] key;
    private final String[] ends;
    private final String[] percentage;


    public BudgetListAd(Activity context, String[]title, String[]catName, String[]status, Integer[]imgid, String[] budkey, String[] pert, String[] end){
        super(context, R.layout.bud_list_view, title);
        this.context=context;
        this.title=title;
        this.catName=catName;
        this.status=status;
        this.imgId=imgid;
        this.key=budkey;
        this.ends=end;
        this.percentage=pert;
    }
    /**Set budget data on list view layout**/
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.bud_list_view, null, true);

        TextView budTitle = (TextView) v.findViewById(R.id.budName);
        TextView budcat = (TextView) v.findViewById(R.id.budCat);
        TextView budstat = (TextView) v.findViewById(R.id.budStat);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        TextView budID=(TextView)v.findViewById(R.id.txtbudgetId);
        TextView ttype=(TextView)v.findViewById(R.id.typesAcc);
        budID.setVisibility(TextView.INVISIBLE);
        budTitle.setText(title[position]);
        budcat.setText(catName[position]);
        budstat.setText(status[position]);
        budID.setText(key[position]);
        ttype.setText(percentage[position]+"%");
        img.setImageResource(imgId[position]);
        return v;

    }


}
