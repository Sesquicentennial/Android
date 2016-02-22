package carleton150.edu.carleton.carleton150.Interfaces;

import android.view.View;

/**
 * Interface used to monitor clicks on a RecyclerView
 */
public interface RecyclerViewClickListener {

    public void recyclerViewListClicked(View v, int position);
}
