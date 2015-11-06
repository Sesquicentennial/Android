package carleton150.edu.carleton.carleton150.DialogFragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import carleton150.edu.carleton.carleton150.ArrayAdapters.LandmarkListAdapter;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.R;


public class HistoryPopoverDialogFragment extends DialogFragment {

    private View view;
    public HistoryPopoverDialogFragment()
    {
    }

    public static HistoryPopoverDialogFragment newInstance() {
        HistoryPopoverDialogFragment f = new HistoryPopoverDialogFragment();
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        view = getActivity().getLayoutInflater().
                inflate(R.layout.fragment_history_popover_dialog, new LinearLayout(getActivity()), false);

        // Build dialog
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);

        builder.getWindow().getAttributes().windowAnimations = R.anim.abc_slide_in_top;
       // setStyle(DialogFragment.STYLE_NO_TITLE, R.style.HistoryPopoverDialogFragmentStyle);

        return builder;
    }
}
