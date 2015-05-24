package com.sinapsi.android.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.pkmmte.view.CircularImageView;
import com.sinapsi.android.Lol;
import com.sinapsi.android.background.SinapsiBackgroundService;
import com.sinapsi.android.background.SinapsiFragment;
import com.sinapsi.android.background.WebServiceConnectionListener;
import com.sinapsi.android.utils.DialogUtils;
import com.sinapsi.android.utils.GraphicsUtils;
import com.sinapsi.android.utils.animation.ViewTransitionManager;
import com.sinapsi.android.utils.lists.ArrayListAdapter;
import com.sinapsi.android.utils.swipeaction.SmartSwipeActionButton;
import com.sinapsi.android.utils.swipeaction.SwipeActionButton;
import com.sinapsi.android.utils.swipeaction.SwipeActionLayoutManager;
import com.sinapsi.engine.R;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.utils.HashMapBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * The main macro manager fragment.
 */
public class MacroManagerFragment extends SinapsiFragment implements WebServiceConnectionListener {



    private ViewTransitionManager transitionManager;
    private ArrayListAdapter<MacroInterface> macroList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean created = false;

    private boolean isListOnTop = true;
    private boolean isElementSwipingHorizontally = false;

    private enum States {
        NO_ELEMENTS,
        NO_CONNECTION,
        LIST,
        PROGRESS
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_macro_manager, container, false);



        FloatingActionButton fab =(FloatingActionButton) rootView.findViewById(R.id.new_macro_button);
        RecyclerView macroListRecycler = (RecyclerView) rootView.findViewById(R.id.macro_list_recycler);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        Lol.printNullity(this, "swipeRefreshLayout", swipeRefreshLayout);

        transitionManager = new ViewTransitionManager(new HashMapBuilder<String, List<View>>()
                .put(States.NO_ELEMENTS.name(), Arrays.asList(
                        rootView.findViewById(R.id.no_macros_text), fab))
                .put(States.NO_CONNECTION.name(), Collections.singletonList(
                        rootView.findViewById(R.id.no_connection_layout)))
                .put(States.LIST.name(), Arrays.asList(
                        macroListRecycler, fab))
                .put(States.PROGRESS.name(), Collections.singletonList(
                        rootView.findViewById(R.id.macro_list_progress)))
                .create());


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        macroListRecycler.setLayoutManager(layoutManager);
        macroListRecycler.setHasFixedSize(true);

        macroList = new ArrayListAdapter<MacroInterface>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {


                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.macro_manager_element, parent, false);

