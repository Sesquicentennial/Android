package carleton150.edu.carleton.carleton150.DialogFragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content;
import carleton150.edu.carleton.carleton150.R;

/**
 * Class to manage a HistoryPopoverDialogFragment. Currently fills in a text view
 * and a title from the geofenceInfoObject
 */
public class HistoryPopoverDialogFragment extends DialogFragment {

    private View view;
    private static Content geofenceInfoObject;
    public HistoryPopoverDialogFragment()
    {
    }

    public static HistoryPopoverDialogFragment newInstance(Content object) {
        HistoryPopoverDialogFragment f = new HistoryPopoverDialogFragment();
        geofenceInfoObject = object;
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        view = getActivity().getLayoutInflater().
                inflate(R.layout.fragment_history_popover_dialog, new LinearLayout(getActivity()), false);
        TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
        TextView txtInfo = (TextView) view.findViewById(R.id.txt_info);
        txtTitle.setText(geofenceInfoObject.getName());
        txtInfo.setText(geofenceInfoObject.getData());

        // Build dialog
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);

        builder.getWindow().getAttributes().windowAnimations = R.anim.abc_slide_in_top;
        return builder;
    }
}
