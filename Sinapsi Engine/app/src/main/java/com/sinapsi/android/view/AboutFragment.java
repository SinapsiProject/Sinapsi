package com.sinapsi.android.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sinapsi.android.background.SinapsiFragment;
import com.sinapsi.android.R;

/**
 * About fragment with app infos and links.
 */
public class AboutFragment extends SinapsiFragment {
    private static final int ANIM_TOTAL_DURATION = 1500;
    private static final int ANIM_START_OFFSET = 500;

    @Override
    public String getName(Context context) {
        return context.getString(R.string.about_fragment_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        Animation alpha1 = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
        alpha1.setStartOffset(ANIM_START_OFFSET);
        alpha1.setDuration(ANIM_TOTAL_DURATION / 3);
        alpha1.setFillAfter(true);

        Animation alpha2 = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
        alpha1.setFillAfter(true);
        alpha2.setStartOffset(ANIM_START_OFFSET + alpha1.getDuration());
        alpha2.setDuration(ANIM_TOTAL_DURATION / 3);

        Animation alpha3 = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
        alpha1.setFillAfter(true);
        alpha3.setStartOffset(ANIM_START_OFFSET + alpha1.getDuration() + alpha2.getDuration());
        alpha3.setDuration(ANIM_TOTAL_DURATION / 3);


        rootView.findViewById(R.id.logo).startAnimation(alpha1);
        rootView.findViewById(R.id.brought_label).startAnimation(alpha2);
        rootView.findViewById(R.id.names_label).startAnimation(alpha3);


        return rootView;
    }
}
