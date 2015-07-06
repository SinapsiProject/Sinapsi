package com.sinapsi.android.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sinapsi.android.background.SinapsiFragment;
import com.sinapsi.android.R;

/**
 * About fragment with app infos and links.
 */
public class AboutFragment extends SinapsiFragment {
    @Override
    public String getName(Context context) {
        return context.getString(R.string.about_fragment_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

        //TODO: impl
    }
}
