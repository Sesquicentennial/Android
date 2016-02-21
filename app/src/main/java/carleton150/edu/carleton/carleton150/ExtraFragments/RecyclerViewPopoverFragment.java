package carleton150.edu.carleton.carleton150.ExtraFragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import carleton150.edu.carleton.carleton150.Adapters.HistoryAdapter;
import carleton150.edu.carleton.carleton150.Adapters.MyScaleInAnimationAdapter;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.MainFragments.HistoryFragment;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.MemoriesContent;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.POJO.Quests.Waypoint;
import carleton150.edu.carleton.carleton150.R;

/**
 * Class to manage a RecyclerViewPopoverFragment. This is used to show the popover for memories,
 * history, and quest progress
 */
public class RecyclerViewPopoverFragment extends Fragment{

    private View view;
    private RecyclerView historyInfoObjects;
    private LinearLayoutManager historyLayoutManager;
    private HistoryAdapter historyAdapter;
    private Button btnClose;
    private static boolean isMemories = false;
    private int screenWidth;
    private int screenHeight;
    private TextView txtErrorGettingMemories;
    private double MEMORIES_RADIUS = 0.1;
    private static HistoryFragment parentFragment;
    private static Fragment parentQuestFragment;
    private static boolean isQuestInProgress = false;

    private static GeofenceInfoContent[] geofenceInfoObject;
    private static Quest quest;
    private static int progressThroughQuest;

    public RecyclerViewPopoverFragment()
    {
        //required empty public constructor
    }

    /**
     * Creates a new instance of the RecyclerViewPopoverFragment where the GeofenceInfoContent[]
     * to display in the RecyclerView is provided. This is called when creating a history popover
     *
     * @param object the GeofenceInfoContent[] for the RecyclerViewPopoverFragment to display
     * @return the RecyclerViewPopoverFragment that was created
     */
    public static RecyclerViewPopoverFragment newInstance(GeofenceInfoContent[] object) {
        RecyclerViewPopoverFragment f = new RecyclerViewPopoverFragment();
        geofenceInfoObject = object;
        isMemories = false;
        isQuestInProgress = false;
        return f;
    }

    /**
     * Creates a new instance of the RecyclerViewPopoverFragment where the array to be displayed
     * by the RecyclerView is not provided. This means that this was called to display memories,
     * and the memories to display must be requested by the VolleyRequester
     *
     * @param mParentFragment
     * @return the RecyclerViewPopoverFragment that was created
     */
    public static RecyclerViewPopoverFragment newInstance(HistoryFragment mParentFragment){
        RecyclerViewPopoverFragment f = new RecyclerViewPopoverFragment();
        isMemories = true;
        parentFragment = mParentFragment;
        isQuestInProgress = false;
        return f;
    }