                final TextView macroTitle = (TextView) v.findViewById(R.id.macro_element_title);
                macroTitle.setSelected(true);
                final CircularImageView circularImageView = (CircularImageView) v.findViewById(R.id.macro_element_icon);
                final SwipeLayout sl = (SwipeLayout) v.findViewById(R.id.macro_element_swipe_layout);
                final ImageButton button = (ImageButton) v.findViewById(R.id.show_more_macro_actions_button);
                final Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.button_rotate);
                rotation.setRepeatCount(Animation.INFINITE);
                View.OnClickListener openCloseListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(sl.getOpenStatus() == SwipeLayout.Status.Open)
                            sl.close(true);
                        else if(sl.getOpenStatus() == SwipeLayout.Status.Close)
                            sl.open(true);
                    }
                };

                button.setOnClickListener(openCloseListener);
                circularImageView.setOnClickListener(openCloseListener);

                LinearLayout bottomWrapper = (LinearLayout)v.findViewById(R.id.bottom_wrapper);
                sl.setShowMode(SwipeLayout.ShowMode.PullOut);
                final SwipeActionLayoutManager salm = new SwipeActionLayoutManager(
                        getActivity(), bottomWrapper);
                sl.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onStartOpen(SwipeLayout swipeLayout) {
                        button.startAnimation(rotation);
                        setIsElementSwipingHorizontally(true);
                    }

                    @Override
                    public void onOpen(SwipeLayout swipeLayout) {
                        //setIsElementSwipingHorizontally(true);
                    }

                    @Override
                    public void onStartClose(SwipeLayout swipeLayout) {
                        setIsElementSwipingHorizontally(true);
                    }

                    @Override
                    public void onClose(SwipeLayout swipeLayout) {
                        setIsElementSwipingHorizontally(false);
                        button.clearAnimation();
                    }

                    @Override
                    public void onUpdate(SwipeLayout swipeLayout, int leftOffset, int topOffset) {
                        float alpha = Math.abs((float) leftOffset / (float) swipeLayout.getBottomView().getWidth());

                        salm.setAlpha(alpha);

                        float oppositeAlpha = 1.0f - alpha;
                        macroTitle.setAlpha(oppositeAlpha);
                        //circularImageView.setAlpha(oppositeAlpha);


                    }

                    @Override
                    public void onHandRelease(SwipeLayout swipeLayout, float v, float v2) {
                        setIsElementSwipingHorizontally(false);
                    }
                });

                bottomWrapper.setMinimumWidth(v.getWidth() - (circularImageView.getWidth() + button.getWidth()));

                return v;
            }

            @Override
            public void onBindViewHolder(final ItemViewHolder viewHolder, final MacroInterface elem, final int position) {


                View v = viewHolder.itemView;

                Lol.d(ArrayListAdapter.class, elem.getName() + " just binded to a viewHolder");
                TextView title = ((TextView) v.findViewById(R.id.macro_element_title));

                if(!elem.isValid()) {
                    String text = "<font color=" + GraphicsUtils.getStringHexOfColor(getResources().getColor(R.color.error_red)) +">";
                    text += elem.getName() + "</font>";
                    title.setText(Html.fromHtml(text)); //TODO: this seems to not work
                }else{
                    title.setText(elem.getName());
                }
                
                //TODO: set description and other data

                final SwipeLayout sl = (SwipeLayout) v.findViewById(R.id.macro_element_swipe_layout);
                LinearLayout ll = (LinearLayout) v.findViewById(R.id.bottom_wrapper);
                SwipeActionLayoutManager salm = new SwipeActionLayoutManager(getActivity(), ll);
                salm.clear();

                salm.addSwipeAction(new SmartSwipeActionButton(
                        elem,
                        getActivity(),
                        "Delete Macro",
                        getResources().getDrawable(R.drawable.ic_action_trash),
                        getResources().getColor(R.color.red_700),
                        getResources().getColor(R.color.red_900)) {
                    @Override
                    public void onDo(View v, Object o) {
                        DialogUtils.showYesNoDialog(
                                getActivity(),
                                context.getString(R.string.delete),
                                String.format(context.getString(R.string.are_you_sure_delete_macro), elem.getName()),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO: delete from local db, and web service if connected
                                        updateContent();
                                        dialog.dismiss();
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        sl.close();
                    }
                });

                salm.addSwipeAction(new SmartSwipeActionButton(
                        elem,
                        getActivity(),
                        "Edit Macro",
                        getResources().getDrawable(R.drawable.ic_action_edit),
                        getResources().getColor(R.color.sinapsi_blue),
                        getResources().getColor(R.color.sinapsi_blue_dark)) {
                    @Override
                    public void onDo(View v, Object o) {
                        //TODO: start editor passing the macro
                        sl.close();
                    }
                });

                ImageButton closeContextImageButton = new ImageButton(getActivity());
                closeContextImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_cancel_dark));
                closeContextImageButton.setBackgroundColor(getResources().getColor(R.color.full_transparent));
                closeContextImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sl.close();
                    }
                });
                salm.addCustomView(closeContextImageButton);

                CircularImageView ciw = (CircularImageView) v.findViewById(R.id.macro_element_icon);

                ciw.setImageDrawable(GraphicsUtils.generateMacroIcon(elem, v.getContext()));

                ciw.setBorderWidth(1);
                ciw.setBorderColor(v.getResources().getColor(R.color.cardview_light_background));


            }
        };


        macroListRecycler.setAdapter(new ScaleInAnimationAdapter( new AlphaInAnimationAdapter(macroList)));
        /*macroListRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getActivity()) {
            @Override
            public void onItemClick(View view, int position) {
                SwipeLayout sl = (SwipeLayout)view.findViewById(R.id.macro_element_swipe_layout);
                if(sl.getOpenStatus() == SwipeLayout.Status.Open)
                    sl.close(true);
                else if(sl.getOpenStatus() == SwipeLayout.Status.Close)
                    sl.open(true);
            }
        });*/


        macroListRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean enable = false;
                if (recyclerView != null && recyclerView.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = recyclerView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                setIsListOnTop(enable);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMacro();
            }
        });

        FloatingActionButton retryButton = (FloatingActionButton) rootView.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContent();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateContent();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.sinapsi_blue);
        transitionManager.makeTransitionIfDifferent(States.PROGRESS.name());
        updateContent();
        created = true;
        return rootView;
    }

    private void updateContent() {
        Lol.d(this, "Update content started");
        if(!isServiceConnected()) return;
        swipeRefreshLayout.setRefreshing(true);
        transitionManager.makeTransitionIfDifferent(States.PROGRESS.name());


        //TODO: handle server sync
        //updateMacroList(service.getMacros());
        //TODO: remove the lines below and decomment the line above, this is just for test
        MacroInterface mi1 = new FactoryModel().newMacro("Stupid Macro", 1);
        MacroInterface mi2 = new FactoryModel().newMacro("Macro With SMS Icon", 2);
        MacroInterface mi3 = new FactoryModel().newMacro("Invalid Macro", 3);
        MacroInterface mi4 = new FactoryModel().newMacro("Macro with very very very long name", 4);

        mi1.setMacroColor("#66BB33");
        mi2.setMacroColor("#CC9922");
        mi3.setMacroColor("#3366CC");
        mi4.setMacroColor("#3366CC");

        mi2.setIconName("ic_macro_sms");

        mi3.setValid(false);

        updateMacroList(Arrays.asList(
                mi1,
                mi2,
                mi3,
                mi4,
                mi1,
                mi2,
                mi3,
                mi4,
                mi1,
                mi2,
                mi3,
                mi4
        ));

        Lol.d(this, "Macro showed: " + macroList.getItemCount());

        swipeRefreshLayout.setRefreshing(false);
        transitionManager.makeTransitionIfDifferent(States.LIST.name());
        Lol.d(this, "Update content finished");
    }

    private void newMacro() {
        Lol.d(this, "newMacro called");
        //TODO: easier way to call from SinapsiFragments
        //TODO: return temp parameter mechanism
        Intent i = ((MainActivity) getActivity()).generateParameterizedIntent(EditorActivity.class, "Parameter passing across activities test passed");
        startActivity(i);
    }

    @Override
    public void onOnlineMode() {
        //TODO: impl
    }

    @Override
    public void onOfflineMode() {
        //TODO: impl
    }

    public void removeMacro(MacroInterface elem, int position){


        //TODO: remove from db
    }

    private void updateMacroList(List<MacroInterface> ml) {
        macroList.clear();
        macroList.addAll(ml);
    }

    @Override
    public String getName(Context context) {
        return context.getString(R.string.macro_manager_fragment_title);
    }

    @Override
    public void onServiceConnected(SinapsiBackgroundService service) {
        super.onServiceConnected(service);
        service.addWebServiceConnectionListener(this);

        //updates on service connected only if this is visible to the user
        if(created)updateContent();
    }

    public void setIsListOnTop(boolean b){
        isListOnTop = b;
        swipeRefreshLayout.setEnabled(isListOnTop && !isElementSwipingHorizontally);
    }

    public void setIsElementSwipingHorizontally(boolean b){
        isElementSwipingHorizontally = b;
        swipeRefreshLayout.setEnabled(isListOnTop && !isElementSwipingHorizontally);
    }
}
