package carleton150.edu.carleton.carleton150.Adapters;

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
import carleton150.edu.carleton.carleton150.POJO.HistoryContentObjectDummy;
import carleton150.edu.carleton.carleton150.R;

/**
 * Created by haleyhinze on 1/26/16.
 */
public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<HistoryContentObjectDummy> historyList = null;
    public static RecyclerViewClickListener clickListener;

    public HistoryAdapter(ArrayList<HistoryContentObjectDummy> historyList, RecyclerViewClickListener clickListener) {
        this.historyList = historyList;
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return historyList.get(position).getType();
    }


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

    @Override
    public int getItemCount() {
        if(historyList != null) {
            return historyList.size();
        }else{
            return 0;
        }
    }

    public void updateHistoryList(ArrayList<HistoryContentObjectDummy> newInfo){
        if(historyList != null){
            for(int i = 0; i<historyList.size(); i++){
                removeItem(0);
            }
        }else{
            historyList = new ArrayList<>();
        }
        for(int i = 0; i<newInfo.size(); i++){
            addItem(newInfo.get(i), i);
        }

    }

    public void removeItem(int position) {
        historyList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(HistoryContentObjectDummy content, int position) {
        historyList.add(position, content);
        notifyItemInserted(position);
    }

    public ArrayList<HistoryContentObjectDummy> getHistoryList(){
        return this.historyList;
    }


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
         * @param width
         */
        public void setWidth(int width) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
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


        /**
         * @param width
         */
        public void setWidth(int width) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
        }

        @Override
        public void onClick(View v) {
            clickListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }


}