    /**
     * Creates a new instance of the RecyclerViewPopoverFragment where the array to be displayed is
     * the quest waypoints that were completed by the user. This method is to be called by the
     * QuestInProgress fragment or the QuestCompletedFragment
     *
     * @param mParentQuestFragment QuestInProgress fragment or QuestCompletedFragment
     * @param mQuest the user's current quest
     * @param mProgress the user's progress through the quest
     * @return the RecyclerViewPopoverFragment that was created
     */
    public static RecyclerViewPopoverFragment newInstance(Fragment mParentQuestFragment, Quest mQuest, int mProgress){
        RecyclerViewPopoverFragment f = new RecyclerViewPopoverFragment();
        isMemories = false;
        quest = mQuest;
        progressThroughQuest = mProgress;
        parentQuestFragment = mParentQuestFragment;
        isQuestInProgress = true;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Manages view
        view = getActivity().getLayoutInflater().
                inflate(R.layout.fragment_history_popover, new LinearLayout(getActivity()), false);
        TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtErrorGettingMemories = (TextView) view.findViewById(R.id.txt_error_getting_memories);
        btnClose = (Button) view.findViewById(R.id.btn_exit_popup);

        //Sets a listener for the "Add Memory" button
        Button btnAddMemory = (Button) view.findViewById(R.id.btn_add_memory);
        if(isMemories){
            /*
            If the RecyclerViewPopoverFragment is being used to display memories, makes the "Add Memory" button
            visible and listens for clicks. On a click, launches the AddMemoriesFragment
             */
            btnAddMemory.setVisibility(View.VISIBLE);
            btnAddMemory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddMemoriesFragment();
                }
            });
        }else{
            //If not being used to display memories, sets visibility of button to add memory to "GONE"
            btnAddMemory.setVisibility(View.GONE);
        }

        //Closes the RecyclerViewPopoverFragment when the close button is pressed
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCurrentFragment();
            }
        });

        if(!isMemories && !isQuestInProgress) {
            //If this is being used to show the history, sets the title to the name of the geofence
            String name = null;
            int i = 0;
            while (name == null && i < geofenceInfoObject.length) {
                if (geofenceInfoObject[i].getName() != null) {
                    name = geofenceInfoObject[i].getName();
                }
                i++;
            }
            txtTitle.setText(name);
        }
        else if (isMemories){
            txtTitle.setText(getString(R.string.nearby_memories_title));
            txtErrorGettingMemories.setVisibility(View.VISIBLE);
            txtErrorGettingMemories.setText(R.string.getting_nearby_memories);
        }else if(isQuestInProgress){
            txtTitle.setText(getString(R.string.progress_through_quest_title));
            txtErrorGettingMemories.setVisibility(View.GONE);
        }

        if(isMemories){
            setMemoriesColorScheme();
        }

        //builds RecyclerViews to display info
        buildRecyclerViews();

        return view;
    }

    /**
     * Removes this fragment from the view
     */
    private void removeCurrentFragment(){
        if(historyAdapter != null) {
            historyAdapter.closeAdapter();
        }
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        fm.detach(this).remove(this).commit();

    }

    /**
     * Sets the color scheme. This method is only called if the RecyclerViewPopoverFragment
     * is being used to display memories.
     */
    private void setMemoriesColorScheme(){
        View line = view.findViewById(R.id.view_line);
        RelativeLayout relLayoutHistPopoverBackground = (RelativeLayout) view.findViewById(R.id.rel_layout_history_popover_background);
        relLayoutHistPopoverBackground.setBackgroundColor(getResources().getColor(R.color.windowBackground));
        line.setVisibility(View.GONE);

    }

    /**
     * Determines what the fragment is being used for and builds the appropriate RecyclerView
     */
    private void buildRecyclerViews(){
            historyInfoObjects = (RecyclerView) view.findViewById(R.id.lst_history_items);
            historyLayoutManager = new LinearLayoutManager(getActivity());
            historyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            historyInfoObjects.setLayoutManager(historyLayoutManager);
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;

            if(!isMemories && !isQuestInProgress) {
                buildHistoryRecyclerView();
            }else if (isMemories){
               buildMemoriesRecyclerView();
            }else if (isQuestInProgress){
                buildQuestProgressRecyclerView();
            }
    }

    /**
     * Builds a RecyclerView when the RecyclerViewPopoverFragment is being used to display
     * history info
     */
    private void buildHistoryRecyclerView(){
        historyAdapter = new HistoryAdapter(getActivity(), geofenceInfoObject, null, screenWidth,
                screenHeight, isMemories, isQuestInProgress);
        //RecyclerView animation
        MyScaleInAnimationAdapter scaleInAnimationAdapter = new MyScaleInAnimationAdapter(historyAdapter);
        scaleInAnimationAdapter.setFirstOnly(false);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
        historyInfoObjects.setAdapter(scaleInAnimationAdapter);
    }

    /**
     * Builds a RecyclerView when the RecyclerViewPopoverFragment is being used to display
     * memories
     */
    private void buildMemoriesRecyclerView(){
        VolleyRequester volleyRequester = new VolleyRequester();
        MainActivity activity = (MainActivity) getActivity();
        Location location = activity.getLastLocation();

        //Need a location to request memories. If location not available, notifies user
        if(location != null) {
            volleyRequester.requestMemories(location.getLatitude(), location.getLongitude(),
                    MEMORIES_RADIUS, this);
        }else{
            txtErrorGettingMemories.setVisibility(View.VISIBLE);
            txtErrorGettingMemories.setText(getString(R.string.no_current_location));
        }
    }

    /**
     * Builds a RecyclerView when the RecyclerViewPopoverFragment is being used to display
     * quest progress
     */
    private void buildQuestProgressRecyclerView(){
        Waypoint[] waypoints = quest.getWaypoints();
        Waypoint[] completedWaypoints = new Waypoint[progressThroughQuest];
        for(int i = 0; i<progressThroughQuest; i++){
            completedWaypoints[i] = waypoints[i];
        }
        historyAdapter = new HistoryAdapter(getActivity(), null, completedWaypoints, screenWidth, screenHeight, false, true);
        MyScaleInAnimationAdapter scaleInAnimationAdapter = new MyScaleInAnimationAdapter(historyAdapter);
        scaleInAnimationAdapter.setFirstOnly(false);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
        historyInfoObjects.setAdapter(scaleInAnimationAdapter);
        historyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
        historyInfoObjects = null;
        historyLayoutManager = null;
        historyAdapter = null;
        btnClose = null;
        geofenceInfoObject = null;
        txtErrorGettingMemories = null;
        parentFragment = null;

    }

    /**
     * Called from VolleyRequester. Creates a HistoryAdapter for the new memories and displays
     * them in the RecyclerView
     *
     * @param memories memories retrieved from server
     */
    public void handleNewMemories(MemoriesContent memories){
        if(memories != null) {
            if(memories.getContent().length == 0){
                txtErrorGettingMemories.setVisibility(View.VISIBLE);
                txtErrorGettingMemories.setText(getString(R.string.no_nearby_memories));
            }else{
                txtErrorGettingMemories.setVisibility(View.GONE);
            }
            historyAdapter = new HistoryAdapter(getActivity(), memories.getContent(), null, screenWidth,
                    screenHeight, isMemories, isQuestInProgress);
            //RecyclerView animation
            MyScaleInAnimationAdapter scaleInAnimationAdapter = new MyScaleInAnimationAdapter(historyAdapter);
            scaleInAnimationAdapter.setFirstOnly(false);
            scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
            historyInfoObjects.setAdapter(scaleInAnimationAdapter);
            historyAdapter.notifyDataSetChanged();
        }else{
            txtErrorGettingMemories.setVisibility(View.VISIBLE);
            txtErrorGettingMemories.setText(getString(R.string.memories_null));
        }
    }

    /**
     * Calles a method in the parentFragment to show the popover to add a memory
     */
    private void showAddMemoriesFragment(){
        parentFragment.showAddMemoriesPopover();
        }
}
