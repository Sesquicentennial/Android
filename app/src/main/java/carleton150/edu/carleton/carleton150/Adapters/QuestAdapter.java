package carleton150.edu.carleton.carleton150.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import carleton150.edu.carleton.carleton150.MainFragments.QuestFragment;
import carleton150.edu.carleton.carleton150.R;


/**
 * Created by haleyhinze on 12/8/15.
 */
public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    private List<QuestInfo> questList = null;
    public static RecyclerViewClickListener clickListener;

    public QuestAdapter(List<QuestInfo> questList, RecyclerViewClickListener clickListener) {
        this.questList = questList;
        this.clickListener = clickListener;
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
        QuestInfo qi = questList.get(position);
        holder.setTitle(qi.getTitle());
        holder.setCreator(qi.getCreator());
        holder.setDescription(qi.getDescription());
        holder.setWidth(qi.getWidth());
    }

    @Override
    public int getItemCount() {
        if(questList != null) {
            return questList.size();
        }else{
            return 0;
        }
    }


    public static class QuestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView description;
        private TextView creator;

        public QuestViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.txtTitle);
            description = (TextView) itemView.findViewById(R.id.txtDescription);
            creator = (TextView) itemView.findViewById(R.id.txtCreator);
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
