package carleton150.edu.carleton.carleton150.ExtraFragments;


import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import carleton150.edu.carleton.carleton150.Interfaces.FragmentChangeListener;
import carleton150.edu.carleton.carleton150.MainFragments.MainFragment;
import carleton150.edu.carleton.carleton150.MainFragments.QuestFragment;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestCompletedFragment extends MainFragment {

    private Quest quest;
    private View v;

    public QuestCompletedFragment() {
        // Required empty public constructor
    }

    public void initialize(Quest quest){
        this.quest = quest;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.gc();
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_quest_completed, container, false);

        ImageView imgQuestCompletedAnim = (ImageView) v.findViewById(R.id.img_animation_quest_completed);
        Button btnDone = (Button) v.findViewById(R.id.btn_done_with_quest);
        TextView txtNumCompleted = (TextView) v.findViewById(R.id.txt_clue_number_comp_window);
        TextView txtCompMsg = (TextView) v.findViewById(R.id.txt_completion_message);
        RelativeLayout relLayoutQuestCompleted = (RelativeLayout) v.findViewById(R.id.rel_layout_quest_completed);

        txtCompMsg.setText(quest.getCompMsg());
        txtNumCompleted.setText(quest.getWaypoints().length + "/" + quest.getWaypoints().length);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                imgQuestCompletedAnim.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.anim_quest_completed));
                ((AnimationDrawable) imgQuestCompletedAnim.getBackground()).start();

            } catch (OutOfMemoryError e) {
                imgQuestCompletedAnim.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.qanim25));
            }
        }else{
            imgQuestCompletedAnim.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.qanim25));
        }

        imgQuestCompletedAnim.setVisibility(View.VISIBLE);
        relLayoutQuestCompleted.setVisibility(View.VISIBLE);

        txtNumCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressPopup();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToQuestSelectionScreen();
            }
        });

        return v;
    }


    /**
     * Shows the history popover for a given marker on the map
     *
     * @param
     */
    private void showProgressPopup(){

        ImageView imgQuestCompleted = (ImageView) v.findViewById(R.id.img_animation_quest_completed);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imgQuestCompleted.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        }
        imgQuestCompleted.setImageDrawable(getResources().getDrawable(R.drawable.qanim25));

        System.gc();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        RecyclerViewPopoverFragment recyclerViewPopoverFragment = RecyclerViewPopoverFragment.newInstance(this, quest, quest.getWaypoints().length);

        // Transaction start
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom,
                R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        fragmentTransaction.add(R.id.fragment_container, recyclerViewPopoverFragment, "QuestProgressPopoverFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void goBackToQuestSelectionScreen(){
        System.gc();
        QuestFragment fr=new QuestFragment();
        FragmentChangeListener fc=(FragmentChangeListener)getActivity();
        fc.replaceFragment(fr);
    }


    @Override
    public void fragmentOutOfView() {
        super.fragmentOutOfView();
        ImageView imgQuestCompleted = (ImageView) v.findViewById(R.id.img_animation_quest_completed);
        imgQuestCompleted.setImageDrawable(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imgQuestCompleted.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        }
    }

    @Override
    public void onDestroyView() {
        ImageView imgQuestCompleted = (ImageView) v.findViewById(R.id.img_animation_quest_completed);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imgQuestCompleted.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_transparent));
            System.gc();
        }else{
            imgQuestCompleted.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_transparent));
        }
        imgQuestCompleted = null;
        super.onDestroyView();
    }
}
