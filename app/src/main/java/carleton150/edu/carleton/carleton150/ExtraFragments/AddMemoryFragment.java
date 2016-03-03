package carleton150.edu.carleton.carleton150.ExtraFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import carleton150.edu.carleton.carleton150.Camera;
import carleton150.edu.carleton.carleton150.LogMessages;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;
import carleton150.edu.carleton.carleton150.R;

/**
 * Fragment to add a new memory, allows user to either take a picture
 * or choose one from their documents to upload
 */
public class AddMemoryFragment extends Fragment {

    private View v;
    private File photoFile = null;
    private ArrayList<EditText> editTexts = new ArrayList();
    private String imageString = null;
    private Dialog alertDialogLoading = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddMemoryFragment.
     */
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

    /**
     * Manages view items
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
     * Removes itself and makes sure to close the keyboard
     */
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

    /**
     * Called when user clicks "Add Image" button. Creates an image file
     * for the photo and starts an intent for the user to select a photo
     * or take a photo
     */
    public void takePhoto() {
        try {
            photoFile = Camera.createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        startActivityForResult(Camera.photoIntent(getActivity(), photoFile), 5);
    }

    /**
     * Called when the user selects or takes a photo.
     * Displays the selected image on the screen.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 5) {
                Log.i("photo location", photoFile.getAbsolutePath());
                String path = Camera.photoResult(getActivity(), photoFile, data);
                setImageString(path, getActivity());
                ImageView imgMemoryView = (ImageView) v.findViewById(R.id.img_memory_view);
                Button btnSelectImage = (Button) v.findViewById(R.id.btn_select_image);

                if (resultCode != 1) {
                    Bitmap downsizedBitmap = null;
                    try {
                        downsizedBitmap = decodeUri(getActivity(),Uri.fromFile(new File(path)), 800);
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

    /**
     * Downsizes the bitmap the path refers to and turns it to a 64-bit string.
     * Sets imageString to refer to that string
     *
     * @param path the path of the file where the image is stored
     * @param context
     */
    private void setImageString(String path, Context context){
        Bitmap downsizedBitmap = null;
        try {
            downsizedBitmap = decodeUri(context,Uri.fromFile(new File(path)), 800);
        } catch (FileNotFoundException e) {
            Log.i(new LogMessages().MEMORY_MONITORING, "setImageString : unable to downsize bitmap");
            e.printStackTrace();
            return;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        downsizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] byteArray = bos.toByteArray();
        imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.i(new LogMessages().MEMORY_MONITORING, "setImageString : imageString is : " + imageString);
    }

    /**
     * Decodes a uri into a bitmap
     * @param c context
     * @param uri the uri of the photo to change to a bitmap
     * @param requiredSize size of the output bitmap
     * @return
     * @throws FileNotFoundException
     */
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

    /**
     * Attempts to upload a memory if the user has entered all the required fields.
     * If the user entered required fields, displays a dialog telling the user that
     * the image is being uploaded
     */
    private void uploadMemoryIfPossible(){
        EditText etTitle = (EditText) v.findViewById(R.id.et_memory_title);
        EditText etDesc = (EditText) v.findViewById(R.id.et_memory_description);
        EditText etUploader = (EditText) v.findViewById(R.id.et_memory_uploader);
        MainActivity mainActivity = (MainActivity) getActivity();
        if(imageString == null){
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
            mainActivity.showAlertDialog(getResources().getString(R.string.unable_to_decode_image), alertDialog);
            return;
        }
        if(checkTextFieldsEntered()){
            String title = etTitle.getText().toString();
            String desc = etDesc.getText().toString();
            String uploader = etUploader.getText().toString();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
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
            volleyRequester.addMemory(imageString, title, uploader, desc, timestamp, lat, lng, this);
            RelativeLayout layout = new RelativeLayout(getActivity());
            TextView textView = new TextView(getActivity());
            textView.setText("Uploading memory...");
            textView.setTextColor(getResources().getColor(R.color.colorAccent));
            textView.setBackgroundColor(getResources().getColor(R.color.transparent));
            RelativeLayout.LayoutParams childParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            childParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(textView, childParams);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            alertDialogLoading = new Dialog(getActivity(), R.style.LoadingDialogTheme);
            alertDialogLoading.addContentView(layout, params);
            alertDialogLoading.show();
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

    /**
     * Closes the soft keyboard
     * @param c
     * @param windowToken
     */
    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    /**
     * Method that is called when the memory was uploaded successfully. Closes the
     * AddMemoryFragment and returns to the HistoryView
     */
    public void addMemorySuccess(){
        if(alertDialogLoading != null){
            alertDialogLoading.dismiss();
            alertDialogLoading = null;
        }
        MainActivity mainActivity = (MainActivity) getActivity();
        AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
        mainActivity.showAlertDialog(getResources().getString(R.string.successfully_added_memory), alertDialog);
        removeCurrentFragment();
    }

    /**
     * Method that is called when the memory was not uploaded successfully. Displays an
     * AlertDialog requesting that the user try to upload the memory again at another time
     */
    public void addMemoryError(){
        if(alertDialogLoading != null){
            alertDialogLoading.dismiss();
            alertDialogLoading = null;
        }
        MainActivity mainActivity = (MainActivity) getActivity();
        AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
        mainActivity.showAlertDialog(getResources().getString(R.string.unable_to_add_memory), alertDialog);
    }

}
