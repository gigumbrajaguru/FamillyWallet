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
 * Created by Knight on 10/3/2017.
 */

public class GroupListAdapter extends ArrayAdapter<GroupDetails> {

    private Activity context;
    private List<GroupDetails> grList;


    public GroupListAdapter(Activity context, List<GroupDetails> grList) {
        super(context, R.layout.transaction_family_slideup, grList);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.grList = grList;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView;
        GroupDetails grpDetail = grList.get(position);


        rowView = inflater.inflate(R.layout.group_row, null, true);
        TextView txtName = (TextView) rowView.findViewById(R.id.txtGrName);
        txtName.setText(grpDetail.getFirstName());

        return rowView;


    }
}
