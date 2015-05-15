package com.sinapsi.android.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.view.View;

import java.util.List;
import java.util.Map;

/**
 *
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
            //shows only the selected view group
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
        public void setVisibility(final View view, final boolean show) {
            VersionUtils.versionedDo(
                    new VersionUtils.VersionedTask(-1) {
                        @Override
                        public void doTask() {
                            view.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    },
                    Build.VERSION.SDK_INT,
                    new VersionUtils.VersionedTask(Build.VERSION_CODES.HONEYCOMB_MR2) {
                        @Override
                        public void doTask() {
                            //view.setVisibility(show ? View.VISIBLE : View.GONE);
                            //view.setAlpha(0.0f);
                            view.animate().setDuration(animShortTime).alpha(show ? 1 : 0)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            view.setVisibility(show ? View.VISIBLE : View.GONE);
                                        }
                                    }).start();
                        }
                    });
        }
    };

}
