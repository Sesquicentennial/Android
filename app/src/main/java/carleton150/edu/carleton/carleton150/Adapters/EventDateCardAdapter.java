package carleton150.edu.carleton.carleton150.Adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewDatesClickListener;
import carleton150.edu.carleton.carleton150.R;


/**
 * Adapter for the RecyclerView that shows images in the bottom of the HistoryFragment
 */
public class EventDateCardAdapter extends RecyclerView.Adapter<EventDateCardAdapter.EventDateCardViewHolder> {

    public static RecyclerViewDatesClickListener clickListener;
    private int screenWidth;
    private View itemView;
    private Resources resources;
    private ArrayList<String> dateInfo;


    public EventDateCardAdapter(ArrayList<String> dateInfo, RecyclerViewDatesClickListener clickListener,
                                int screenWidth, Resources resources) {

        this.clickListener = clickListener;
        this.screenWidth = screenWidth;
        this.resources = resources;
        this.dateInfo = dateInfo;
    }



    @Override
    public EventDateCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.event_date_card, parent, false);
        return new EventDateCardViewHolder(itemView);
    }


    // Setting view for each card
    @Override
    public void onBindViewHolder(EventDateCardViewHolder holder, int position) {
        holder.setDate(dateInfo.get(position));

        //TODO: get a good width...
        holder.setWidth((int) (screenWidth / 2.5));
    }

    @Override
    public int getItemCount() {
        if(dateInfo != null) {
            return dateInfo.size();
        }else{
            return 0;
        }
    }

    public static class EventDateCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public EventDateCardViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

        }

        /**
         * @param width
         */
        public void setWidth(int width) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
        }

        @Override
        public void onClick(View v) {
            TextView dateTitle = (TextView) itemView.findViewById(R.id.event_date_title);
            String dateInfo = dateTitle.getTag().toString();

            clickListener.recyclerViewListClicked(dateInfo);

        }

        public void setDate(String dateInfo) {
            Log.i(dateInfo, "Meow");
            TextView dateTitle = (TextView) itemView.findViewById(R.id.event_date_title);
            DateFormat df = new SimpleDateFormat("EEEE'\r\n' MMM dd");
            String[] dateArray = dateInfo.split("-");
            Log.d(String.valueOf(dateArray[0] + dateArray[1] + dateArray[2]), "dateArray should be this");
            Date dateCalendar = new Date(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1])-1, Integer.parseInt(dateArray[2]));
            String cleanDateInfo = df.format(dateCalendar);
            Log.d(cleanDateInfo, "cleanDateInfo");

            dateTitle.setText(cleanDateInfo);
            dateTitle.setTag(dateInfo);
        }

    }
}
//// TODO: Parse dateInfo to Calendar object -> String
//String currentDateInfo = dateInfo.get(position);
//DateFormat df = new SimpleDateFormat("EEEE',' MMM dd");
//String[] dateArray = currentDateInfo.split("-");
//GregorianCalendar dateCalendar = new GregorianCalendar(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
//String cleanDateInfo = df.format(dateCalendar);
//Log.d(cleanDateInfo, "cleanDateInfo");
//        holder.setDate(cleanDateInfo);