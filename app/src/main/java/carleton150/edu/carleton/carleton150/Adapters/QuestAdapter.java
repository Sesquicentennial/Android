package carleton150.edu.carleton.carleton150.Adapters;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
    private Resources resources;
    private SharedPreferences sharedPreferences;

    public QuestAdapter(ArrayList<Quest> questList, RecyclerViewClickListener clickListener, int screenWidth, int screenHeight, Resources resources, SharedPreferences sharedPreferences) {
        this.questList = questList;
        this.clickListener = clickListener;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.resources = resources;
        this.sharedPreferences = sharedPreferences;
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
        holder.setDifficulty(qi.getDifficulty(), resources);
        holder.setCreator(qi.getCreator());
        holder.setTargetAudience(qi.getAudience(), resources);
        holder.setImage(position, qi.getImage(), screenWidth, screenHeight);
        int cluesCompleted = sharedPreferences.getInt(qi.getName(), 0);
        float percentCompleted = 0;
        if(cluesCompleted != 0){
            percentCompleted = cluesCompleted/qi.getWaypoints().length * 100;
        }
        holder.setPercentCompleted((int) percentCompleted);

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
        private TextView txtDifficulty;
        private TextView description;
        private LinearLayout layoutRatings;
        private Button btnBeginQuest;
        private TextView intendedAudience;


        public QuestViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.txtTitle);
            image = (ImageView) itemView.findViewById(R.id.img_quest);
            description = (TextView) itemView.findViewById(R.id.txt_quest_description);
            layoutRatings = (LinearLayout) itemView.findViewById(R.id.lin_layout_ratings);
            btnBeginQuest = (Button) itemView.findViewById(R.id.btn_start_quest);
            txtDifficulty = (TextView) itemView.findViewById(R.id.txt_difficulty);
            creator = (TextView) itemView.findViewById(R.id.txt_creator);
            intendedAudience = (TextView) itemView.findViewById(R.id.txt_intended_audience);


            btnBeginQuest.setOnClickListener(this);
        }


        public void setPercentCompleted(int percentCompleted){
            if(percentCompleted == 0){
                btnBeginQuest.setText("Begin Quest");
            }else {
                btnBeginQuest.setText(percentCompleted + "% Completed");
            }
        }
        public void setDescription(String description) {
            this.description.setText(description);
        }

        public void setDifficulty(String difficulty, Resources resources) {
            int difficultyInt = 0;
            difficultyInt = Integer.parseInt(difficulty);
            String difficultyString = "No Rating";
            int colorInt = resources.getColor(R.color.windowBackground);

            if(difficultyInt == 0){
                difficultyString = "Easy";
                colorInt = resources.getColor(R.color.green);
            }if(difficultyInt == 1){
                difficultyString = "Medium";
                colorInt = resources.getColor(R.color.orange);
            }if(difficultyInt == 2){
                difficultyString = "Hard";
                colorInt = resources.getColor(R.color.red);
            }if(difficultyInt > 2){
                difficultyInt = 2;
            }
            for(int i = 0; i <= difficultyInt; i++){
                View view = this.layoutRatings.getChildAt(i);
                GradientDrawable background = (GradientDrawable) view.getBackground();
                background.setColor(colorInt);
            }for(int i = difficultyInt+1; i<3; i++){
                View view = this.layoutRatings.getChildAt(i);
                GradientDrawable background = (GradientDrawable) view.getBackground();
                background.setColor(resources.getColor(R.color.windowBackground));
            }

            txtDifficulty.setText(difficultyString);
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
            if(creator != null) {
                if(!creator.equals("")) {
                    this.creator.setVisibility(View.VISIBLE);
                    this.creator.setText(creator);
                }
                else{
                    this.creator.setVisibility(View.GONE);
                }
            }else{
                this.creator.setVisibility(View.GONE);
            }
        }

        /**
         * @param targetAudience
         */
        public void setTargetAudience(String targetAudience, Resources resources) {
            if(targetAudience != null) {
                if(!targetAudience.equals("")) {
                    this.intendedAudience.setVisibility(View.VISIBLE);
                    this.intendedAudience.setText(resources.getString(R.string.intended_for) + " " + targetAudience);
                }
                else{
                    this.intendedAudience.setVisibility(View.GONE);
                }
            }else{
                this.intendedAudience.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            clickListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }



}
