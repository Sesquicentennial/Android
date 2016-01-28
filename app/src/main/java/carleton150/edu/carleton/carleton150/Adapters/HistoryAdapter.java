package carleton150.edu.carleton.carleton150.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewScrolledListener;
import carleton150.edu.carleton.carleton150.POJO.HistoryContentObjectDummy;
import carleton150.edu.carleton.carleton150.R;

/**
 * Created by haleyhinze on 1/26/16.
 *
 * Adapter for the RecyclerView in the HistoryInfoPopup
 */
public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<HistoryContentObjectDummy> historyList = null;
    public static RecyclerViewClickListener clickListener;
    public static RecyclerViewScrolledListener scrolledListener;

    public HistoryAdapter(ArrayList<HistoryContentObjectDummy> historyList,
                          RecyclerViewClickListener clickListener, RecyclerView recyclerView,
                          RecyclerViewScrolledListener scrolledListener) {
        this.historyList = historyList;
        this.clickListener = clickListener;
        this.scrolledListener = scrolledListener;
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == recyclerView.SCROLL_STATE_IDLE){
                    clearDate();
                }else {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    displayDate(lm);
                }

            }
        });
    }

    private void displayDate(LinearLayoutManager lm){
        int lastVisible = lm.findLastVisibleItemPosition();
        scrolledListener.recyclerViewScrolled(historyList.get(lastVisible).getDate());
    }

    private void clearDate(){
        scrolledListener.recyclerViewStoppedScrolling();
    }

    /**
     * Returns 0 if the object contains an image, 1 if it contains text
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return historyList.get(position).getType();
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HistoryContentObjectDummy historyContentObjectDummy = historyList.get(position);
        if(holder instanceof HistoryViewHolderText){
            ((HistoryViewHolderText) holder).setTxtMedia(historyContentObjectDummy.getText());
            ((HistoryViewHolderText) holder).setTxtDate(historyContentObjectDummy.getDate());
        }else if(holder instanceof HistoryViewHolderImage){
            ((HistoryViewHolderImage) holder).setImage(historyContentObjectDummy.getImage());
            ((HistoryViewHolderImage) holder).setTxtDate(historyContentObjectDummy.getDate());
        }
    }

    /**
     * returns the number of items in the historyList
     * @return
     */
    @Override
    public int getItemCount() {
        if(historyList != null) {
            return historyList.size();
        }else{
            return 0;
        }
    }

    public ArrayList<HistoryContentObjectDummy> getHistoryList(){
        return this.historyList;
    }


    /**
     * A ViewHolder for views that contain only an image and date
     */
    public static class HistoryViewHolderImage extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtDate;
        private ImageView imgMedia;

        public HistoryViewHolderImage(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            imgMedia = (ImageView) itemView.findViewById(R.id.img_history_info_image);


        }

        public String getTxtDate() {
            return txtDate.getText().toString();
        }

        public void setTxtDate(String txtDate) {
            this.txtDate.setText(txtDate);
        }


        /**
         */
        public void setImage(byte[] image) {
            Bitmap bMap = BitmapFactory.decodeByteArray(image, 0, image.length);
            imgMedia.setImageBitmap(bMap);

        }

        @Override
        public void onClick(View v) {
            clickListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }


    /**
     * A ViewHolder for views that contain only a text description and date
     */

    public static class HistoryViewHolderText extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtMedia;
        private TextView txtDate;

        public HistoryViewHolderText(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            txtMedia = (TextView) itemView.findViewById(R.id.txt_txt_media);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);


        }

        public TextView getTxtMedia() {
            return txtMedia;
        }

        public void setTxtMedia(String txtMedia) {
            this.txtMedia.setText(txtMedia);
        }

        public String getTxtDate() {
            return txtDate.getText().toString();
        }

        public void setTxtDate(String txtDate) {
            this.txtDate.setText(txtDate);
        }

        @Override
        public void onClick(View v) {
            clickListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }


}
