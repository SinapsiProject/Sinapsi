package com.sinapsi.android.utils.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.sinapsi.android.Lol;
import com.sinapsi.android.utils.VersionUtils;

import java.util.List;
import java.util.Map;

/**
 * An utility class to manage animated visibility transitions between groups of views.
 */
public class ViewTransitionManager {


    private Map<String, List<View>> map;
    private String currentState = "";
    private VisibilityChangeAnimation animationMethod;


    public ViewTransitionManager(Map<String, List<View>> viewsmap){
        map = viewsmap;
        animationMethod = VISIBILITY_CHANGE_ANIMATION_DEFAULT;
    }

    public void makeTransition(String viewsKey){
        if (map.containsKey(viewsKey)){
            //hides everything
            for(List<View> lv: map.values()){
                for(View v: lv)
                    showView(false, v);
            }
            //shows only the selected views
            for(View sv: map.get(viewsKey))
                showView(true, sv);
            currentState = viewsKey;
        }else throw new RuntimeException("Invalid key: "+viewsKey);
    }

    public void makeTransitionIfDifferent(String viewsKey){
        if(!currentState.equals(viewsKey)) makeTransition(viewsKey);
    }

    public String getCurrentState(){
        return currentState;
    }

    public void showView(final boolean show, final View view){
        animationMethod.setVisibility(view,show);
    }

    public void setAnimationMethod(VisibilityChangeAnimation am){
        animationMethod = am;
    }

    public static final VisibilityChangeAnimation VISIBILITY_CHANGE_ANIMATION_DEFAULT = new VisibilityChangeAnimation() {

        private int animShortTime = 500;

        @Override
        @SuppressLint("NewApi")
        public void setVisibility(final View v, final boolean show) {
            VersionUtils.versionedDo(
                    new VersionUtils.VersionedTask(-1) {
                        @Override
                        public void doTask() {
                            Lol.d("TransitionAnimation","No animation");
                            v.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    },
                    Build.VERSION.SDK_INT,
                    new VersionUtils.VersionedTask(Build.VERSION_CODES.HONEYCOMB_MR2) {
                        @Override
                        public void doTask() {
                            Lol.d("TransitionAnimation", "Alpha animation");
                            v.setVisibility(show ? View.GONE : View.VISIBLE);
                            Lol.d("TransitionAnimation","visibility -> " + ((v.getVisibility() == View.VISIBLE)? "visible":"gone"));
                            v.setAlpha(show ? 0 : 1);

                            v.animate().setDuration(animShortTime).alpha(show ? 1 : 0)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            v.setVisibility(show ? View.VISIBLE : View.GONE);
                                            Lol.d("TransitionAnimation", "visibility -> " + ((v.getVisibility() == View.VISIBLE) ? "visible" : "gone"));
                                        }
                                    }).start();
                            //TODO: alpha animation seems not to work
                        }
                    });
        }
    };

}
