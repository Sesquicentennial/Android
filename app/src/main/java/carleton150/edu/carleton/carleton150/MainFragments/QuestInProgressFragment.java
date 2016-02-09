package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import carleton150.edu.carleton.carleton150.FlipAnimation;
import carleton150.edu.carleton.carleton150.Interfaces.FragmentChangeListener;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.Quests.Geofence;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestInProgressFragment extends MapMainFragment {


    private Quest quest = null;
    private int numClue = 0;
    private TextView txtClue;
    private Button btnFoundIt;
    private TextView txtHint;
    private TextView txtClueNumber;
    private ImageButton btnReturnToMyLocation;
    private Button btnFlipCardToHint;
    private Button btnFlipCardToClue;
    private RelativeLayout relLayoutQuestCompleted;
    private TextView txtQuestCompleted;
    private ImageView imgQuestCompleted;
    private Button btnDoneWithQuest;

    View rootLayout;
    View cardFace;
    View cardBack;


    private SupportMapFragment mapFragment;


    public QuestInProgressFragment() {
        // Required empty public constructor
    }

    /**
     * This must be called after creating the QuestInProgressFragment in order to pass
     * it the current quest
     * @param quest quest to be completed by user
     */
    public void initialize(Quest quest){
        this.quest = quest;
    }


    /**
     * Sets OnClickListeners to register when hint button or found it button is clicked
     * and to show the hint or check if the user is within a valid radius of the waypoint
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quest_in_progress, container, false);

        txtClue = (TextView) v.findViewById(R.id.txt_clue);
        btnFoundIt = (Button) v.findViewById(R.id.btn_found_location);
        txtHint = (TextView) v.findViewById(R.id.txt_hint);
        txtClueNumber = (TextView) v.findViewById(R.id.txt_clue_number);
        btnReturnToMyLocation = (ImageButton) v.findViewById(R.id.btn_return_to_my_location);
        rootLayout = v.findViewById(R.id.lin_layout_card_root);
        cardFace = v.findViewById(R.id.clue_view_front);
        cardBack = v.findViewById(R.id.clue_view_back);
        btnFlipCardToClue = (Button) v.findViewById(R.id.btn_show_clue);
        btnFlipCardToHint = (Button) v.findViewById(R.id.btn_show_hint);
        relLayoutQuestCompleted = (RelativeLayout) v.findViewById(R.id.rel_layout_quest_completed);
        txtQuestCompleted = (TextView) v.findViewById(R.id.txt_completion_message);
        imgQuestCompleted = (ImageView) v.findViewById(R.id.img_animation_quest_completed);
        btnDoneWithQuest = (Button) v.findViewById(R.id.btn_done_with_quest);

        btnFlipCardToHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });

        btnFlipCardToClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });


        String hint = quest.getWaypoints().get(String.valueOf(numClue)).getHint();
        if(hint.equals("")){
            txtHint.setText(getResources().getString(R.string.no_hint_available));
        }else {
            txtHint.setText(quest.getWaypoints().get(String.valueOf(numClue)).getHint());
        }

        btnReturnToMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnZoomToUserLocation();
            }
        });

        /*btnShowHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quest.getWaypoints().get(String.valueOf(numClue)) != null) {
                    btnShowHint.setVisibility(View.GONE);
                    String hint = quest.getWaypoints().get(String.valueOf(numClue)).getHint();
                    if(hint.equals("")){
                        txtHint.setText(getResources().getString(R.string.no_hint_available));
                    }else {
                        txtHint.setText(quest.getWaypoints().get(String.valueOf(numClue)).getHint());
                    }
                //If quest is completed, sets the hint to blank
                }else{
                    txtHint.setText("");
                }
                txtHint.setVisibility(View.VISIBLE);
            }
        });*/

        btnFoundIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfClueFound();
            }
        });
        updateCurrentWaypoint();
        return v;
    }

    /**
     * zooms to the user's current location
     */
    private void returnZoomToUserLocation(){
        zoomCamera = true;
        setCamera();
    }

    /**
     * replaces the RelativeLayout named my_map with a SupportMapFragment
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
         mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.my_map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.my_map, mapFragment).commit();
        }

    }

    /**
     * Lifecycle method overridden to set up the map if it
     * is currently null
     */
    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if(mainActivity.mLastLocation != null){
            drawLocationMarker(mainActivity.mLastLocation);
        }
    }

    /**
     * Checks if the user's current location is within the radius of the waypoint
     * (both the radius and waypoint are specified in the quest object)
     */
    private void checkIfClueFound(){
        Location curLocation = mainActivity.mLastLocation;
        if(curLocation != null) {
            Geofence hintGeofence = quest.getWaypoints().get(String.valueOf(numClue)).getGeofence();
            double lat = Double.valueOf(hintGeofence.getLat());
            double lon = Double.valueOf(hintGeofence.getLng());
            double rad = Double.valueOf(hintGeofence.getRad());
            float[] results = new float[1];

            Location.distanceBetween(curLocation.getLatitude(), curLocation.getLongitude(),
                    lat, lon,
                    results);
            if (results[0] <= rad) {
                clueCompleted();
            } else {
                //String to display if hint is not already showing
                String alertString = getActivity().getResources().getString(R.string.location_not_found_hint);
                if (txtHint.getVisibility() == View.VISIBLE) {
                    //String to display if hint is already showing
                    alertString = getActivity().getResources().getString(R.string.location_not_found);
                }
                mainActivity.showAlertDialog(alertString,
                        new AlertDialog.Builder(mainActivity).create());
            }
        }else{
            Log.i(logMessages.LOCATION, "QuestInProgressFragment: checkIfClueFound: location is null");
            //TODO: this shouln't happen. Handle it better...
        }
    }


    /**
     * Sets up the map
     * Monitors the zoom and target of the camera and changes them
     * if the user zooms out too much or scrolls map too far off campus.
     */
    @Override
    protected void setUpMap() {

        super.setUpMap();
        // to get rid of blue dot showing user's location
        mMap.setMyLocationEnabled(false);
        }


    /**
     * Updates map view to reflect user's new location
     *
     * @param newLocation
     */
    @Override
    public void handleLocationChange(Location newLocation) {
        super.handleLocationChange(newLocation);
        setCamera();
        drawLocationMarker(newLocation);

        if(mainActivity.getGeofenceMonitor().currentLocation != null) {
            setUpMapIfNeeded();
        }
    }

    /**
     * draws a custom location marker for the user's current location
     * @param newLocation
     */
    private void drawLocationMarker(Location newLocation) {
        if(mMap != null) {
            Log.i(logMessages.LOCATION, "QuestInProgressFragment : drawLocationMarker : mMap is not null");
            mMap.clear();
            Bitmap knightIcon = BitmapFactory.decodeResource(getResources(), R.drawable.knight_horse_icon);
            LatLng position = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(knightIcon);
            Marker curLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Current Location")
                    .icon(icon));
        }else{
            Log.i(logMessages.LOCATION, "QuestInProgressFragment : drawLocationMarker : mMap is null");
        }
    }



    /**
     * Checks if the quest is finished. If not, sets the text to show the next clue
     *
     * @return boolean, true if quest is finished, false otherwise
     */
    public boolean updateCurrentWaypoint(){
        String currentClue = String.valueOf(numClue);
        boolean finished = false;
        try {
            if (quest.getWaypoints().get(currentClue) == null &&
                    quest.getWaypoints().get(String.valueOf(numClue - 1)) != null) {
                finished = true;
                return finished;
            }
            txtClue.setText(quest.getWaypoints().get(currentClue).getClue());
            txtClueNumber.setText((numClue + 1) + "/" + quest.getWaypoints().size());
            return finished;
        } catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Handles when a clue has been completed by incrementing the clue
     * number, updating the current waypoint, and checking if the quest is completed
     */
    public void clueCompleted() {
        Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: clueCompleted");
        numClue += 1;
        boolean completedQuest = updateCurrentWaypoint();
        if (completedQuest){

            showCompletedQuestMessage();
        }
    }

    /**
     * Shows the message stored with the quest when the quest has been
     * completed
     */
    private void showCompletedQuestMessage(){
        relLayoutQuestCompleted.setVisibility(View.VISIBLE);
        txtQuestCompleted.setText("Message is : " + quest.getCompMsg());
        txtQuestCompleted.setMovementMethod(new ScrollingMovementMethod());

        ((AnimationDrawable) imgQuestCompleted.getBackground()).start();
        btnDoneWithQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToQuestSelectionScreen();
            }
        });
    }

    /**
     * Called when the fragment comes into view (different than onResume() because
     * the viewPager keeps several fragments in resumed state. This method is called
     * when the fragment actually comes into view on the screen
     *
     * updates the waypoints,
     * and sets the map camera if necessary
     */
    @Override
    public void fragmentInView() {
        Log.i(logMessages.LOCATION, "QuestInProgressFragment : fragmentInView : called");
        updateCurrentWaypoint();
        if(mainActivity.mLastLocation != null){
            Log.i(logMessages.LOCATION, "QuestInProgressFragment : fragmentInView : last location not null, drawing marker");
            drawLocationMarker(mainActivity.mLastLocation);
        }
        setCamera();
    }



    /**
     * Map should be set to null in onDestroyView(), but then there is an error
     * because the FragmentManager has already called onSaveInstanceState, so variables
     * can no longer be changed. Therefore, it is necessary to make mMap = null before
     * saving the instance state.
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMap = null;
        super.onSaveInstanceState(outState);
    }

    private void goBackToQuestSelectionScreen(){
        QuestFragment fr=new QuestFragment();
        FragmentChangeListener fc=(FragmentChangeListener)getActivity();
        fc.replaceFragment(fr);
    }

    private void flipCard()
    {

        FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

        if (cardFace.getVisibility() == View.GONE)
        {
            flipAnimation.reverse();
        }
        rootLayout.startAnimation(flipAnimation);
    }
}
