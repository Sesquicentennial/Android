package carleton150.edu.carleton.carleton150.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.R;


/**
 * Created by haleyhinze on 12/8/15.
 */
public class HistoryCardAdapter extends RecyclerView.Adapter<HistoryCardAdapter.HistoryCardViewHolder> {

    private GeofenceInfoContent[] geofenceList = null;
    public static RecyclerViewClickListener clickListener;
    private int screenWidth;
    private View itemView;

    public HistoryCardAdapter(GeofenceInfoContent[] geofenceList, RecyclerViewClickListener clickListener, int screenWidth) {
        this.geofenceList = geofenceList;
        this.clickListener = clickListener;
        this.screenWidth = screenWidth;
    }

    @Override
    public HistoryCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.history_card, parent, false);
        return new HistoryCardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryCardViewHolder holder, int position) {
        GeofenceInfoContent geofenceInfoContent = geofenceList[position];


        //TODO: get a good width...
        holder.setWidth((int) (screenWidth / 5));


        //TODO:make this background come from qi
        holder.setBackground();
    }

    @Override
    public int getItemCount() {
        if(geofenceList != null) {
            return geofenceList.length;
        }else{
            return 0;
        }
    }

    public void updateGeofences(GeofenceInfoContent[] newGeofences){
        this.geofenceList = newGeofences;
        notifyDataSetChanged();
    }

    public GeofenceInfoContent[] getGeofencesList(){
        return this.geofenceList;
    }


    public static class HistoryCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;

        public HistoryCardViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            image = (ImageView) itemView.findViewById(R.id.img_history_card);
        }

        /**
         * @param width
         */
        public void setWidth(int width) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
        }

        /**
         */
        public void setBackground() {
            image.setImageResource(R.drawable.test_image2);

        }

        @Override
        public void onClick(View v) {
            clickListener.recyclerViewListClicked(v, getLayoutPosition());
        }

    }

    public GeofenceInfoContent getItemAtPosition(int position){
        return geofenceList[position];
    }



}
