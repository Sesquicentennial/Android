package carleton150.edu.carleton.carleton150.ExtraFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

import carleton150.edu.carleton.carleton150.Adapters.HistoryAdapter;
import carleton150.edu.carleton.carleton150.Adapters.MyScaleInAnimationAdapter;
import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewScrolledListener;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
import carleton150.edu.carleton.carleton150.POJO.HistoryContentObjectDummy;
import carleton150.edu.carleton.carleton150.R;

/**
 * Class to manage a HistoryPopoverDialogFragment. Currently fills in a text view
 * and a title from the geofenceInfoObject
 */
public class HistoryPopoverFragment extends Fragment implements RecyclerViewClickListener, RecyclerViewScrolledListener{

    private View view;

    private RecyclerView historyInfoObjects;
    private ArrayList<HistoryContentObjectDummy> historyInfo;
    private LinearLayoutManager historyLayoutManager;
    private HistoryAdapter historyAdapter;
    private Button btnClose;
    private TextView txtTimelineDate;

    private static GeofenceInfoContent geofenceInfoObject;
    public HistoryPopoverFragment()
    {
    }

    public static HistoryPopoverFragment newInstance(GeofenceInfoContent object) {
        HistoryPopoverFragment f = new HistoryPopoverFragment();
        geofenceInfoObject = object;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = getActivity().getLayoutInflater().
                inflate(R.layout.fragment_history_popover, new LinearLayout(getActivity()), false);
        TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtTimelineDate = (TextView) view.findViewById(R.id.txt_timeline_date);
        btnClose = (Button) view.findViewById(R.id.btn_exit_popup);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCurrentFragment();
            }
        });

        txtTitle.setText(geofenceInfoObject.getName());


        buildDummyHistoryContent(12, geofenceInfoObject.getData());

        //builds RecyclerViews to display info
        buildRecyclerViews();
        //historyAdapter.updateHistoryList(historyInfo);
       // alphaInAnimationAdapter.notifyDataSetChanged();

        return view;
    }

    private void removeCurrentFragment(){
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        fm.remove(this).commit();
    }

    /**
     * Builds the views for the historical info
     */
    private void buildRecyclerViews(){

        historyInfoObjects = (RecyclerView) view.findViewById(R.id.lst_history_items);
        historyLayoutManager = new LinearLayoutManager(getActivity());
        historyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historyInfoObjects.setLayoutManager(historyLayoutManager);


        historyAdapter = new HistoryAdapter(historyInfo, this, historyInfoObjects, this);

        //RecyclerView animation
        MyScaleInAnimationAdapter scaleInAnimationAdapter = new MyScaleInAnimationAdapter(historyAdapter);
        scaleInAnimationAdapter.setFirstOnly(false);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());



        historyInfoObjects.setAdapter(scaleInAnimationAdapter);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {

    }

    @Override
    public void recyclerViewScrolled(String date) {
        txtTimelineDate.setVisibility(View.VISIBLE);
        txtTimelineDate.setText(date);
    }

    @Override
    public void recyclerViewStoppedScrolling() {
        txtTimelineDate.setVisibility(View.GONE);
    }

    private void buildDummyHistoryContent(int num, String curContent){
        historyInfo = new ArrayList<>();
        HistoryContentObjectDummy newContent = new HistoryContentObjectDummy();
        newContent.setType(1);
        newContent.setText(curContent);
        historyInfo.add(newContent);
        for(int i = 0; i <num ; i++){
            HistoryContentObjectDummy newDummyContent = new HistoryContentObjectDummy();
           Random rand = new Random();
            int value = rand.nextInt(2);
            newDummyContent.setType(value);
            int date = 2000 + i;
            newDummyContent.setDate("" + date);
            if(value == 1){
                newDummyContent.setText("SOME DUMMY TEXT");
            }else{
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_image1);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                newDummyContent.setImage(bitmapdata);
            }
            historyInfo.add(newDummyContent);
        }
    }
}
