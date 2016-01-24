package carleton150.edu.carleton.carleton150.MainFragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.Adapters.QuestAdapter;
import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.Interfaces.FragmentChangeListener;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.R;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Class to display quests and allow a user to select a quest to view information about it
 * or start the quest
 */
public class QuestFragment extends MainFragment implements RecyclerViewClickListener {

    private static View view;
    private RecyclerView quests;
    private ArrayList<Quest> questInfo;
    private LinearLayoutManager questLayoutManager;
    private QuestAdapter questAdapter;
    private int screenWidth;
    private ScaleInAnimationAdapter scaleAdapter;

    private Button btnStartQuest;
    private TextView txtInfo;
    private Button btnTryAgain;
    private TextView txtTitle;


    public QuestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_quest, container, false);
        btnStartQuest = (Button) view.findViewById(R.id.btn_start_quest);
        txtInfo = (TextView) view.findViewById(R.id.txt_quest_description);
        txtTitle = (TextView) view.findViewById(R.id.txt_quest_title);


        /*Button for user to try getting quests again if the app was unable
        to get them from the server
         */
        btnTryAgain = (Button) view.findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTryAgain.setVisibility(View.GONE);
                txtInfo.setText(getString(R.string.retrieving_quests));
                fragmentInView();
            }
        });

        //builds RecyclerViews to display quests
        buildRecyclerViews();
        //requests quests from server
        fragmentInView();
        return view;
    }

    /**
     * Handles when a quest was clicked by making the "Start Quest" button clickable
     * and displaying info about the selected quest
     * @param v
     * @param position
     */
    @Override
    public void recyclerViewListClicked(View v, final int position) {
        if(isVisible) {
            Log.i("View", "QuestFragment : recyclerViewListClicked");
            btnStartQuest.setClickable(true);
            btnStartQuest.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnStartQuest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginQuest(questAdapter.getQuestList().get(position));
                }
            });

            txtInfo.setText(questAdapter.getQuestList().get(position).getDesc());
            txtTitle.setText(questAdapter.getQuestList().get(position).getName());
            txtTitle.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Begins a quest by creating a new QuestInProgressFragment and passing
     * it info about the selected quest. Uses the FragmentChangeListener
     * interface to communicate with the MainActivity
     * @param quest quest to begin
     */
    private void beginQuest(Quest quest){
        QuestInProgressFragment fr=new QuestInProgressFragment();
        fr.initialize(quest);
        FragmentChangeListener fc=(FragmentChangeListener)getActivity();
        fc.replaceFragment(fr);
    }

    /**
     * Builds the views for the quests
     */
    private void buildRecyclerViews(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        quests = (RecyclerView) view.findViewById(R.id.lst_quests);
        questLayoutManager = new LinearLayoutManager(getActivity());
        questLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        quests.setLayoutManager(questLayoutManager);


        questAdapter = new QuestAdapter(questInfo, this, screenWidth);

        //RecyclerView animation
        scaleAdapter = new ScaleInAnimationAdapter(questAdapter);
        scaleAdapter.setFirstOnly(false);
        scaleAdapter.setDuration(200);

        quests.setAdapter(scaleAdapter);
    }

    /**
     * Called by VolleyRequester, handles new quests from the server
     * @param newQuests
     */
    @Override
    public void handleNewQuests(ArrayList<Quest> newQuests) {
        /*This is a call from the VolleyRequester, so this check prevents the app from
        crashing if the user leaves the tab while the app is trying
        to get quests from the server
         */
        if(this.isDetached()){
            return;
        }
        super.handleNewQuests(newQuests);
        if(newQuests != null) {
            txtInfo.setText(getString(R.string.select_quest_to_view_info));
            btnTryAgain.setVisibility(View.GONE);

            questInfo = newQuests;

            //TODO:remove
            questInfo.addAll(newQuests);
            questInfo.addAll(newQuests);
            questInfo.addAll(newQuests);
            questInfo.addAll(newQuests);
            questAdapter.updateQuests(questInfo);
            scaleAdapter.notifyDataSetChanged();
            Log.i(logMessages.VOLLEY, "QuestFragment: handleNewQuests : questAdapter contains : " + questAdapter.getItemCount());
        } else {
            if(questInfo == null) {
                txtInfo.setText(getString(R.string.no_quests_retrieved));
                btnTryAgain.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void fragmentOutOfView() {
        super.fragmentOutOfView();
    }

    /**
     * When the fragment comes into view, requests quests from the server
     */
    @Override
    public void fragmentInView() {
        super.fragmentInView();
        if(questInfo != null){
            txtTitle.setVisibility(View.INVISIBLE);
            txtInfo.setText(getString(R.string.select_quest_to_view_info));
        }
        volleyRequester.requestQuests(this);
    }
}
