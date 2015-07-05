package com.sinapsi.android.utils.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.sinapsi.android.Lol;
import com.sinapsi.android.utils.VersionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

            Set<View> viewSet = new HashSet<>();
            for(List<View> lv: map.values())
                viewSet.addAll(lv);

            for(View v: viewSet){

                boolean previouslyVisible = false;
                boolean show = false;


                if(!currentState.equals("") && map.get(currentState).contains(v))
                    previouslyVisible = true;

                if(map.get(viewsKey).contains(v))
                    show = true;

                if(previouslyVisible && !show){
                    fadeOut(v);
                }else if(!previouslyVisible && show){
                    fadeIn(v);
                }
            }

            currentState = viewsKey;

        } else throw new RuntimeException("Invalid key: "+viewsKey);
    }

    public void makeTransitionIfDifferent(String viewsKey){
        if(!currentState.equals(viewsKey)) makeTransition(viewsKey);
    }

    public String getCurrentState(){
        return currentState;
    }

    public void fadeIn(final View view){
        animationMethod.fadeIn(view);
    }

    public void fadeOut(final View view){
        animationMethod.fadeOut(view);
    }


    public void setAnimationMethod(VisibilityChangeAnimation am){
        animationMethod = am;
    }

    public static final VisibilityChangeAnimation VISIBILITY_CHANGE_ANIMATION_DEFAULT = new VisibilityChangeAnimation() {

        private int animShortTime = 500;

        @Override
        @SuppressLint("NewApi")
        public void fadeIn(final View v) {
            VersionUtils.versionedDo(
                    new VersionUtils.VersionedTask(-1) {
                        @Override
                        public void doTask() {
                            Lol.d("TransitionAnimation","No animation");
                            v.setVisibility(View.VISIBLE);
                        }
                    },
                    Build.VERSION.SDK_INT,
                    new VersionUtils.VersionedTask(Build.VERSION_CODES.HONEYCOMB_MR2) {
                        @Override
                        public void doTask() {

                            v.setVisibility(View.VISIBLE);
                            v.setAlpha(0);
                            v.animate().setDuration(animShortTime).alpha(1)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            v.setVisibility(View.VISIBLE);
                                        }
                                    }).start();
                        }
                    });
        }

        @Override
        @SuppressLint("NewApi")
        public void fadeOut(final View v) {
            VersionUtils.versionedDo(
                    new VersionUtils.VersionedTask(-1) {
                        @Override
                        public void doTask() {
                            Lol.d("TransitionAnimation","No animation");
                            v.setVisibility(View.VISIBLE);
                        }
                    },
                    Build.VERSION.SDK_INT,
                    new VersionUtils.VersionedTask(Build.VERSION_CODES.HONEYCOMB_MR2) {
                        @Override
                        public void doTask() {

                            v.setVisibility(View.VISIBLE);
                            v.setAlpha(1);
                            v.animate().setDuration(animShortTime).alpha(0)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            v.setVisibility(View.GONE);
                                        }
                                    }).start();
                        }
                    });
        }
    };

}
