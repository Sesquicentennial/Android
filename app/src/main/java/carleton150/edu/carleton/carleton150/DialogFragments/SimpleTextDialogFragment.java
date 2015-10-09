package carleton150.edu.carleton.carleton150.DialogFragments;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import carleton150.edu.carleton.carleton150.R;


/**
 * Created by haleyhinze on 8/6/15.
 *
 * A simple DialogFragment that displays a message and an ok button.
 * When the ok button is pressed, the dialog is dismissed.
 */
public class SimpleTextDialogFragment extends DialogFragment {

    private String curTag;
    private String noNetworkTag = "noNetwork";
    private String noGPSTag = "noGPS";
    private String noItemNameTag = "noItemName";


    /**
     * Create a new instance of SimpleTextDialogFragment
     */
    public static SimpleTextDialogFragment newInstance() {
        SimpleTextDialogFragment dialogFragment = new SimpleTextDialogFragment();

        return dialogFragment;

    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        curTag = tag;
        return super.show(transaction, tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_simple_text, container, false);

        TextView tv = (TextView) v.findViewById(R.id.text);
        TextView secondaryText = (TextView) v.findViewById(R.id.secondary_text);

        Button button = (Button)v.findViewById(R.id.btnOk);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        String displayText = "";

        //uses the tag provided when the dialog fragment was created to determine what
        //the message will be
        if (curTag.equals(noNetworkTag)){
            displayText = "No network connection is available. Please connect and try again.";
        }
        else if (curTag.equals(noGPSTag)){
            displayText = "GPS isn't on.";
            secondaryText.setText("Please turn on GPS in settings and try again");
        }else if (curTag.equals(noItemNameTag)){
            displayText = "No item name.";
            secondaryText.setText("Please set an item name and try saving again.");
        }

        tv.setText(displayText);

        // Watch for button clicks.

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }
}
