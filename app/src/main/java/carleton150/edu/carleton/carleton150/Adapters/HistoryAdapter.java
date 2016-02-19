package carleton150.edu.carleton.carleton150.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewScrolledListener;
import carleton150.edu.carleton.carleton150.LogMessages;
import carleton150.edu.carleton.carleton150.Models.BitmapWorkerTask;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
import carleton150.edu.carleton.carleton150.R;

/**
 * Adapter for the RecyclerView in the HistoryInfoPopup
 */
public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private GeofenceInfoContent[] historyList = null;
    public static RecyclerViewScrolledListener scrolledListener;
    public int screenWidth;
    public int screenHeight;
    public boolean isMemories;
    public Context context;

    public HistoryAdapter(Context context, GeofenceInfoContent[] historyList, RecyclerView recyclerView,
                          RecyclerViewScrolledListener scrolledListener, int screenWidth, int screenHeight, boolean isMemories) {
        this.historyList = historyList;
        this.scrolledListener = scrolledListener;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.isMemories = isMemories;
        this.context = context;

        if (!isMemories) {
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == recyclerView.SCROLL_STATE_IDLE) {
                        clearDate();
                    } else {
                        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                        displayDate(lm);
                    }

                }
            });
        }
    }

    private void displayDate(LinearLayoutManager lm){
        int lastVisible = lm.findLastVisibleItemPosition();
        scrolledListener.recyclerViewScrolled(historyList[lastVisible].getYear());
    }

    private void clearDate(){
        if(scrolledListener != null) {
            scrolledListener.recyclerViewStoppedScrolling();
        }
    }

    public void closeAdapter(){
        this.context = null;
    }

    /**
     * Returns 0 if the object contains an image, 1 if it contains text
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if(isMemories){
            return 0;
        }
        if(historyList[position].getType().equals(historyList[position].TYPE_IMAGE)){
            return 0;
        } if(historyList[position].getType().equals(historyList[position].TYPE_TEXT)){
            return 1;
        } else {
            return -1;
        }
    }




    /**
     * Depending on the type, creates a ViewHolder from either the
     * history_info_card_image or the history_info_card_text
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.history_info_card_image, parent, false);
                return new HistoryViewHolderImage(itemView);
            case 1:
                View view = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.history_info_card_text, parent, false);
                return new HistoryViewHolderText(view);
        }
        return null;
    }

    /**
     * When the holder is bound, sets the necessary fields depending on the type
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final GeofenceInfoContent geofenceInfoContent = historyList[position];
        if(holder instanceof HistoryViewHolderText){
            ((HistoryViewHolderText) holder).setTxtSummary(geofenceInfoContent.getSummary());
            ((HistoryViewHolderText) holder).setTxtDescription(geofenceInfoContent.getData());
            ((HistoryViewHolderText) holder).setTxtDate(geofenceInfoContent.getYear());
            ((HistoryViewHolderText) holder).setExpanded(geofenceInfoContent.isExpanded());
            ImageView imgExpanded = ((HistoryViewHolderText) holder).getIconExpand();
            imgExpanded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ClickListener", "item clicked!");
                    geofenceInfoContent.setExpanded(!geofenceInfoContent.isExpanded());
                    ((HistoryViewHolderText) holder).setExpanded(geofenceInfoContent.isExpanded());
                    ((HistoryViewHolderText) holder).setIconExpand(context);
                }
            });
        }else if(holder instanceof HistoryViewHolderImage){
            if(!isMemories) {
                ((HistoryViewHolderImage) holder).setImage(position, geofenceInfoContent.getData(), screenWidth, screenHeight);
                ((HistoryViewHolderImage) holder).setTxtDate(geofenceInfoContent.getYear());
                ((HistoryViewHolderImage) holder).setTxtCaption(geofenceInfoContent.getCaption());
                ((HistoryViewHolderImage) holder).setTxtDescription(geofenceInfoContent.getDesc());
                ((HistoryViewHolderImage) holder).setExpanded(geofenceInfoContent.isExpanded());
                ImageView imgExpanded = ((HistoryViewHolderImage) holder).getIconExpand();
                imgExpanded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("ClickListener", "item clicked!");
                        geofenceInfoContent.setExpanded(!geofenceInfoContent.isExpanded());
                        ((HistoryViewHolderImage) holder).setExpanded(geofenceInfoContent.isExpanded());
                        ((HistoryViewHolderImage) holder).setIconExpand(context);
                    }
                });
            }else{
                Log.i("HistoryAdapter", "Image string for memory is: " + geofenceInfoContent.getImage());
                ((HistoryViewHolderImage) holder).setImage(position, geofenceInfoContent.getImage(), screenWidth, screenHeight);
                ((HistoryViewHolderImage) holder).setTxtDate(geofenceInfoContent.getTimestamp());
                ((HistoryViewHolderImage) holder).setTxtCaption(geofenceInfoContent.getCaption());
                ((HistoryViewHolderImage) holder).setTxtDescription(geofenceInfoContent.getDesc());
                ((HistoryViewHolderImage) holder).setExpanded(geofenceInfoContent.isExpanded());
                ImageView imgExpanded = ((HistoryViewHolderImage) holder).getIconExpand();
                imgExpanded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("ClickListener", "item clicked!");
                        geofenceInfoContent.setExpanded(!geofenceInfoContent.isExpanded());
                        ((HistoryViewHolderImage) holder).setExpanded(geofenceInfoContent.isExpanded());
                        ((HistoryViewHolderImage) holder).setIconExpand(context);
                    }
                });
            }
        }

    }

    /**
     * returns the number of items in the historyList
     * @return
     */
    @Override
    public int getItemCount() {
        if(historyList != null) {
            return historyList.length;
        }else{
            return 0;
        }
    }

    public GeofenceInfoContent[] getHistoryList(){
        return this.historyList;
    }


    /**
     * A ViewHolder for views that contain only an image and date
     */
    public static class HistoryViewHolderImage extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtDate;
        private ImageView imgMedia;
        private TextView txtCaption;
        private ImageView iconExpand;
        private TextView txtDescription;
        private boolean expanded = false;

        public HistoryViewHolderImage(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            imgMedia = (ImageView) itemView.findViewById(R.id.img_history_info_image);
            txtCaption = (TextView) itemView.findViewById(R.id.txt_caption);
            iconExpand = (ImageView) itemView.findViewById(R.id.img_expand);
            txtDescription = (TextView) itemView.findViewById(R.id.txt_image_description);


        }


        public String getTxtDescription() {
            return txtDescription.getText().toString();
        }

        public void setTxtDescription(String txtDescription) {
            this.txtDescription.setText(txtDescription);
        }


        public String getTxtDate() {
            return txtDate.getText().toString();
        }

        public void setTxtDate(String txtDate) {
            this.txtDate.setText(txtDate);
        }

        public void setTxtCaption(String caption){
            this.txtCaption.setText(caption);
        }

        public void setExpanded(boolean expanded){
            this.expanded = expanded;
        }

        public void swapExpanded(){
            this.expanded = !this.expanded;
        }

        public ImageView getIconExpand(){
            return this.iconExpand;
        }


        public void setIconExpand(Context context){
            if(expanded){
                iconExpand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_navigation_expand_less));
                txtDescription.setVisibility(View.VISIBLE);
            }else{
                iconExpand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_navigation_expand_more));
                txtDescription.setVisibility(View.GONE);
            }
        }


        /**
         */
        public void setImage(int resId, String encodedImage, int screenWidth, int screenHeight) {
            System.gc();
            int w = 10, h = 10;

            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap mPlaceHolderBitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap


            if (cancelPotentialWork(resId, imgMedia)) {

                //TODO: find better formula than dividing by 2
                final BitmapWorkerTask task = new BitmapWorkerTask(imgMedia,  encodedImage
                        , screenWidth/2, screenHeight/3);
                final BitmapWorkerTask.AsyncDrawable asyncDrawable =
                        new BitmapWorkerTask.AsyncDrawable(mPlaceHolderBitmap, task);
                imgMedia.setImageDrawable(asyncDrawable);
                task.execute(resId);
            }

        }

        public static boolean cancelPotentialWork(int data, ImageView imageView) {
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (bitmapWorkerTask != null) {
                final int bitmapData = bitmapWorkerTask.data;
                // If bitmapData is not yet set or it differs from the new data
                if (bitmapData == 0 || bitmapData != data) {
                    // Cancel previous task
                    bitmapWorkerTask.cancel(true);
                } else {
                    // The same work is already in progress
                    return false;
                }
            }
            // No task associated with the ImageView, or an existing task was cancelled
            return true;
        }


        private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
            if (imageView != null) {
                final Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BitmapWorkerTask.AsyncDrawable) {
                    final BitmapWorkerTask.AsyncDrawable asyncDrawable = (BitmapWorkerTask.AsyncDrawable) drawable;
                    return asyncDrawable.getBitmapWorkerTask();
                }
            }
            return null;
        }

        @Override
        public void onClick(View v) {
            swapExpanded();
        }
    }


    /**
     * A ViewHolder for views that contain only a text description and date
     */

    public static class HistoryViewHolderText extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtSummary;
        private TextView txtDate;
        private ImageView iconExpand;
        private TextView txtDescription;
        private boolean expanded = false;

        public HistoryViewHolderText(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            txtSummary = (TextView) itemView.findViewById(R.id.txt_txt_summary);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            iconExpand = (ImageView) itemView.findViewById(R.id.img_expand);
            txtDescription = (TextView) itemView.findViewById(R.id.txt_txt_description);


        }

        public ImageView getIconExpand(){
            return this.iconExpand;
        }

        public String getTxtDescription() {
            return txtDescription.getText().toString();
        }

        public void setTxtDescription(String txtDescription) {
            this.txtDescription.setText(txtDescription);
        }

        public String getTxtSummary() {
            return txtSummary.getText().toString();
        }

        public void setTxtSummary(String txtSummary) {
            this.txtSummary.setText(txtSummary);
        }

        public String getTxtDate() {
            return txtDate.getText().toString();
        }

        public void setTxtDate(String txtDate) {
            this.txtDate.setText(txtDate);
        }


        public void setIconExpand(Context context){
            if(expanded){
                txtDescription.setVisibility(View.VISIBLE);
                iconExpand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_navigation_expand_less));
            }else{
                iconExpand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_navigation_expand_more));
                txtDescription.setVisibility(View.GONE);
            }
        }

        public void setExpanded(boolean expanded){
            this.expanded = expanded;
        }

        public void swapExpanded(){
            this.expanded = !expanded;
        }

        @Override
        public void onClick(View v) {
            swapExpanded();
        }
    }


}
