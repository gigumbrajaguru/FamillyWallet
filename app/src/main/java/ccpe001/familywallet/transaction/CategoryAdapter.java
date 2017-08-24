package ccpe001.familywallet.transaction;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ccpe001.familywallet.R;

/*Class to set the category gridview*/
public class CategoryAdapter extends ArrayAdapter<String> {

    /*initializing variables for current context, image name & image ID*/
    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    /*Assigning context, image name & image ID when method called*/
    public CategoryAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.category_list, itemname);
        this.context = context;
        this.itemname = itemname;
        this.imgid = imgid;
    }

    /*populating category grid view*/
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.category_list, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt1);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img1);
        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        return rowView;

    }
}