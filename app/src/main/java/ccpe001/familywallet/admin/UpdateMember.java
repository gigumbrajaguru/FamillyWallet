package ccpe001.familywallet.admin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ccpe001.familywallet.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateMember extends Fragment {


    public UpdateMember() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_member, container, false);
    }

}
