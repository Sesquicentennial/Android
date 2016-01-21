package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import carleton150.edu.carleton.carleton150.Adapters.EventsListAdapter;
import carleton150.edu.carleton.carleton150.POJO.Event;
import carleton150.edu.carleton.carleton150.POJO.EventObject.EventContent;
import carleton150.edu.carleton.carleton150.POJO.EventObject.Events;
import carleton150.edu.carleton.carleton150.R;

/**
 * The main fragment for the Social portion of the app. Most
 * of the code was automatically generated and won't be used
 * for the final version, but it's helpful for figuring out how
 * to use fragments
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends MainFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ListView eventsListView;
    private Button btnTryAgain;
    private TextView txtTryAgain;
    private ArrayList<EventContent> events = new ArrayList<>();
    private EventsListAdapter eventsListAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        eventsListView = (ListView) v.findViewById(R.id.lst_events);
        txtTryAgain = (TextView) v.findViewById(R.id.txt_request_events);
        btnTryAgain = (Button) v.findViewById(R.id.btn_try_getting_events);


        requestEvents();

        eventsListAdapter = new EventsListAdapter(events, getActivity().getApplicationContext());
        eventsListView.setAdapter(eventsListAdapter);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTryAgain.setText("Requesting events. Please wait...");
                btnTryAgain.setVisibility(View.GONE);
                requestEvents();
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void requestEvents(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String monthString = String.format("%02d", month);
        String dayString = String.format("%02d", day);
        String startTime = year + "-" + monthString + "-" + dayString;
        Log.i(logMessages.VOLLEY, "requestEvents : start time is : " + startTime);
        volleyRequester.requestEvents(startTime, 20, this);
    }

    @Override
    public void handleNewEvents(Events events) {
        try {
            EventContent[] eventContents = events.getContent();
            for (int i = 0; i < eventContents.length; i++) {
                this.events.add(eventContents[i]);
            }
            txtTryAgain.setVisibility(View.GONE);
            btnTryAgain.setVisibility(View.GONE);
            eventsListView.setVisibility(View.VISIBLE);
            eventsListAdapter.notifyDataSetChanged();
        }catch(NullPointerException e){
            if(this.events.size() == 0) {
                txtTryAgain.setText("Unable to retrieve events. Please check your network and try again.");
                txtTryAgain.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
                eventsListView.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }
}
