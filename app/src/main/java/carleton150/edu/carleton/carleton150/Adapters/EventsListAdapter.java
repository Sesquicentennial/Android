/*
package carleton150.edu.carleton.carleton150.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import carleton150.edu.carleton.carleton150.POJO.EventObject.EventContent;
import carleton150.edu.carleton.carleton150.R;

*//**
 * Created by nayelymartinez on 2/4/16.
 *//*
public class EventsListAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private Map<String, List<String>> eventCollections;
    private List<EventContent> events;

    public EventsListAdapter(Activity context, List<EventContent> events){
        this.events = events;
        this.context = context;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return eventCollections.get(events.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String event = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.event);

        item.setText(event);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return eventCollections.get(events.get(groupPosition)).size();
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
        String eventName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater groupinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = groupinflater.inflate(R.layout.group_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.event);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(eventName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}*/


package carleton150.edu.carleton.carleton150.Adapters;

import android.app.Activity;
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

// Adapter for the events list view


public class EventsListAdapter extends BaseExpandableListAdapter {

    private List<EventContent> events;
    private Activity context;
    private ViewHolder activeHolder = null;
    private LayoutInflater layoutInflater;

    static class ViewHolder{
        TextView txtTitle;
        TextView txtLocation;
        TextView txtDate;
        View view;
    }

    static class ChildViewHolder {
        TextView txtDescription;
        View view;
    }

    public EventsListAdapter(Activity context, List<EventContent> events){
        this.events = events;
        this.context = context;
    }

    // Added child
    public String getChild(int groupPosition, int childPosition) {
        return events.get(groupPosition).getDescription();
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String event = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        final ChildViewHolder holder;

        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        //holder.txtDescription = (TextView) convertView.findViewById(R.id.txt_description);
        //holder.txtDescription.setText(event);

        TextView item = (TextView) convertView.findViewById(R.id.txt_description);

        item.setText(event);
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

        /*convertView.OnClickListener(new View.OnClickListener() {
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
        });*/

        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
