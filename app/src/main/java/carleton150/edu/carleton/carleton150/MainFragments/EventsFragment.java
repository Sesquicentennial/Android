package carleton150.edu.carleton.carleton150.MainFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import carleton150.edu.carleton.carleton150.Adapters.EventsListAdapter;
import carleton150.edu.carleton.carleton150.POJO.EventObject.EventContent;
import carleton150.edu.carleton.carleton150.POJO.EventObject.Events;
import carleton150.edu.carleton.carleton150.R;

/**
 * The main fragment for the Events portion of the app. Displays
 * and events calendar
 *
 */
public class EventsFragment extends MainFragment {

    private Button btnTryAgain;
    private TextView txtTryAgain;
    private ArrayList<EventContent> events = new ArrayList<>();

    private EventsListAdapter eventsListAdapter;

    List<EventContent> groupList;
    List<EventContent> childList;
    ExpandableListView eventsListView;

    private Button btn_date;
    private ArrayList<EventContent> eventsByDate = new ArrayList();
    HorizontalScrollView datesScrollView;
    private String strDate;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        txtTryAgain = (TextView) v.findViewById(R.id.txt_request_events);
        btnTryAgain = (Button) v.findViewById(R.id.btn_try_getting_events);

        //requests events from server
        requestEvents();

        LinkedHashMap<String, List<String>> eventCollections = createCollection();

        eventsListView = (ExpandableListView) v.findViewById(R.id.lst_events);
        eventsListAdapter = new EventsListAdapter(getActivity(), events);
        eventsListView.setAdapter(eventsListAdapter);

        datesScrollView = (HorizontalScrollView) v.findViewById(R.id.scroll_dates);
        //eventsListAdapter = new EventsListAdapter(getActivity(), eventsByDate);

        /*btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestEvents();
            }
        });
*/
        /*If no events were retrieved, displays this button so the user can click
        to try again once the network is connected
         */
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTryAgain.setText(getString(R.string.requesting_events));
                btnTryAgain.setVisibility(View.GONE);
                requestEvents();
            }
        });



        return v;
    }

    /**
     * Requests events from server using the volleyRequester
     */
    private void requestEvents(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String monthString = String.format("%02d", month);
        String dayString = String.format("%02d", day);
        String startTime = monthString + "/" + dayString + "/" + year;
        Log.i(logMessages.VOLLEY, "requestEvents : start time is : " + startTime);
        volleyRequester.requestEvents(startTime, 20, this);
    }

    /**
     * Called from VolleyRequester. Handles new events from server
     * @param events
     */
    @Override
    public void handleNewEvents(Events events) {

        /*This is a call from the VolleyRequester, so this check prevents the app from
        crashing if the user leaves the tab while the app is trying
        to get quests from the server
         */

        try {

            try {
                EventContent[] eventContents = events.getContent();
                for (int i = 0; i < eventContents.length; i++) {
                    // eventContents[i].getStartTime();
                    // if equal to button date, then go ahead and add
                    this.events.add(eventContents[i]);
                }
                txtTryAgain.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                eventsListView.setVisibility(View.VISIBLE);
                datesScrollView.setVisibility(View.VISIBLE);
                eventsListAdapter.notifyDataSetChanged();
            } catch (NullPointerException e) {
                if (this.events.size() == 0) {
                    txtTryAgain.setText(getString(R.string.no_events_retrieved));
                    txtTryAgain.setVisibility(View.VISIBLE);
                    btnTryAgain.setVisibility(View.VISIBLE);
                    eventsListView.setVisibility(View.GONE);
                    datesScrollView.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    @Override
    public void fragmentOutOfView() {
        super.fragmentOutOfView();
        Log.i("UI", "EventsFragment : fragmentOutOfView");
    }

    @Override
    public void fragmentInView() {
        super.fragmentInView();
        Log.i("UI", "EventsFragment : fragmentInView");
    }

    private ArrayList<String> createGroupList() {
        ArrayList<String> groupList = new ArrayList<String>();
        groupList.add("HP");
        groupList.add("Dell");
        groupList.add("Lenovo");
        groupList.add("Sony");
        groupList.add("HCL");
        groupList.add("Samsung");
        return groupList;
    }

    private LinkedHashMap<String, List<String>> createCollection() {
        // preparing laptops collection(child)
        String[] hpModels = { "HP Pavilion G6-2014TX", "ProBook HP 4540",
                "HP Envy 4-1025TX" };
        String[] hclModels = { "HCL S2101", "HCL L2102", "HCL V2002" };
        String[] lenovoModels = { "IdeaPad Z Series", "Essential G Series",
                "ThinkPad X Series", "Ideapad Z Series" };
        String[] sonyModels = { "VAIO E Series", "VAIO Z Series",
                "VAIO S Series", "VAIO YB Series" };
        String[] dellModels = { "Inspiron", "Vostro", "XPS" };
        String[] samsungModels = { "NP Series", "Series 5", "SF Series" };

        LinkedHashMap<String, List<String>> eventCollection = new LinkedHashMap<String, List<String>>();

        for (String laptop : createGroupList()) {
            if (laptop.equals("HP")) {
                loadChild(hpModels);
            } else if (laptop.equals("Dell"))
                loadChild(dellModels);
            else if (laptop.equals("Sony"))
                loadChild(sonyModels);
            else if (laptop.equals("HCL"))
                loadChild(hclModels);
            else if (laptop.equals("Samsung"))
                loadChild(samsungModels);
            else
                loadChild(lenovoModels);

            eventCollection.put(laptop, loadChild(hpModels));
        }
        return eventCollection;
    }

    private ArrayList<String> loadChild(String[] eventModels) {
        ArrayList<String> childList = new ArrayList<String>();
        for (String model : eventModels)
            childList.add(model);
        return childList;
    }
}
