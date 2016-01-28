package carleton150.edu.carleton.carleton150.Adapters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.MainActivity;
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
    private Resources resources;


    public HistoryCardAdapter(GeofenceInfoContent[] geofenceList, RecyclerViewClickListener clickListener,
                              int screenWidth, Resources resources) {
        this.geofenceList = geofenceList;
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
        public void setBackground() {
            image.setImageBitmap(decodeSampledBitmapFromResource(resources, R.drawable.test_image1, 100, 100));

        }

        @Override
        public void onClick(View v) {
            clickListener.recyclerViewListClicked(v, getLayoutPosition());
        }

    }

    public GeofenceInfoContent getItemAtPosition(int position){
        return geofenceList[position];
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
