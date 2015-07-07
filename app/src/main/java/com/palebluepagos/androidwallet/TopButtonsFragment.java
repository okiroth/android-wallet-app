package com.palebluepagos.androidwallet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ivan on 3/30/15.
 */
public class TopButtonsFragment extends Fragment {

    public View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_top_buttons, container, false);

        if (HomeActivity.superUser) {
            rootView.findViewById(R.id.pay_super_button).setVisibility(View.VISIBLE);
        }

        return rootView;
    }

}
