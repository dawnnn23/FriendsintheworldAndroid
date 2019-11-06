package com.example.dawn.friendsintheworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private ArrayList<String> Groups;
    private Context mContext;
    public CustomAdapter(Context context, ArrayList<String>
            Groups)
    {
        this.mContext = context;
        this.Groups = Groups;

    }
    @Override
    public int getCount() {
        return Groups.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final viewHolder holder;
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.display_groups_row, null);
            holder = new viewHolder();
            holder.group = (TextView) convertView.findViewById(R.id.t_groupName);
            convertView.setTag(holder);
        } else{
            holder = (viewHolder) convertView.getTag();
        }
        holder.group.setText(Groups.get(position));
        return convertView;
    }

    public class viewHolder {
        TextView group;
    }

}
