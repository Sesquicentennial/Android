package carleton150.edu.carleton.carleton150.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import carleton150.edu.carleton.carleton150.POJO.EventObject.EventContent;
import carleton150.edu.carleton.carleton150.R;

/**
 * Created by haleyhinze on 1/10/16.
 */
public class EventsListAdapter extends BaseExpandableListAdapter {

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

    public EventsListAdapter(Context context, List<EventContent> events){
        this.events = events;
        this.context = context;
    }

    // Added child
    public Object getChild(int groupPosition, int childPosition) {
        return events.get(groupPosition).getDescription();
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /*public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final EventContent event = (EventContent) getChild(groupPosition, childPosition);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.event);

        final ViewHolder holder = new ViewHolder();;
        holder.txtDescription = (TextView) convertView.findViewById(R.id.txt_description);

        item.setText(event.getDescription());
        return convertView;
    }*/

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final EventContent event = (EventContent) getChild(groupPosition, childPosition);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.event);

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater groupinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = groupinflater.inflate(R.layout.child_item, null);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtTitle.setText(event.getDescription());
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public Object getGroup(int groupPosition) {
        return events.get(groupPosition);
    }

    public int getGroupCount() {
        return events.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        EventContent eventName = (EventContent) getGroup(groupPosition);
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater groupinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = groupinflater.inflate(R.layout.group_item, null);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

        TextView item = (TextView) convertView.findViewById(R.id.event);
        holder.txtTitle = (TextView) convertView.findViewById(R.id.txt_title);
        holder.txtLocation = (TextView) convertView.findViewById(R.id.txt_location);
        holder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
        holder.view = convertView;
        convertView.setTag(holder);

        final EventContent event = events.get(groupPosition);
        //final ViewHolder holder = (ViewHolder) v.getTag();
        if(event != null){
            holder.txtTitle.setText(event.getTitle());
            holder.txtDate.setText(event.getStartTime());
            holder.txtLocation.setText(event.getLocation());
        }

        if (event.isExpanded()) {
            holder.view.setBackgroundColor(Color.parseColor("#c8bc9d"));
            event.setIsExpanded(true);
        } else {
            holder.view.setBackgroundColor(Color.parseColor("#e4decf"));
            event.setIsExpanded(false);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!event.isExpanded()) {
                    holder.view.setBackgroundColor(Color.parseColor("#c8bc9d"));
                    event.setIsExpanded(true);
                } else {
                    holder.view.setBackgroundColor(Color.parseColor("#e4decf"));
                    event.setIsExpanded(false);
                }
            }
        });

        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
