package carleton150.edu.carleton.carleton150.MainFragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.Adapters.QuestAdapter;
import carleton150.edu.carleton.carleton150.Interfaces.FragmentChangeListener;
import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.R;

/**
 * Class to display quests and allow a user to select a quest to view information about it
 * or start the quest
 */
public class QuestFragment extends MainFragment implements RecyclerViewClickListener {

    private ArrayList<Quest> questInfo;
    private LinearLayoutManager questLayoutManager;
    private QuestAdapter questAdapter;
    private int screenWidth;
    private View view;
    private boolean requestQuests = true;


    public QuestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final TextView txtInfo;
        final Button btnTryAgain;
        view =  inflater.inflate(R.layout.fragment_quest, container, false);


        /*Button for user to try getting quests again if the app was unable
        to get them from the server
         */
        btnTryAgain = (Button) view.findViewById(R.id.btn_try_getting_quests);
        txtInfo = (TextView) view.findViewById(R.id.txt_request_quests);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTryAgain.setVisibility(View.GONE);
                txtInfo.setText(getString(R.string.retrieving_quests));
                fragmentInView();
            }
        });

        ImageView imgQuestion = (ImageView) view.findViewById(R.id.img_question);
        imgQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTutorial();
            }
        });

        //builds RecyclerViews to display quests
        buildRecyclerViews();
        fragmentInView();

        // Toggle tutorial if first time using app
        if (checkFirstQuestRun()) {
            toggleTutorial();
        }

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
        Log.i("View", "QuestFragment : recyclerViewListClicked");
        beginQuest(questAdapter.getQuestList().get(position));
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
        RecyclerViewPager quests = (RecyclerViewPager) view.findViewById(R.id.lst_quests);
        questLayoutManager = new LinearLayoutManager(getActivity());
        questLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        quests.setLayoutManager(questLayoutManager);
        MainActivity mainActivity = (MainActivity) getActivity();
        questAdapter = new QuestAdapter(questInfo, this, screenWidth, metrics.heightPixels,
                getResources(), mainActivity.getPersistentQuestStorage());
        quests.setAdapter(questAdapter);
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
        if(view != null) {
            try {
                RecyclerViewPager quests = (RecyclerViewPager) view.findViewById(R.id.lst_quests);
                TextView txtInfo = (TextView) view.findViewById(R.id.txt_request_quests);
                Button btnTryAgain = (Button) view.findViewById(R.id.btn_try_getting_quests);

                super.handleNewQuests(newQuests);
                if(newQuests == null){
                    requestQuests = true;

                    txtInfo.setText(getString(R.string.no_quests_retrieved));
                    btnTryAgain.setVisibility(View.VISIBLE);
                    if (quests != null) {
                        quests.setVisibility(View.GONE);
                    }
                    return;
                }
                else if (newQuests != null) {
                    requestQuests = false;
                    questInfo = newQuests;
                    questAdapter.updateQuestList(questInfo);
                    questAdapter.notifyDataSetChanged();
                    quests.setVisibility(View.VISIBLE);
                    Log.i(logMessages.VOLLEY, "QuestFragment: handleNewQuests : questAdapter contains : " + questAdapter.getItemCount());
                }
                    if (questInfo == null) {
                        requestQuests = true;
                        txtInfo.setText(getString(R.string.no_quests_retrieved));
                        btnTryAgain.setVisibility(View.VISIBLE);
                        if (quests != null) {
                            quests.setVisibility(View.GONE);
                        }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * When the fragment comes into view, requests quests from the server
     */
    @Override
    public void fragmentInView() {
        super.fragmentInView();
        Log.i("UI", "QuestFragment : fragmentInView");
        if(requestQuests) {
            volleyRequester.requestQuests(this);
            requestQuests = false;
        }
    }

    /**
     * Sets view items to null when view is destroyed to avoid
     * memory leaks
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
        requestQuests = true;
        questAdapter = null;
        questLayoutManager = null;
        questInfo = null;
    }

    /**
     * If the tutorial is in view, hides it. Otherwise, shows it
     */
    private void toggleTutorial(){
        final RelativeLayout relLayoutTutorial = (RelativeLayout) view.findViewById(R.id.tutorial);
        if(relLayoutTutorial.getVisibility() == View.VISIBLE){
            relLayoutTutorial.setVisibility(View.GONE);
        }else{
            relLayoutTutorial.setVisibility(View.VISIBLE);
        }
        Button btnCloseTutorial = (Button) view.findViewById(R.id.btn_close_tutorial);
        btnCloseTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relLayoutTutorial.setVisibility(View.GONE);
            }
        });
    }
}
