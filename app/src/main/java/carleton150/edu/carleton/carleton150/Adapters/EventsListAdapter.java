package carleton150.edu.carleton.carleton150.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import carleton150.edu.carleton.carleton150.POJO.EventObject.EventContent;
import carleton150.edu.carleton.carleton150.R;

/**
 * Adapter for the events list view
 */
public class EventsListAdapter extends BaseAdapter {

    private List<EventContent> events;
    private Context context;
    private ViewHolder activeHolder = null;
    private LayoutInflater layoutInflater;

    static class ViewHolder{
        TextView txtTitle;
        TextView txtLocation;
        TextView txtDate;
        TextView txtDescription;
        View view;
    }

    public EventsListAdapter(List<EventContent> events, Context context, LayoutInflater layoutInflater){
        this.events = events;
        this.context = context;
        this.layoutInflater = layoutInflater;
    }


    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (events.get(position).isExpanded()) {
            return 1;
        }
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // View it's trying to recycle (convertView), viewHolder (for the convertView), and event it's a view for
        // LayoutInflater mInflater;
        final ViewHolder holder;
        if(convertView == null){
            //mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // mInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_item_event, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txt_title);
            holder.txtLocation = (TextView) convertView.findViewById(R.id.txt_location);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.txt_description);
            holder.view = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final EventContent event = events.get(position);
        //final ViewHolder holder = (ViewHolder) v.getTag();
        if(event != null){
            holder.txtTitle.setText(event.getTitle());
            holder.txtDate.setText(event.getStartTime());
            holder.txtLocation.setText(event.getLocation());
            holder.txtDescription.setText(event.getDescription());
        }

        if (event.isExpanded()) {
            holder.txtDescription.setVisibility(View.VISIBLE);
            holder.view.setBackgroundColor(Color.parseColor("#c8bc9d"));
            event.setIsExpanded(true);
        } else {
            holder.txtDescription.setVisibility(View.GONE);
            holder.view.setBackgroundColor(Color.parseColor("#e4decf"));
            event.setIsExpanded(false);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!event.isExpanded()) {
                    holder.txtDescription.setVisibility(View.VISIBLE);
                    holder.view.setBackgroundColor(Color.parseColor("#c8bc9d"));
                    event.setIsExpanded(true);
                } else {
                    holder.txtDescription.setVisibility(View.GONE);
                    holder.view.setBackgroundColor(Color.parseColor("#e4decf"));
                    event.setIsExpanded(false);
                }
                /*if (activeHolder == null) {
                    activeHolder = holder;
                    holder.txtDescription.setVisibility(View.VISIBLE);
                    holder.view.setBackgroundColor(Color.parseColor("#c8bc9d"));
                    System.out.println("Got here 2");
                } else {    // Something is tapped
                    // Collapse active holder
                    activeHolder.txtDescription.setVisibility(View.GONE);
                    activeHolder.view.setBackgroundColor(Color.parseColor("#e4decf"));

                    // If new holder is not the same as before, expand new holder
                    if (activeHolder != holder) {
                        holder.txtDescription.setVisibility(View.VISIBLE);
                        holder.view.setBackgroundColor(Color.parseColor("#c8bc9d"));
                        activeHolder = holder;
                    } else {
                        activeHolder = null;
                    }
                }*/

/*                if(activeHolder != null) {
                    activeHolder.txtDescription.setVisibility(View.GONE);
                    activeHolder.view.setBackgroundColor(Color.parseColor("#e4decf"));
                }
                if(activeHolder != holder) {
                    holder.txtDescription.setVisibility(View.VISIBLE);
                    holder.view.setBackgroundColor(Color.parseColor("#c8bc9d"));
                    activeHolder = holder;
                }else{
                    activeHolder = null;
                }*/
            }
        });

        return convertView;
    }
}
