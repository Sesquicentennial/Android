package carleton150.edu.carleton.carleton150.ExtraFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import carleton150.edu.carleton.carleton150.Camera;
import carleton150.edu.carleton.carleton150.LogMessages;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;
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
    private Uri imageUri = null;
    private File photoFile = null;
    private ArrayList<EditText> editTexts = new ArrayList();
    private String imageString = null;

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
        EditText etTitle = (EditText) v.findViewById(R.id.et_memory_title);
        EditText etDesc = (EditText) v.findViewById(R.id.et_memory_description);
        EditText etUploader = (EditText) v.findViewById(R.id.et_memory_uploader);
        editTexts.add(etTitle);
        editTexts.add(etDesc);
        editTexts.add(etUploader);

        Button btnSelectImage = (Button) v.findViewById(R.id.btn_select_image);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        Button btnExitPopup = (Button) v.findViewById(R.id.btn_exit_popup);
        btnExitPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCurrentFragment();
            }
        });

        Button btnSubmit = (Button) v.findViewById(R.id.btn_submit_memory);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMemoryIfPossible();
            }
        });

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

    private void removeCurrentFragment(){
        EditText focusedEditText = null;
        for(int i = 0; i<editTexts.size(); i++){
            if(editTexts.get(i).hasFocus()){
                focusedEditText = editTexts.get(i);
            }
        }
        if(focusedEditText != null) {
            closeKeyboard(getActivity(), focusedEditText.getWindowToken());
        }
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        fm.detach(this).remove(this).commit();

    }


    public void takePhoto() {
        try {
            photoFile = Camera.createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        startActivityForResult(Camera.photoIntent(getActivity(), photoFile), 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //TODO : Taking photo crashes app
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 5) { //TODO uri get path is null
                Log.i("photo location", photoFile.getAbsolutePath());
                String path = Camera.photoResult(getActivity(), photoFile, data);
                setImageString(path, getActivity());
                //add your own logic for path

                ImageView imgMemoryView = (ImageView) v.findViewById(R.id.img_memory_view);
                Button btnSelectImage = (Button) v.findViewById(R.id.btn_select_image);

                if (resultCode != 1) {
                    Bitmap downsizedBitmap = null;
                    try {
                        downsizedBitmap = decodeUri(getActivity(),Uri.fromFile(new File(path)), 400);
                        Log.i(new LogMessages().IMAGE_HANDLING, "OnActivityResult : downsized bitmap successfully");
                        imgMemoryView.setImageBitmap(downsizedBitmap);
                        imgMemoryView.setVisibility(View.VISIBLE);
                        btnSelectImage.setVisibility(View.GONE);

                    } catch (FileNotFoundException e) {
                        Log.i(new LogMessages().IMAGE_HANDLING, "OnActivityResult : unable to downsize bitmap");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void setImageString(String path, Context context){
        Bitmap downsizedBitmap = null;
        try {
            downsizedBitmap = decodeUri(context,Uri.fromFile(new File(path)), 400);
        } catch (FileNotFoundException e) {
            Log.i(new LogMessages().MEMORY_MONITORING, "setImageString : unable to downsize bitmap");
            e.printStackTrace();
            return;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        downsizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] byteArray = bos.toByteArray();
        imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.i(new LogMessages().MEMORY_MONITORING, "setImageString : imageString is : " + imageString);
    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v = null;
        editTexts = null;
    }


    private void uploadMemoryIfPossible(){
        EditText etTitle = (EditText) v.findViewById(R.id.et_memory_title);
        EditText etDesc = (EditText) v.findViewById(R.id.et_memory_description);
        EditText etUploader = (EditText) v.findViewById(R.id.et_memory_uploader);
        MainActivity mainActivity = (MainActivity) getActivity();
        ImageView image = (ImageView) v.findViewById(R.id.img_memory_view);

        if(imageString == null){
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
            mainActivity.showAlertDialog(getResources().getString(R.string.unable_to_decode_image), alertDialog);
            return;
        }

        if(checkTextFieldsEntered()){
            String title = etTitle.getText().toString();
            String desc = etDesc.getText().toString();
            String uploader = etUploader.getText().toString();

            Calendar c = Calendar.getInstance();
            String timestamp = c.getTime().toString();

            Location curLocation = mainActivity.getLastLocation();
            double lat;
            double lng;
            if(curLocation != null) {
                lat = curLocation.getLatitude();
                lng = curLocation.getLongitude();
            }else{
                AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
                mainActivity.showAlertDialog(getResources().getString(R.string.unable_to_get_location), alertDialog);
                return;
            }
            VolleyRequester volleyRequester = new VolleyRequester();

            volleyRequester.addMemory(imageString, title, uploader, desc, timestamp, lat, lng);
            removeCurrentFragment();
        }


    }


    /**
     * Makes sure that the user has entered text for the required fields. If not,
     * displays an alert dialog requesting that they do and returns false. Otherwise,
     * returns true
     * @return
     */
    private boolean checkTextFieldsEntered(){
        EditText etTitle = (EditText) v.findViewById(R.id.et_memory_title);
        EditText etDesc = (EditText) v.findViewById(R.id.et_memory_description);
        EditText etUploader = (EditText) v.findViewById(R.id.et_memory_uploader);
        if(etTitle.getText().toString().equals("")){
            MainActivity mainActivity = (MainActivity) getActivity();
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
            mainActivity.showAlertDialog(getResources().getString(R.string.please_add_memory_title), alertDialog);
            return false;
        }if(etDesc.getText().toString().equals("")){
            MainActivity mainActivity = (MainActivity) getActivity();
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
            mainActivity.showAlertDialog(getResources().getString(R.string.please_add_memory_desc), alertDialog);
            return false;
        }if(etUploader.getText().toString().equals("")){
            MainActivity mainActivity = (MainActivity) getActivity();
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
            mainActivity.showAlertDialog(getResources().getString(R.string.please_add_memory_uploader_name), alertDialog);
            return false;
        }
        return true;
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

}
