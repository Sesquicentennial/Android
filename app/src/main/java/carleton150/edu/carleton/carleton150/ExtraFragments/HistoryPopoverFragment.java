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
import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.MainFragments.HistoryFragment;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.MemoriesContent;
import carleton150.edu.carleton.carleton150.R;

/**
 * Class to manage a HistoryPopoverDialogFragment. Currently fills in a text view
 * and a title from the geofenceInfoObject
 */
public class HistoryPopoverFragment extends Fragment implements RecyclerViewClickListener{

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

    private static GeofenceInfoContent[] geofenceInfoObject;
    public HistoryPopoverFragment()
    {
    }

    public static HistoryPopoverFragment newInstance(GeofenceInfoContent[] object) {
        HistoryPopoverFragment f = new HistoryPopoverFragment();
        geofenceInfoObject = object;
        isMemories = false;
        return f;
    }

    public static HistoryPopoverFragment newInstance(HistoryFragment mParentFragment){
        HistoryPopoverFragment f = new HistoryPopoverFragment();
        isMemories = true;
        parentFragment = mParentFragment;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = getActivity().getLayoutInflater().
                inflate(R.layout.fragment_history_popover, new LinearLayout(getActivity()), false);
        TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtErrorGettingMemories = (TextView) view.findViewById(R.id.txt_error_getting_memories);
        btnClose = (Button) view.findViewById(R.id.btn_exit_popup);
        Button btnAddMemory = (Button) view.findViewById(R.id.btn_add_memory);
        if(isMemories){
            btnAddMemory.setVisibility(View.VISIBLE);
            btnAddMemory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddMemoriesFragment();
                }
            });
        }else{
            btnAddMemory.setVisibility(View.GONE);
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCurrentFragment();
            }
        });

        if(!isMemories) {
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
        else{
            txtTitle.setText(getString(R.string.nearby_memories_title));
            txtErrorGettingMemories.setVisibility(View.VISIBLE);
            txtErrorGettingMemories.setText(R.string.getting_nearby_memories);
        }

        if(isMemories){
            setMemoriesColorScheme();
        }

        //builds RecyclerViews to display info
        buildRecyclerViews();
        //historyAdapter.updateHistoryList(historyInfo);
       // alphaInAnimationAdapter.notifyDataSetChanged();

        return view;
    }

    private void removeCurrentFragment(){
        if(historyAdapter != null) {
            historyAdapter.closeAdapter();
        }
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        fm.detach(this).remove(this).commit();

    }

    private void setMemoriesColorScheme(){
        View line = view.findViewById(R.id.view_line);
        RelativeLayout relLayoutHistPopoverBackground = (RelativeLayout) view.findViewById(R.id.rel_layout_history_popover_background);
        relLayoutHistPopoverBackground.setBackgroundColor(getResources().getColor(R.color.windowBackground));
        line.setVisibility(View.GONE);

    }

    /**
     * Builds the views for the historical info
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

            //if it is memories, we don't have data yet...
            if(!isMemories) {
                historyAdapter = new HistoryAdapter(getActivity(), geofenceInfoObject, screenWidth, screenHeight, isMemories);

                //RecyclerView animation
                MyScaleInAnimationAdapter scaleInAnimationAdapter = new MyScaleInAnimationAdapter(historyAdapter);
                scaleInAnimationAdapter.setFirstOnly(false);
                scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());

                historyInfoObjects.setAdapter(scaleInAnimationAdapter);
            }else{
                VolleyRequester volleyRequester = new VolleyRequester();
                MainActivity activity = (MainActivity) getActivity();
                Location location = activity.getLastLocation();
                if(location != null) {
                    volleyRequester.requestMemories(location.getLatitude(), location.getLongitude(), MEMORIES_RADIUS, this);
                }else{
                    txtErrorGettingMemories.setVisibility(View.VISIBLE);
                    txtErrorGettingMemories.setText(getString(R.string.no_current_location));
                }
            }

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {

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

    public void handleNewMemories(MemoriesContent memories){
        if(memories != null) {
            if(memories.getContent().length == 0){
                txtErrorGettingMemories.setVisibility(View.VISIBLE);
                txtErrorGettingMemories.setText(getString(R.string.no_nearby_memories));
            }else{
                txtErrorGettingMemories.setVisibility(View.GONE);
            }
            historyAdapter = new HistoryAdapter(getActivity(), memories.getContent(), screenWidth, screenHeight, isMemories);

            //RecyclerView animation
            MyScaleInAnimationAdapter scaleInAnimationAdapter = new MyScaleInAnimationAdapter(historyAdapter);
            scaleInAnimationAdapter.setFirstOnly(false);
            scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());

            historyInfoObjects.setAdapter(scaleInAnimationAdapter);
            historyAdapter.notifyDataSetChanged();
        }else{
            txtErrorGettingMemories.setVisibility(View.VISIBLE);
            txtErrorGettingMemories.setText(getString(R.string.memories_null));
            //TODO: add appropriate error handling to this...
        }
    }


    private void showAddMemoriesFragment(){

        parentFragment.showAddMemoriesPopover();

        }
}
