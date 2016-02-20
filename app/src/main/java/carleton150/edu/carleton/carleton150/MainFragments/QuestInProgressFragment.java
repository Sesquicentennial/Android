package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import carleton150.edu.carleton.carleton150.Interfaces.FragmentChangeListener;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.Models.BitmapWorkerTask;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.POJO.Quests.Waypoint;
import carleton150.edu.carleton.carleton150.R;

import static carleton150.edu.carleton.carleton150.R.id.rel_layout_found_it_hint;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestInProgressFragment extends MapMainFragment {


    private Quest quest = null;
    private int numClue = 0;
    private int screenWidth;
    private int screenHeight;
    private static final String QUEST_STARTED = "You already started this quest. " +
            "Would you like to Resume it or Start Over?";

    private View cardFace;
    private View cardBack;
    private View v;
    private boolean inView = false;


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
        v = inflater.inflate(R.layout.fragment_quest_in_progress, container, false);
        Button btnFoundIt = (Button) v.findViewById(R.id.btn_found_location);
        Button btnFoundItHint = (Button) v.findViewById(R.id.btn_found_location_hint);
        TextView txtHint = (TextView) v.findViewById(R.id.txt_hint);
        ImageButton btnReturnToMyLocation = (ImageButton) v.findViewById(R.id.btn_return_to_my_location);
        cardFace = v.findViewById(R.id.clue_view_front);
        cardBack = v.findViewById(R.id.clue_view_back);
        Button btnFlipCardToClue = (Button) v.findViewById(R.id.btn_show_clue);
        Button btnFlipCardToHint = (Button) v.findViewById(R.id.btn_show_hint);
        SlidingDrawer slidingDrawerClue = (SlidingDrawer) v.findViewById(R.id.front_drawer);
        SlidingDrawer slidingDrawerHint = (SlidingDrawer) v.findViewById(R.id.back_drawer);
        final ImageView imgExpandClue = (ImageView) v.findViewById(R.id.img_expand_clue);
        final ImageView imgExpandHint = (ImageView) v.findViewById(R.id.img_expand_hint);


        slidingDrawerClue.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                imgExpandClue.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_expand_more));
            }
        });
        slidingDrawerHint.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                imgExpandHint.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_expand_more));
            }
        });

        slidingDrawerClue.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                imgExpandClue.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_expand_less));
            }
        });
        slidingDrawerHint.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                imgExpandHint.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigation_expand_less));
            }
        });
        ImageView imgHint = (ImageView) v.findViewById(R.id.img_hint_image_back);
        ImageView imgClue = (ImageView) v.findViewById(R.id.img_clue_image_front);

        if(inView) {
            checkIfQuestStarted();
            Log.i("CHECK QUEST", "onCreateView, checking if quest is started");
        }


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

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

        Waypoint[] waypoints = quest.getWaypoints();
        if(numClue != waypoints.length) {
            String hint = waypoints[numClue].getHint().getText();

            String image = null;
            String hintImage = null;
            if (waypoints[numClue].getHint().getImage() != null) {
                hintImage = waypoints[numClue].getHint().getImage().getImage();
            }
            if (waypoints[numClue].getClue().getImage() != null) {
                image = waypoints[numClue].getClue().getImage().getImage();
            }


            if (hint == null || hint.equals("")) {
                txtHint.setText(getResources().getString(R.string.no_hint_available));
            } else {
                txtHint.setText(waypoints[numClue].getHint().getText());
            }

            RelativeLayout relLayoutFoundItHint = (RelativeLayout) v.findViewById(rel_layout_found_it_hint);
            RelativeLayout relLayoutFoundItClue = (RelativeLayout) v.findViewById(R.id.rel_layout_found_it_clue);

            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixelsSmallPadding = (int) (10*scale + 0.5f);
            int dpAsPixelsBigPadding = (int) (80*scale + 0.5f);

            if (image != null) {
                slidingDrawerClue.setVisibility(View.VISIBLE);
                relLayoutFoundItClue.setPadding(0, 0, 0, dpAsPixelsBigPadding);
                setImage(image, screenWidth, screenHeight, imgClue);
            } else {
                slidingDrawerClue.setVisibility(View.GONE);
                relLayoutFoundItClue.setPadding(0, 0, 0, dpAsPixelsSmallPadding);
            }

            if (hintImage != null) {
                slidingDrawerHint.setVisibility(View.VISIBLE);
                relLayoutFoundItHint.setPadding(0, 0, 0, dpAsPixelsBigPadding);
                setImage(hintImage, screenWidth, screenHeight, imgHint);
            } else {
                slidingDrawerHint.setVisibility(View.GONE);
                relLayoutFoundItHint.setPadding(0, 0, 0, dpAsPixelsSmallPadding);
            }

            btnReturnToMyLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnZoomToUserLocation();
                }
            });
        }

        btnFoundIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfClueFound();

            }
        });



        btnFoundItHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfClueFound();
            }
        });

        boolean completedQuest = updateCurrentWaypoint();
        if (completedQuest){
            showCompletedQuestMessage();
        }

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
        MainActivity mainActivity = (MainActivity) getActivity();
        setUpMapIfNeeded();
        fragmentInView();
        if(mainActivity.mLastLocation != null){
            drawLocationMarker(mainActivity.mLastLocation);
        }
        drawTiles();

    }

    /**
     * Checks if the user's current location is within the radius of the waypoint
     * (both the radius and waypoint are specified in the quest object)
     */
    private void checkIfClueFound(){
        MainActivity mainActivity = (MainActivity) getActivity();
        Location curLocation = mainActivity.mLastLocation;
        if(curLocation != null) {
            Waypoint curWaypoint = quest.getWaypoints()[numClue];
            double lat = curWaypoint.getLat();
            double lon = curWaypoint.getLng();
            double rad = curWaypoint.getRad();
            float[] results = new float[1];

            Location.distanceBetween(curLocation.getLatitude(), curLocation.getLongitude(),
                    lat, lon,
                    results);
            if (results[0] <= rad) {
                clueCompleted();
            } else {
                //String to display if hint is not already showing
                String alertString = getActivity().getResources().getString(R.string.location_not_found_hint);
                TextView txtHint = (TextView) v.findViewById(R.id.txt_hint);
                if (txtHint.getVisibility() == View.VISIBLE) {
                    //String to display if hint is already showing
                    alertString = getActivity().getResources().getString(R.string.location_not_found);
                }
                mainActivity.showAlertDialog(alertString,
                        new AlertDialog.Builder(mainActivity).create());
            }
        }else{
            Log.i(logMessages.LOCATION, "QuestInProgressFragment: checkIfClueFound: location is null");
            //: this shouln't happen. Handle it better...
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
        MainActivity mainActivity = (MainActivity) getActivity();
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
        boolean finished = false;
        Waypoint[] waypoints = quest.getWaypoints();
        try {
            if(numClue == waypoints.length) {
                finished = true;
                return finished;
            }
            TextView txtClue = (TextView) v.findViewById(R.id.txt_clue);
            TextView txtClueNumber = (TextView) v.findViewById(R.id.txt_clue_number);
            TextView txtClueNumberBack = (TextView) v.findViewById(R.id.txt_clue_number_hint);
            TextView txtHint = (TextView) v.findViewById(R.id.txt_hint);
            SlidingDrawer slidingDrawerClue = (SlidingDrawer) v.findViewById(R.id.front_drawer);
            SlidingDrawer slidingDrawerHint = (SlidingDrawer) v.findViewById(R.id.back_drawer);
            txtClue.setText(waypoints[numClue].getClue().getText());
            txtClueNumber.setText((numClue + 1) + "/" + quest.getWaypoints().length);
            txtClueNumberBack.setText((numClue + 1) + "/" + quest.getWaypoints().length);

            if(txtHint != null || !txtHint.equals("")){
                txtHint.setText(waypoints[numClue].getHint().getText());
            }else{
                txtHint.setText(getResources().getString(R.string.no_hint_available));
            }

            ImageView imgClue = (ImageView) v.findViewById(R.id.img_clue_image_front);
            ImageView imgHint = (ImageView) v.findViewById(R.id.img_hint_image_back);
            String image = null;
            String hintImage = null;
            if(waypoints[numClue].getHint().getImage() != null) {
                hintImage = waypoints[numClue].getHint().getImage().getImage();
            }if(waypoints[numClue].getClue().getImage() != null){
                image = waypoints[numClue].getClue().getImage().getImage();
            }
             if (image != null){
                  slidingDrawerClue.setVisibility(View.VISIBLE);
                  setImage(image, screenWidth, screenHeight, imgClue);
             }else{
                  slidingDrawerClue.setVisibility(View.GONE);
              }

            if (hintImage != null){
                slidingDrawerHint.setVisibility(View.VISIBLE);
                setImage(hintImage, screenWidth, screenHeight, imgHint);
            }else{
                slidingDrawerHint.setVisibility(View.GONE);
            }


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
        showClueCompletedMessage();
        numClue += 1;

        //saves the quest progress into SharedPreferences
        MainActivity mainActivity = (MainActivity) getActivity();
        SharedPreferences.Editor sharedPrefsEditor = mainActivity.getPersistentQuestStorage().edit();
        sharedPrefsEditor.putInt(quest.getName(), numClue);
        sharedPrefsEditor.commit();


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
        final ImageView imgQuestCompleted = (ImageView) v.findViewById(R.id.img_animation_quest_completed);
        TextView txtQuestCompleted = (TextView) v.findViewById(R.id.txt_completion_message);
        RelativeLayout relLayoutQuestCompleted = (RelativeLayout) v.findViewById(R.id.rel_layout_quest_completed);
        Button btnDoneWithQuest = (Button) v.findViewById(R.id.btn_done_with_quest);
        btnDoneWithQuest.setText("DONE");
        final MainActivity mainActivity = (MainActivity) getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && mainActivity.getMemoryClass() > 150) {
            System.gc();
            imgQuestCompleted.setBackground(ContextCompat.getDrawable(mainActivity, R.drawable.anim_quest_completed));
        }
            else{
                imgQuestCompleted.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.qanim25));

        }
        txtQuestCompleted.setText("Message is : " + quest.getCompMsg());
        txtQuestCompleted.setMovementMethod(new ScrollingMovementMethod());
        imgQuestCompleted.setVisibility(View.VISIBLE);
        relLayoutQuestCompleted.setVisibility(View.VISIBLE);
        ((AnimationDrawable) imgQuestCompleted.getBackground()).start();
        btnDoneWithQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && mainActivity.getMemoryClass() > 150) {
                    imgQuestCompleted.setBackground(ContextCompat.getDrawable(mainActivity, R.drawable.bg_transparent));
                    System.gc();
                }else{
                    imgQuestCompleted.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.bg_transparent));
                }
                goBackToQuestSelectionScreen();
            }
        });
    }

    /**
     * Shows the message stored with the quest when the quest has been
     * completed
     */
    private void showClueCompletedMessage(){
        ImageView imgQuestCompleted = (ImageView) v.findViewById(R.id.img_animation_quest_completed);
        TextView txtQuestCompleted = (TextView) v.findViewById(R.id.txt_completion_message);
        final RelativeLayout relLayoutQuestCompleted = (RelativeLayout) v.findViewById(R.id.rel_layout_quest_completed);
        Button btnDoneWithQuest = (Button) v.findViewById(R.id.btn_done_with_quest);
        txtQuestCompleted.setText("Message is : " + quest.getWaypoints()[numClue].getCompletion().getText());
        txtQuestCompleted.setMovementMethod(new ScrollingMovementMethod());

        if(quest.getWaypoints()[numClue].getCompletion().getImage() != null){
            setImage(quest.getWaypoints()[numClue].getCompletion().getImage(),
                    screenWidth, screenHeight, imgQuestCompleted);
        }else{
            imgQuestCompleted.setVisibility(View.GONE);
        }

        relLayoutQuestCompleted.setVisibility(View.VISIBLE);

        btnDoneWithQuest.setText("Continue to Next Hint");
        btnDoneWithQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relLayoutQuestCompleted.setVisibility(View.GONE);
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
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.mLastLocation != null){
            Log.i(logMessages.LOCATION, "QuestInProgressFragment : fragmentInView : last location not null, drawing marker");
            drawLocationMarker(mainActivity.mLastLocation);
        }
        if(this.isResumed()) {
            if(!inView) {
                Log.i("CHECK QUEST", "fragmentInView : fragment added to view, checking if quest is started");
                checkIfQuestStarted();
                inView = true;
            }
            drawTiles();
        }
        setCamera();

        if(isResumed()) {
            ImageView imgClue = (ImageView) v.findViewById(R.id.img_clue_image_front);
            ImageView imgHint = (ImageView) v.findViewById(R.id.img_hint_image_back);
            Waypoint[] waypoints = quest.getWaypoints();

            String image = null;
            String hintImage = null;
            if(waypoints.length == numClue){
                showCompletedQuestMessage();
                return;
            }
            if (waypoints[numClue].getHint().getImage() != null) {
                hintImage = waypoints[numClue].getHint().getImage().getImage();
                setImage(hintImage, screenWidth, screenHeight, imgHint);
            }
            if (waypoints[numClue].getClue().getImage() != null) {
                image = waypoints[numClue].getClue().getImage().getImage();
                setImage(image, screenWidth, screenHeight, imgClue);
            }
        }

        }

    @Override
    public void fragmentOutOfView() {
        super.fragmentOutOfView();
        ImageView imgClue = (ImageView) v.findViewById(R.id.img_clue_image_front);
        ImageView imgHint = (ImageView) v.findViewById(R.id.img_hint_image_back);
        ImageView imgQuestCompleted = (ImageView) v.findViewById(R.id.img_animation_quest_completed);
        imgQuestCompleted.setImageDrawable(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imgQuestCompleted.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        }
        inView = false;
        imgClue.setImageDrawable(null);
        imgHint.setImageDrawable(null);
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

        if (cardFace.getVisibility() == View.GONE)
        {
            cardBack.setVisibility(View.GONE);
            cardBack.animate().alpha(0f).setDuration(300);
            cardFace.bringToFront();
            cardFace.setVisibility(View.VISIBLE);
            cardFace.animate().alpha(1f).setDuration(300);

        }else{
            cardFace.setVisibility(View.GONE);
            cardFace.animate().alpha(0f).setDuration(300);
            cardBack.bringToFront();
            cardBack.setVisibility(View.VISIBLE);
            cardBack.animate().alpha(1f).setDuration(300);
        }
    }

    /**
     */
    public void setImage(String encodedImage, int screenWidth, int screenHeight, ImageView imageView) {

        int w = 10, h = 10;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap mPlaceHolderBitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap



            final BitmapWorkerTask task = new BitmapWorkerTask(imageView,  encodedImage
                    , screenWidth/3, screenHeight/6);
            final BitmapWorkerTask.AsyncDrawable asyncDrawable =
                    new BitmapWorkerTask.AsyncDrawable(mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute();

    }

    private void checkIfQuestStarted(){
        MainActivity mainActivity = (MainActivity) getActivity();
        SharedPreferences sharedPreferences = mainActivity.getPersistentQuestStorage();
        int curClue = sharedPreferences.getInt(quest.getName(), 0);
        if(curClue != 0){
            showOptionToResumeQuest();
        }
    }

    private void showOptionToResumeQuest(){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showAlertDialogNoNeutralButton(new AlertDialog.Builder(mainActivity)
                .setMessage(QUEST_STARTED)
                .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resumeQuest();
                    }
                })
                .setNegativeButton("Start Over", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create());
    }

    private void resumeQuest(){
        MainActivity mainActivity = (MainActivity) getActivity();
        int curClue = mainActivity.getPersistentQuestStorage().getInt(quest.getName(), 0);
        if(curClue != 0){
            numClue = curClue;
        }

        boolean completedQuest = updateCurrentWaypoint();
        if (completedQuest){
            showCompletedQuestMessage();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cardFace = null;
        cardBack = null;
        v = null;
    }

    @Override
    public void onDestroy() {
        quest = null;
        super.onDestroy();
    }
}
