package carleton150.edu.carleton.carleton150.MainFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

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

    private ListView eventsListView;
    private Button btnTryAgain;
    private TextView txtTryAgain;
    private ArrayList<EventContent> events = new ArrayList<>();
    private EventsListAdapter eventsListAdapter;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        eventsListView = (ListView) v.findViewById(R.id.lst_events);
        txtTryAgain = (TextView) v.findViewById(R.id.txt_request_events);
        btnTryAgain = (Button) v.findViewById(R.id.btn_try_getting_events);

        //requests events from server
        requestEvents();

        eventsListAdapter = new EventsListAdapter(events, getActivity().getApplicationContext(), getActivity().getLayoutInflater());
        eventsListView.setAdapter(eventsListAdapter);

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
                    this.events.add(eventContents[i]);
                }
                txtTryAgain.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                eventsListView.setVisibility(View.VISIBLE);
                eventsListAdapter.notifyDataSetChanged();
            } catch (NullPointerException e) {
                if (this.events.size() == 0) {
                    txtTryAgain.setText(getString(R.string.no_events_retrieved));
                    txtTryAgain.setVisibility(View.VISIBLE);
                    btnTryAgain.setVisibility(View.VISIBLE);
                    eventsListView.setVisibility(View.GONE);
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
}
