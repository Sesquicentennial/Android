package carleton150.edu.carleton.carleton150.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.R;


/**
 * Created by haleyhinze on 12/8/15.
 */
public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    private ArrayList<Quest> questList = null;
    public static RecyclerViewClickListener clickListener;
    private int screenWidth;
    private View itemView;

    public QuestAdapter(ArrayList<Quest> questList, RecyclerViewClickListener clickListener, int screenWidth) {
        this.questList = questList;
        this.clickListener = clickListener;
        this.screenWidth = screenWidth;
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
        holder.setDescription(qi.getDesc());

        //TODO: get a good width...
        holder.setWidth((int) (screenWidth/1.5));

        //TODO:make this background come from qi
        holder.setBackground();
    }

    @Override
    public int getItemCount() {
        if(questList != null) {
            return questList.size();
        }else{
            return 0;
        }
    }

    public void updateQuests(ArrayList<Quest> newQuests){
        this.questList = newQuests;
        notifyDataSetChanged();
    }

    public ArrayList<Quest> getQuestList(){
        return this.questList;
    }


    public static class QuestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView description;
        private ImageView image;
        private TextView creator;

        public QuestViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            title = (TextView) itemView.findViewById(R.id.txtTitle);
            description = (TextView) itemView.findViewById(R.id.txtDescription);
            image = (ImageView) itemView.findViewById(R.id.img_quest);


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
         * @param description
         */
        public void setDescription(String description) {
            this.description.setText(description);
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
            image.setImageResource(R.drawable.test_image1);
            image.setColorFilter(R.color.blackSemiTransparent);

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
