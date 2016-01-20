package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import carleton150.edu.carleton.carleton150.Adapters.QuestAdapter;
import carleton150.edu.carleton.carleton150.Adapters.QuestInfo;
import carleton150.edu.carleton.carleton150.Adapters.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestFragment extends MainFragment implements RecyclerViewClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static View view;
    private RecyclerView quests;
    private ArrayList<Quest> questInfo;
    private LinearLayoutManager questLayoutManager;
    private QuestAdapter questAdapter;
    private int screenWidth;

    private Button btnStartQuest;
    private TextView txtInfo;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuestFragment newInstance(String param1, String param2) {
        QuestFragment fragment = new QuestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public QuestFragment() {
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
        view =  inflater.inflate(R.layout.fragment_quest, container, false);
        btnStartQuest = (Button) view.findViewById(R.id.btn_start_quest);
        txtInfo = (TextView) view.findViewById(R.id.txt_quest_description);

        //builds RecyclerViews to display scavenger hunts
        buildRecyclerViews();

        return view;
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

    @Override
    public void recyclerViewListClicked(View v, final int position) {
        Log.i("View", "QuestFragment : recyclerViewListClicked");
        btnStartQuest.setClickable(true);
        btnStartQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // beginQuest(questInfo.get(position));
            }
        });

       txtInfo.setText(questAdapter.getQuestList().get(position).getDesc());
    }

    private void beginQuest(QuestInfo questInfo){
        //TODO: start questing fragment
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

    private void buildRecyclerViews(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;

        quests = (RecyclerView) view.findViewById(R.id.lst_quests);
        questLayoutManager = new LinearLayoutManager(getActivity());
        questLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        quests.setLayoutManager(questLayoutManager);
        questAdapter = new QuestAdapter(questInfo, this, screenWidth);
        quests.setAdapter(questAdapter);
    }


    /**
     * Dummy method to create a filler list
     * @param size
     * @return
     */
    private List createList(int size) {

        List result = new ArrayList();
        for (int i = 1; i <= size; i++) {
            QuestInfo qi = new QuestInfo();
            qi.setTitle("title" + i);
            qi.setDescription("description blah blah blah blah long description here to see what happens" + i);
            qi.setCreator("creator" + i);
            qi.setWidth((int) (screenWidth /1.5));
            result.add(qi);

        }

        return result;
    }

    @Override
    public void handleNewQuests(ArrayList<Quest> newQuests) {
        super.handleNewQuests(newQuests);
        if(newQuests != null) {
            questAdapter.updateQuests(newQuests);
            questInfo = newQuests;
            Log.i(logMessages.VOLLEY, "QuestFragment: handleNewQuests : questAdapter contains : " + questAdapter.getItemCount());
        }


    }

    @Override
    public void fragmentOutOfView() {
        super.fragmentOutOfView();
    }

    @Override
    public void fragmentInView() {
        super.fragmentInView();
        volleyRequester.requestQuests(this);
    }
}
