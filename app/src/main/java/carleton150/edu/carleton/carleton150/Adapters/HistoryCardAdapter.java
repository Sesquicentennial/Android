package carleton150.edu.carleton.carleton150.Adapters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.R;


/**
 * Adapter for the RecyclerView that shows images in the bottom of the HistoryFragment
 */
public class HistoryCardAdapter extends RecyclerView.Adapter<HistoryCardAdapter.HistoryCardViewHolder> {

    private ArrayList<GeofenceInfoContent[]> geofenceList = new ArrayList<>();
    public static RecyclerViewClickListener clickListener;
    private int screenWidth;
    private View itemView;
    private Resources resources;


    public HistoryCardAdapter(HashMap<String, GeofenceInfoContent[]> geofenceMap, RecyclerViewClickListener clickListener,
                              int screenWidth, Resources resources) {

        if(geofenceMap != null){
            geofenceList = turnHashMapToArr(geofenceMap);
        }

        this.clickListener = clickListener;
        this.screenWidth = screenWidth;
        this.resources = resources;
    }



    @Override
    public HistoryCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.history_card, parent, false);
        return new HistoryCardViewHolder(itemView, resources);
    }



    @Override
    public void onBindViewHolder(HistoryCardViewHolder holder, int position) {

        String imageString = null;
        GeofenceInfoContent[] geofenceInfoContents = geofenceList.get(position);
        int i = 0;
        while (imageString == null && i < geofenceInfoContents.length) {

            if(geofenceInfoContents[i].getType().equals(geofenceInfoContents[i].TYPE_IMAGE)){
                imageString = geofenceInfoContents[i].getData();
            }
            i++;
        }


        //TODO: get a good width...
        holder.setWidth((int) (screenWidth / 5));

        if(imageString != null) {
            holder.setBackground(imageString);
        }
    }

    @Override
    public int getItemCount() {
        if(geofenceList != null) {
            return geofenceList.size();
        }else{
            return 0;
        }
    }

    private ArrayList<GeofenceInfoContent[]> turnHashMapToArr(HashMap<String, GeofenceInfoContent[]> hashMap){

        ArrayList<GeofenceInfoContent[]> arrayList = new ArrayList<>();
        for(Map.Entry<String, GeofenceInfoContent[]> e : hashMap.entrySet()){
            arrayList.add(e.getValue());
        }
        return arrayList;
    }

    public void updateGeofences(HashMap<String, GeofenceInfoContent[]> newGeofences){
        this.geofenceList = turnHashMapToArr(newGeofences);
        notifyDataSetChanged();
    }

    public ArrayList<GeofenceInfoContent[]> getGeofencesList(){
        return this.geofenceList;
    }


    public static class HistoryCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private Resources resources;

        public HistoryCardViewHolder(View itemView, Resources resources) {
            super(itemView);
            this.resources = resources;
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
        public void setBackground(String encodedImage) {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            image.setImageBitmap(decodedByte);

        }

        @Override
        public void onClick(View v) {
            clickListener.recyclerViewListClicked(v, getLayoutPosition());
        }

    }

    public GeofenceInfoContent[] getItemAtPosition(int position){
        return geofenceList.get(position);
    }




}
