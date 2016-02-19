package carleton150.edu.carleton.carleton150.Adapters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.Models.BitmapWorkerTask;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.R;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;


/**
 * Adapter for the RecyclerView in the QuestFragment
 */
public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    private ArrayList<Quest> questList = new ArrayList<>();
    public static RecyclerViewClickListener clickListener;
    private int screenWidth;
    private int screenHeight;
    private View itemView;

    public QuestAdapter(ArrayList<Quest> questList, RecyclerViewClickListener clickListener, int screenWidth, int screenHeight) {
        this.questList = questList;
        this.clickListener = clickListener;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public QuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.quest_card, parent, false);
        return new QuestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuestViewHolder holder, int position) {
        Quest qi = questList.get(position);
        holder.setTitle(qi.getName());
        holder.setWidth((int) (screenWidth));
        holder.setDescription(qi.getDesc());
        holder.setLayoutRatings(4);

        holder.setImage(position, qi.getImage(), screenWidth, screenHeight);

    }

    @Override
    public int getItemCount() {
        if(questList != null) {
            return questList.size();
        }else{
            return 0;
        }
    }

    public void updateQuestList(ArrayList<Quest> newQuests){
        questList = newQuests;
    }

    public void removeItem(int position) {
        questList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(Quest quest, int position) {
        questList.add(position, quest);
        notifyItemInserted(position);
    }

    public ArrayList<Quest> getQuestList(){
        return this.questList;
    }


    public static class QuestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView image;
        private TextView creator;
        private TextView description;
        private LinearLayout layoutRatings;
        private Button btnBeginQuest;


        public QuestViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.txtTitle);
            image = (ImageView) itemView.findViewById(R.id.img_quest);
            description = (TextView) itemView.findViewById(R.id.txt_quest_description);
            layoutRatings = (LinearLayout) itemView.findViewById(R.id.lin_layout_ratings);
            btnBeginQuest = (Button) itemView.findViewById(R.id.btn_start_quest);

            btnBeginQuest.setOnClickListener(this);
        }


        public void setDescription(String description) {
            this.description.setText(description);
        }

        public void setLayoutRatings(int ratings) {
            for(int i = 0; i < ratings; i++){
                ImageView imageView = (ImageView) this.layoutRatings.getChildAt(i);
                imageView.setImageResource(R.drawable.ic_yellow_star);
            }for(int i = ratings; i<5; i++){
                ImageView imageView = (ImageView) this.layoutRatings.getChildAt(i);
                imageView.setImageResource(R.drawable.ic_cream_star);
            }
        }

        /**
         * @return title
         */
        public String getTitle() {
            return (String) title.getText();
        }

        /**
         * @param title
         */
        public void setTitle(String title) {
            this.title.setText(title);
        }

        /**
         * @param width
         */
        public void setWidth(int width) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
        }

        /**
         */
        public void setImage(int resId, String encodedImage, int screenWidth, int screenHeight) {
            System.gc();
            if(encodedImage == null) {
                image.setImageResource(R.drawable.test_image1);
                image.setColorFilter(R.color.blackSemiTransparent);
            }else {


                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

                int w = 10, h = 10;

                Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                Bitmap mPlaceHolderBitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap


                if (cancelPotentialWork(resId, image)) {

                    //TODO: find better formula than dividing by 2
                    final BitmapWorkerTask task = new BitmapWorkerTask(image, encodedImage
                            , screenWidth / 2, screenHeight / 2);
                    final BitmapWorkerTask.AsyncDrawable asyncDrawable =
                            new BitmapWorkerTask.AsyncDrawable(mPlaceHolderBitmap, task);
                    image.setImageDrawable(asyncDrawable);
                    task.execute(resId);
                }

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

        /**
         * @param creator
         */
        public void setCreator(String creator) {
            this.creator.setText(creator);
        }

        @Override
        public void onClick(View v) {
            clickListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }



}
