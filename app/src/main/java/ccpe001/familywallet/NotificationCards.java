package ccpe001.familywallet;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ccpe001.familywallet.admin.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joanzapata.iconify.widget.IconButton;
import me.leolin.shortcutbadger.ShortcutBadger;

import java.util.Arrays;
import java.util.List;

import static ccpe001.familywallet.Dashboard.badgeCount;
import static ccpe001.familywallet.Dashboard.setBadgeCount;

/**
 * Created by harithaperera on 7/10/17.
 */
public class NotificationCards extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private SQLiteHelper db;
    private TextView itemMessagesBadgeTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to enable onPrepareOptionsMenu in frag
        View view = inflater.inflate(R.layout.notificationtab, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem itemMessages = menu.findItem(R.id.action_notification);
        RelativeLayout badgeLayout = (RelativeLayout) itemMessages.getActionView();
        itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.id.badge_textView);
        Log.d("badcount"," bind view del"+itemMessagesBadgeTextView);
    }


    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder{

            public ImageButton noti_delBtn;
            public TextView noti_date,noti_desc,noti_title;

            public ViewHolder(View itemView) {
                super(itemView);
                noti_delBtn = (ImageButton)itemView.findViewById(R.id.noti_delBtn);
                noti_date = (TextView)itemView.findViewById(R.id.noti_date);
                noti_desc = (TextView)itemView.findViewById(R.id.noti_desc);
                noti_title = (TextView)itemView.findViewById(R.id.noti_title);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.notificationcard, viewGroup, false);
            return(new ViewHolder(v));
        }


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            db = new SQLiteHelper(getActivity());
            final List<SQLiteHelper.DAO> daoList = db.viewNoti();
            final SQLiteHelper.DAO dao = daoList.get(i);

            //load data to fields
            viewHolder.noti_title.setText(dao.title);
            viewHolder.noti_desc.setText(dao.desc);
            viewHolder.noti_date.setText(dao.date);

            viewHolder.noti_delBtn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                    db.deleteNoti(dao.id);
                    daoList.remove(dao);
                    badgeCount--;
                    setBadgeCount(badgeCount,itemMessagesBadgeTextView);


                    //load data to fields
                    viewHolder.noti_title.setText(dao.title);
                    viewHolder.noti_desc.setText(dao.desc);
                    viewHolder.noti_date.setText(dao.date);

                    Snackbar.make(v, R.string.noticards_onBindViewHolder_snackbar, Snackbar.LENGTH_SHORT).show();

                }
            });

        }


        @Override
        public int getItemCount() {
            db = new SQLiteHelper(getActivity());
            return db.viewNoti().size();
        }
    }
}
