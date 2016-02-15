package carleton150.edu.carleton.carleton150.ExtraFragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import carleton150.edu.carleton.carleton150.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddMemoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddMemoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMemoryFragment extends Fragment {

    private View v;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param
     * @param
     * @return A new instance of fragment AddMemoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMemoryFragment newInstance() {
        AddMemoryFragment fragment = new AddMemoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddMemoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_memory, container, false);

        return v;
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

    private void showGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Memory to Upload"), 1);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data){
        ImageView imgMemoryView = (ImageView) v.findViewById(R.id.img_memory_view);
        if(resultCode == 1){
            imgMemoryView.setImageURI(data.getData());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v = null;
    }
}
