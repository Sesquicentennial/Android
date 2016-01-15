package carleton150.edu.carleton.carleton150.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import carleton150.edu.carleton.carleton150.POJO.Event;
import carleton150.edu.carleton.carleton150.POJO.EventObject.EventContent;
import carleton150.edu.carleton.carleton150.POJO.EventObject.Events;
import carleton150.edu.carleton.carleton150.R;

/**
 * Created by haleyhinze on 1/10/16.
 */
public class EventsListAdapter extends BaseAdapter {

    private List<EventContent> events;
    private Context context;
    private ViewHolder activeHolder = null;

    static class ViewHolder{
        TextView txtTitle;
        TextView txtLocation;
        TextView txtDate;
        TextView txtDescription;
    }

    public EventsListAdapter(List<EventContent> events, Context context){
        this.events = events;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater mInflater;
        if(convertView == null){
            mInflater = LayoutInflater.from(context);
            v = mInflater.inflate(R.layout.list_item_event, null);
            ViewHolder holder = new ViewHolder();
            holder.txtTitle = (TextView) v.findViewById(R.id.txt_title);
            holder.txtLocation = (TextView) v.findViewById(R.id.txt_location);
            holder.txtDate = (TextView) v.findViewById(R.id.txt_date);
            holder.txtDescription = (TextView) v.findViewById(R.id.txt_description);
            v.setTag(holder);
        }

        final EventContent event = events.get(position);
        final ViewHolder holder = (ViewHolder) v.getTag();
        if(event != null){
            holder.txtTitle.setText(event.getTitle());
            holder.txtDate.setText(event.getStartTime());
            holder.txtLocation.setText(event.getLocation());
            holder.txtDescription.setText(event.getDescription());
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activeHolder != null) {
                    activeHolder.txtDescription.setVisibility(View.GONE);
                }
                holder.txtDescription.setVisibility(View.VISIBLE);
                activeHolder = holder;
            }
        });

        return v;
    }
}
